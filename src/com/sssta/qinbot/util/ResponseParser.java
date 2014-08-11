package com.sssta.qinbot.util;

import static com.sssta.qinbot.util.HttpHelper.PROPERTY_ACCEPT;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_ACCEPT_CHARSET;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_CONNECTION;
import static com.sssta.qinbot.util.HttpHelper.getCookie;

import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atg.taglib.json.util.JSONArray;
import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;

import com.sssta.qinbot.core.Bot;
import com.sssta.qinbot.model.Friend;
import com.sssta.qinbot.model.Group;
import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.model.VerifyCodeChecker;
import com.sun.tools.javac.util.List;

public class ResponseParser {
	//解析验证码字符串
	public static VerifyCodeChecker parseVC(String respones) throws ParseException{
		System.out.println();
		System.out.println(respones);
		Pattern pattern  = Pattern.compile("ptui_checkVC\\('([01])','(.*)','(.*)', *'(.*)'\\);");
		Matcher matcher = pattern.matcher(respones);
		if(matcher.find()){
			return new VerifyCodeChecker(matcher.group(1), matcher.group(2),matcher.group(3));
		}else{
			throw new ParseException("验证返回值解析错误", 0);
		}
	}
	
	                     
	public static String parseLogin(String response){
		System.out.println(response);
		Pattern pattern = Pattern.compile("ptuiCB\\('(.*?)', *'(.*?)', *'(.*?)', *'(.*?)', *'(.*?)', *'(.*?)'\\);");
    	Matcher matcher = pattern.matcher(response);
    	if (matcher.find()) {
			Bot.getInstance().setNikeName(matcher.group(6));
			//第二次登陆回调
			if (!matcher.group(3).equals("")) {
				HashMap<String, String> properties = new HashMap<String, String>();
				properties.put(PROPERTY_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				properties.put(PROPERTY_ACCEPT_CHARSET, "UTF-8;");
				properties.put(PROPERTY_CONNECTION, "keep-alive");
				HttpHelper.sendGet(matcher.group(3),properties);
			}
			return matcher.group(5);
		}
    	else{
    		return "登录失败";
    	}
    
	}
	
	public List<Message> parseMessages(String resultString){
		
		return null;
	}
	
	public static HashMap<String,Friend> parseFriends(String resultString){
		HashMap<String,Friend> friends = new HashMap<String, Friend>();
		try {
			JSONObject base = new JSONObject(resultString);
			if (base.optInt("retcode",-1) == 0) {
				JSONObject resultObject = base.optJSONObject("result");
				JSONArray friendsArray = resultObject.optJSONArray("friends");
				for (int i = 0; i < friendsArray.length(); i++) {
					JSONObject friendObject = friendsArray.optJSONObject(i);
					Friend friend = new Friend();
					friend.setUin(friendObject.optString("uin"));
					friend.setFriendFlag(friendObject.optInt("flag"));
					friend.setCategories(friendObject.optInt("categories"));
					friends.put(friend.getUin(), friend);
				}
				
				JSONArray infoArray = resultObject.optJSONArray("info");
				for (int i = 0; i < infoArray.length(); i++) {
					JSONObject infoObject = infoArray.optJSONObject(i);
					Friend friend = friends.get(infoObject.optString("uin"));
					friend.setFace(infoObject.optInt("face"));
					friend.setInfoFlag(infoObject.optInt("flag"));
					friend.setNickName(infoObject.optString("nick"));
				}
				
				JSONArray markNameArray = resultObject.optJSONArray("marknames");
				for (int i = 0; i < markNameArray.length(); i++) {
					JSONObject infoObject = markNameArray.optJSONObject(i);
					Friend friend = friends.get(infoObject.optString("uin"));
					friend.setMarkName(infoObject.optString("markname"));
					friend.setMarkNameType(infoObject.optInt("type"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return friends;
	}
	
	public static HashMap<String,Group> parseGroups(String resultString){
		HashMap<String,Group> groups = new HashMap<String, Group>();
		try {
			JSONObject base = new JSONObject(resultString);
			if (base.optInt("retcode",-1) == 0) {
				JSONArray groupArray = base.optJSONObject("result").optJSONArray("gnamelist");
				for (int i = 0; i < groupArray.length(); i++) {
					Group group = new Group(groupArray.optJSONObject(i));
					groups.put(group.getName(),group);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return groups;
	}
	
	
}
