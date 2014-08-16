package com.sssta.qinbot.model;

import static com.sssta.qinbot.util.HttpHelper.PROPERTY_ACCEPT;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_ACCEPT_ENCODING;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_ACCEPT_LANGUAGE;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_CONNECTION;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_CONTETN_TYPE;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_HOST;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_ORIGIN;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_REFER;
import static com.sssta.qinbot.util.HttpHelper.URL_REFER_POLL;

import java.net.URLEncoder;
import java.util.HashMap;

import com.sssta.qinbot.core.Bot;
import com.sssta.qinbot.core.Sender;
import com.sssta.qinbot.util.HttpHelper;

import atg.taglib.json.util.JSONObject;

public class NormalMessage extends Message {

	protected static final HashMap<String, String> properties = new HashMap<String, String>();
	static{
		properties.put(PROPERTY_REFER, URL_REFER_POLL);
		properties.put(PROPERTY_ACCEPT,"*/*");
		properties.put(PROPERTY_ACCEPT_ENCODING, "gzip,deflate,sdch");
		properties.put(PROPERTY_CONTETN_TYPE, "application/x-www-form-urlencoded");
		properties.put(PROPERTY_CONNECTION,"keep-alive");
		properties.put(PROPERTY_ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8");
		properties.put(PROPERTY_HOST, "d.web2.qq.com");
		properties.put(PROPERTY_ORIGIN, "http://d.web2.qq.com");
	}
	
	
	
	public NormalMessage(){
		
	}
	public NormalMessage(JSONObject message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getType() {
		return TYPE_NORMAL;
	}

	@Override
	public void reply(String msg) {
		if (replyMsg== null || replyMsg.trim().equals("")) {
			replyMsg = msg;
			Sender.queue(this);
		}
	}

	@Override
	protected String generateReplyJson() {
		String content = String.format("{\"to\":%s,\"content\":\"[\\\"%s\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":\\\"10\\\",\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"msg_id\":%d,\"clientid\":\"%s\",\"psessionid\":\"%s\"}",from,replyMsg,getMessageId(),Bot.CLIENT_ID,Bot.getInstance().getPsessionid());
		content = "r="+URLEncoder.encode(content)+"&clientid="+Bot.CLIENT_ID+"%psessionid="+Bot.getInstance().getPsessionid();
		return content;
	}

	@Override
	public void send() {
		String resultJson = HttpHelper.sendPost(HttpHelper.URL_SEND_BUDDY,generateReplyJson(),properties);
		System.out.println("send to "+Bot.getInstance().getFriends().get(from).getNickName()+"--"+resultJson);
		
	}

	
}
