package com.sssta.qinbot.core;

import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.swing.JOptionPane;

import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;

import com.sssta.qinbot.event.EventCallback;
import com.sssta.qinbot.model.BotCookie;
import com.sssta.qinbot.model.BotState;
import com.sssta.qinbot.model.DiscussGroup;
import com.sssta.qinbot.model.Friend;
import com.sssta.qinbot.model.Group;
import com.sssta.qinbot.model.VerifyCodeChecker;
import com.sssta.qinbot.util.Cyrpt;
import com.sssta.qinbot.util.HttpHelper;

import static com.sssta.qinbot.util.HttpHelper.*;

import com.sssta.qinbot.util.ResponseParser;

public class Bot {
	private String qq;
	private String psw;
	private String loginSig;
	private String vcReqCode; // 获取验证码所需要的请求码
	private String verifySession;
	private String qqHex;
	private String nikeName;
	private static Bot bot = new Bot();
	private String ptwebqq;
	private String vfwebqq;
	private BotState state = BotState.OFFLINE;
	private String pollReqCache;

	public static final String CLIENT_ID = "33422818";

	private String skey;
	private String psessionid;
	
	private MessageExecutor messageManager = new MessageExecutor(this);
	
	private HashMap<String,Group> groups = new HashMap<String,Group>();
	private HashMap<String,Friend> friends = new HashMap<String,Friend>();
	private HashMap<String,DiscussGroup> discussGroups = new HashMap<String,DiscussGroup>();
	
	private Bot(){
		
	}
	

	public static Bot getInstance() {
		return bot;
	}

	public String getQQ() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getPsw() {
		return psw;
	}

	public void setPsw(String psw) {
		this.psw = psw;
	}

	public String getLoginSig() {
		return loginSig;
	}

	public void setLoginSig(String loginSig) {
		this.loginSig = loginSig;
	}

	public void attachChecker(VerifyCodeChecker checker) {
		vcReqCode = checker.getReqCode();
		qqHex = checker.getQqHex();
	}

	public String getQqHex() {
		return qqHex;
	}

	public String getVcReqCode() {
		return vcReqCode;
	}

	public String getVerifySession() {
		return verifySession;
	}

	public void setVerifySession(String verifySession) {
		this.verifySession = verifySession;
	}

