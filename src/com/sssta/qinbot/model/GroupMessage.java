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

import sun.tools.tree.BitAndExpression;
import sun.tools.tree.ThisExpression;

import com.sssta.qinbot.core.Bot;
import com.sssta.qinbot.core.Sender;
import com.sssta.qinbot.util.HttpHelper;

import atg.taglib.json.util.JSONObject;

public class GroupMessage extends Message {
	
	public String groupCode;
	public String infoSeq;
	public String sendUin;//即为from
	public String seq;
	
	private static final String GROUP_CODE = "group_code";
	private static final String INFO_SEQ = "info_seq";
	private static final String SEND_UIN = "send_uin";
	private static final String SEQ = "seq";
	
	protected static final HashMap<String, String> properties = new HashMap<String, String>();
	static{
		properties.put(PROPERTY_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		properties.put(PROPERTY_REFER, URL_REFER_POLL);
		properties.put(PROPERTY_ACCEPT,"*/*");
		properties.put(PROPERTY_ACCEPT_ENCODING, "gzip,deflate,sdch");
		properties.put(PROPERTY_CONTETN_TYPE, "application/x-www-form-urlencoded");
		properties.put(PROPERTY_CONNECTION,"keep-alive");
		properties.put(PROPERTY_ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8");
		properties.put(PROPERTY_HOST, "d.web2.qq.com");
		properties.put(PROPERTY_ORIGIN, "http://d.web2.qq.com");
	}
	
	public GroupMessage(JSONObject message) {
		super(message);
		groupCode = message.optString(GROUP_CODE);
		infoSeq = message.optString(INFO_SEQ);
		sendUin = message.optString(SEND_UIN);
		seq = message.optString(SEQ);
	}
	
	@Override
	public String getType() {
		return TYPE_GROUP;
	}
	
	@Override
	public void reply(String msg) {
		if (replyMsg== null || replyMsg.trim().equals("")) {
			Group group = Bot.getInstance().getGroups().get(from);
			Friend member  = group.getMembers().get(sendUin);
			String repliedName = member.getUin().equals(group.getOwner())?"群主大人":member.getCard();
			replyMsg = String.format("@%s %s",repliedName,msg);
			Sender.queue(this);
		}
	}

	@Override
	protected String generateReplyJson() {
		String content = String.format("{\"group_uin\":%s,\"content\":\"[\\\"%s\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":\\\"10\\\",\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"msg_id\":%d,\"clientid\":\"%s\",\"psessionid\":\"%s\"}",from,replyMsg,getMessageId(),Bot.CLIENT_ID,Bot.getInstance().getPsessionid());
		content = "r="+URLEncoder.encode(content)+"&clientid="+Bot.CLIENT_ID+"%psessionid="+Bot.getInstance().getPsessionid();
		return content;

	}

	@Override
	public void send() {
		String resultJson = HttpHelper.sendPost(HttpHelper.URL_SEND_GROUP,generateReplyJson(),properties);
		System.out.println("send to group:"+Bot.getInstance().getGroups().get(from).getName()+"--"+resultJson);
	}
	
}
