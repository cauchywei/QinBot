package com.sssta.qinbot.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

import atg.taglib.json.util.Cookie;
import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;

import com.sssta.qinbot.event.EventCallback;
import com.sssta.qinbot.model.BotCookie;
import com.sssta.qinbot.model.BotState;
import com.sssta.qinbot.model.VerifyCodeChecker;
import com.sssta.qinbot.util.FunnyHash;
import com.sssta.qinbot.util.HttpHelper;
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
	
	private Poller poller = new Poller(this);
	private Sender sender = new Sender(this);
	private int messageID = 24220008;

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
		String psw = getPsw();
		String uni = getQqHex();
		String vcode = vCode.equals("") ? getVcReqCode() : vCode;
		
		//System.out.println(psw+"   "+uni+"   "+vCode);
		//获取密码hash码
		String p = FunnyHash.getPswHash(psw, uni, vcode);		
		//发起第一次登陆请求
		String resultString = HttpHelper.sendGet(
				String.format(
						HttpHelper.URL_FORMAT_LOGIN,
						getQQ(), 
						p,
						vcode.toUpperCase(),
						getLoginSig(),
						getVerifySession()),
				HttpHelper.URL_REFER_Q);
		//解析登陆请求返回的信息，若请求成功并且在里面进行第二次登陆回调来获取最后所需要的Cookie；
		String state = ResponseParser.parseLogin(resultString);
		if (state.contains("登录成功")) {
			//获取重要的两个Cookie
			setPtwebqq(HttpHelper.getCookie("ptwebqq"));
			setSkey(HttpHelper.getCookie("skey"));

			setState(BotState.ONLINE);
			//发起第一次Post请求，正式登陆
			String channelLoginUrl = "http://d.web2.qq.com/channel/login2";
			String content = "{\"status\":\"\",\"ptwebqq\":\"" + ptwebqq
					+ "\",\"passwd_sig\":\"\",\"clientid\":\"" + CLIENT_ID
					+ "\"}";
			content = URLEncoder.encode(content);// urlencode
			content = "r=" + content;// post的数据
			String res = HttpHelper
					.sendPost(channelLoginUrl, content,
							"http://d.web2.qq.com/proxy.html?v=20110331002&callback=1&id=2");// post
			System.out.println("\n  " + ptwebqq + "   " + res);
			JSONObject rootObject = null;
			try {
				//抓取重要的两个值，用于发送信息
				rootObject = new JSONObject(res);
				JSONObject object = rootObject.optJSONObject("result");
				vfwebqq = object.optString("vfwebqq");
				psessionid = object.optString("psessionid");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String uniString = getUni();
			HttpHelper.addCookie(new BotCookie("p_uni",uniString));
			HttpHelper.addCookie(new BotCookie("pt2gguni",uniString));
			HttpHelper.addCookie(new BotCookie("uni",uniString));
			HttpHelper.addCookie(new BotCookie("ptui_loginuni",qq));
			//开始轮询
			poller.start();
			sender.start();
			return true;
		} else {
			JOptionPane.showMessageDialog(null, state, "警告",
					JOptionPane.WARNING_MESSAGE);
			HttpHelper.clearCookieCache();
			return false;
		}
	}
	
	public static void checkLogin(EventCallback event){
    	String responseString = HttpHelper.sendGet(String.format(HttpHelper.URL_FORMAT_CHECK,
    			Bot.getInstance().getQQ()
    			,Bot.getInstance().getLoginSig()
    			,new Random().nextDouble()),HttpHelper.URL_REFER_Q);
    	
        String verifyString = HttpHelper.getCookie("ptvfsession");
        if (verifyString!=null) {
	        Bot.getInstance().setVerifySession(verifyString);
	        HttpHelper.addCookie(new BotCookie("verifysession",verifyString));
		}
        
    	try {
			VerifyCodeChecker checker = ResponseParser.parseVC(responseString);
				if(event!=null){
					event.exec(checker.isNeed());
					Bot.getInstance().attachChecker(checker);
				}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private void setState(BotState state) {
		this.state = state;
		if (state == BotState.OFFLINE) {
			pollReqCache = null;
		}
	}
	
	public String getPollReq(){
		if (pollReqCache == null) {
			pollReqCache =  String.format("{\"clientid\":\"%s\",\"psessionid\":\"%s\",\"key\":0,\"ids\":[]}", CLIENT_ID,psessionid);
			pollReqCache = "r="+URLEncoder.encode(pollReqCache)+"&clientid="+CLIENT_ID+"%psessionid"+psessionid;
			//pollReqCache =  String.format("{\"clientid\":\"%s\",\"psessionid\":\"%s\",\"key\":0,\"ids\":[]}", CLIENT_ID,psessionid);
			
		}
		return pollReqCache;
	}
	
	public  String getSendReq(){
		String content = String.format("{\"group_uin\":3551973802,\"content\":\"[\\\"自动的，。。。\\\\n\\\\n\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":\\\"10\\\",\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"msg_id\":%d,\"clientid\":\"%s\",\"psessionid\":\"%s\"}",messageID++,CLIENT_ID,psessionid);
		content = "r="+URLEncoder.encode(content)+"&clientid="+CLIENT_ID+"%psessionid="+psessionid;
		return content;
	}

	public String getPsessionid() {
		return psessionid;
	}

	public void setPsessionid(String psessionid) {
		this.psessionid = psessionid;
	}
	
	public String getUni(){
		StringBuffer sBuffer = new StringBuffer();
		int length = qq.length();
		sBuffer.append('o');
		for (int i = 0; i < 10-length; i++) {
			sBuffer.append('0');
		}
		sBuffer.append(qq);
		return sBuffer.toString();
		
	}
	
}