	public String getNikeName() {
		return nikeName;
	}

	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}

	public String getPtwebqq() {
		return ptwebqq;
	}

	public void setPtwebqq(String ptwebqq) {
		this.ptwebqq = ptwebqq;
	}

	public String getVfwebqq() {
		return vfwebqq;
	}

	public void setVfwebqq(String vfwebqq) {
		this.vfwebqq = vfwebqq;
	}

	public String getSkey() {
		return skey;
	}

	public void setSkey(String skey) {
		this.skey = skey;
	}

	public boolean login(String vCode) {
		
		if (state == BotState.ONLINE) {
			return true;
		}else if(getVcReqCode()==null){
			return false;
		}
		
		
		String uin = getQqHex();
		String vcode = vCode!=null &&vCode.equals("") ? getVcReqCode() : vCode;
		//发起第一次登陆请求
		
		//获取密码hash码
		String p = Cyrpt.getEncryption(psw, uin, vcode);
		
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(PROPERTY_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		properties.put(PROPERTY_CONNECTION, "keep-alive");
		properties.put(PROPERTY_REFER, URL_REFER_Q);
		String resultString = sendGet(
				String.format(
						URL_FORMAT_LOGIN,
						getQQ(), 
						p,
						vcode.toUpperCase(),
						getLoginSig(),
						getVerifySession()),
				properties);
		//解析登陆请求返回的信息，若请求成功并且在里面进行第二次登陆回调来获取最后所需要的Cookie；
		String state = ResponseParser.parseLogin(resultString);
		if (state.contains("登录成功")) {
			//获取重要的两个Cookie
			setPtwebqq(getCookie("ptwebqq"));
			setSkey(getCookie("skey"));

			setState(BotState.ONLINE);
			//发起第一次Post请求，正式登陆
			String channelLoginUrl = "http://d.web2.qq.com/channel/login2";
			String content = "{\"status\":\"\",\"ptwebqq\":\"" + ptwebqq
					+ "\",\"passwd_sig\":\"\",\"clientid\":\"" + CLIENT_ID
					+ "\"}";
			content = "r=" + URLEncoder.encode(content);;// post的数据
			
			
			HashMap<String, String> propertiesPost = new HashMap<String, String>();
			propertiesPost.put(PROPERTY_ACCEPT, "*/*");
			propertiesPost.put(PROPERTY_REFER, URL_REFER_LOGIN_1);
			propertiesPost.put(PROPERTY_HOST, "d.web2.qq.com");
			propertiesPost.put(PROPERTY_ORIGIN, "http://d.web2.qq.com");
			propertiesPost.put(PROPERTY_ACCEPT_ENCODING, "gzip,deflate,sdch");
			propertiesPost.put(PROPERTY_CONNECTION, "keep-alive");
			propertiesPost.put(PROPERTY_CONTETN_TYPE, "application/x-www-form-urlencoded");
			
			String res = sendPost(channelLoginUrl, content,propertiesPost);// post
			//System.out.println("\n  " + ptwebqq + "   " + res);
			JSONObject rootObject = null;
			try {
				//抓取重要的两个值，用于发送信息
				rootObject = new JSONObject(res);
				JSONObject object = rootObject.optJSONObject("result");
				vfwebqq = object.optString("vfwebqq");
				psessionid = object.optString("psessionid");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String uinString = getUin();
			addCookie(new BotCookie("p_uin",uinString));
			addCookie(new BotCookie("pt2gguin",uinString));
			addCookie(new BotCookie("uin",uinString));
			addCookie(new BotCookie("ptui_loginuin",qq));
			
			//初始化用户，群组，讨论组列表
			initInfo();
			
			//开始轮询
			messageManager.start();
			return true;
		} else {
			JOptionPane.showMessageDialog(null, state, "警告",
					JOptionPane.WARNING_MESSAGE);
			clearCookieCache();
			return false;
		}
	}
	
	private void initInfo() {
		updateGroups();
		updateGroupsInfo();
		updateFriends();
		updateDiscussGroups();
		
		getFriendRealQQ();
	}

	

	private void getFriendRealQQ() {
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(PROPERTY_REFER,URL_REFER_GET_INFO);
		properties.put(PROPERTY_ACCEPT,"*/*");
		properties.put(PROPERTY_ACCEPT_ENCODING, "gzip,deflate,sdch");
		properties.put(PROPERTY_CONNECTION,"keep-alive");
		properties.put(PROPERTY_HOST, "s.web2.qq.com");
		properties.put(PROPERTY_ORIGIN, "http://s.web2.qq.com");
		
		
		//获取QQ好友的Q号
		Iterator<String> iterator = friends.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String content = String.format(URL_FORMAT_GET_FRIEND_QQ, key,verifySession,vfwebqq,System.currentTimeMillis());
			String resultString = sendGet(content, properties);
			System.out.println(resultString);
			try {
				JSONObject base = new JSONObject(resultString);
				if (base.optInt("retcode",-1) == 0) {
					JSONObject result = base.optJSONObject("result");
					Friend friend = friends.get(key);
					friend.setQQ(result.optString("account",friend.getUin()));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		//获取所有群组成员的Q号
		iterator = groups.keySet().iterator();
		while (iterator.hasNext()) {
			String groupUni = iterator.next();
			
			HashMap<String, Friend> members = groups.get(groupUni).getMembers();
			
			Iterator<String> memberIterator = members.keySet().iterator();
			while (memberIterator.hasNext()) {
				String key = memberIterator.next();
				//已经在好友列表的群组成员，不做重复请求
				if (friends.containsKey(key)) {
					members.get(key).setQQ(friends.get(key).getQQ());
				}else{
					String content = String.format(URL_FORMAT_GET_FRIEND_QQ, key,verifySession,vfwebqq,System.currentTimeMillis());
					String resultString = sendGet(content, properties);
					System.out.println("GroupMemberQQ--"+resultString);
					try {
						JSONObject base = new JSONObject(resultString);
						if (base.optInt("retcode",-1) == 0) {
							JSONObject result = base.optJSONObject("result");
							Friend member = members.get(key);
							member.setQQ(result.optString("account",member.getUin()));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			
			}
		}
		
	
		
	}

	private void updateDiscussGroups() {
		// TODO Auto-generated method stub
	}

	private void updateFriends() {
		
		String content = String.format("{\"h\":\"hello\",\"hash\":\"%s\",\"vfwebqq\":\"%s\"}",Cyrpt.getHash(ptwebqq, qq),vfwebqq);
		content = "r="+URLEncoder.encode(content);
		
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(PROPERTY_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		properties.put(PROPERTY_REFER,URL_REFER_GET_INFO);
		properties.put(PROPERTY_ACCEPT_ENCODING, "gzip,deflate,sdch");
		properties.put(PROPERTY_CONTETN_TYPE, "application/x-www-form-urlencoded");
		properties.put(PROPERTY_CONNECTION,"keep-alive");
		properties.put(PROPERTY_ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8");
		properties.put(PROPERTY_HOST, "s.web2.qq.com");
		properties.put(PROPERTY_ORIGIN, "http://s.web2.qq.com");

		String resultString = sendPost(HttpHelper.URL_GET_FRIENDS,content,properties);
		System.out.println("friend--"+resultString);
		
		HashMap<String, Friend> newFriends = ResponseParser.parseFriends(resultString);
		if (!newFriends.isEmpty()) {
			friends.clear();
			friends.putAll(newFriends);
		}
	}

	private void updateGroups() {
		
		String content = String.format("{\"hash\":\"%s\",\"vfwebqq\":\"%s\"}",Cyrpt.getHash(ptwebqq, qq),vfwebqq);
		content = "r="+URLEncoder.encode(content);
		
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(PROPERTY_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		properties.put(PROPERTY_REFER, URL_REFER_POLL);
		properties.put(PROPERTY_ACCEPT,"*/*");
		properties.put(PROPERTY_ACCEPT_ENCODING, "gzip,deflate,sdch");
		properties.put(PROPERTY_CONTETN_TYPE, "application/x-www-form-urlencoded");
		properties.put(PROPERTY_CONNECTION,"keep-alive");
		properties.put(PROPERTY_ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8");
		properties.put(PROPERTY_HOST, "d.web2.qq.com");
		properties.put(PROPERTY_ORIGIN, "http://d.web2.qq.com");

		String resultString = sendPost(URL_GET_INFO_GROUP,content,properties);
		HashMap<String, Group> newGroups = ResponseParser.parseGroups(resultString);
		if (!newGroups.isEmpty()) {
			groups.clear();
			groups.putAll(newGroups);
		}
		
	}
	
	private void updateGroupsInfo() {
		Set<String> unisSet = getGroups().keySet();
		Iterator<String> iterator = unisSet.iterator();
				HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(PROPERTY_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		properties.put(PROPERTY_REFER, URL_REFER_GET_GROUP_INFO);
		properties.put(PROPERTY_ACCEPT,"*/*");
		properties.put(PROPERTY_ACCEPT_ENCODING, "gzip,deflate,sdch");
		properties.put(PROPERTY_CONNECTION,"keep-alive");
		properties.put(PROPERTY_ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8");
		properties.put(PROPERTY_HOST, "s.web2.qq.com");
		
		while (iterator.hasNext()) {
			String uni = iterator.next();
			String url = String.format(URL_FORMAT_GET_GROUP_INFO, groups.get(uni).getCode(),vfwebqq,System.currentTimeMillis());
			String resultString = HttpHelper.sendGet(url,properties);
			System.out.println("groupInfo--"+resultString);
			ResponseParser.parseGroupInfo(groups.get(uni), resultString);
		}
	}


	public static void checkLogin(EventCallback event){
		
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(PROPERTY_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		properties.put(PROPERTY_REFER, URL_REFER_Q);
    	String responseString = sendGet(String.format(URL_FORMAT_CHECK,
    			Bot.getInstance().getQQ()
    			,Bot.getInstance().getLoginSig()
    			,new Random().nextDouble()),properties);
    	
        String verifyString = getCookie("ptvfsession");
        if (verifyString!=null) {
	        Bot.getInstance().setVerifySession(verifyString);
	        addCookie(new BotCookie("verifysession",verifyString));
		}
        
    	try {
			VerifyCodeChecker checker = ResponseParser.parseVC(responseString);
				if(event!=null){
					event.exec(checker.isNeed());
					Bot.getInstance().attachChecker(checker);
				}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }

	private void setState(BotState state) {
		this.state = state;
		if (state == BotState.OFFLINE) {
			pollReqCache = null;
		}
	}
	

	public String getPsessionid() {
		return psessionid;
	}

	public void setPsessionid(String psessionid) {
		this.psessionid = psessionid;
	}
	
	public String getUin(){
		StringBuffer sBuffer = new StringBuffer();
		int length = qq.length();
		sBuffer.append('o');
		for (int i = 0; i < 10-length; i++) {
			sBuffer.append('0');
		}
		sBuffer.append(qq);
		return sBuffer.toString();
		
	}
	

	public String getPollReqData(){
		if (pollReqCache == null) {
			pollReqCache =  String.format("{\"clientid\":\"%s\",\"psessionid\":\"%s\",\"key\":0,\"ids\":[]}", CLIENT_ID,psessionid);
			pollReqCache = "r="+URLEncoder.encode(pollReqCache)+"&clientid="+CLIENT_ID+"%psessionid"+psessionid;
		}
		return pollReqCache;
	}
	
	

	public HashMap<String,Group> getGroups() {
		return this.groups;
	}

	public HashMap<String,Friend> getFriends() {
		return this.friends;
	}

	public HashMap<String,DiscussGroup> getDiscussGroups() {
		return this.discussGroups;
	}


	public MessageExecutor getMessageManager() {
		return messageManager;
	}


}
