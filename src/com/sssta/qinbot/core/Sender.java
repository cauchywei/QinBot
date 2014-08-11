package com.sssta.qinbot.core;

import static com.sssta.qinbot.util.HttpHelper.PROPERTY_ACCEPT;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_ACCEPT_ENCODING;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_ACCEPT_LANGUAGE;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_CONNECTION;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_CONTETN_TYPE;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_HOST;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_ORIGIN;
import static com.sssta.qinbot.util.HttpHelper.PROPERTY_REFER;
import static com.sssta.qinbot.util.HttpHelper.URL_REFER_POLL;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import com.sssta.qinbot.model.Group;
import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.util.HttpHelper;

public class Sender extends Thread {
	private boolean pause = false;
	private MessageExecutor messageExecutor;
	private LinkedBlockingQueue<Message> messageQueue;
	public Sender(MessageExecutor messageExecutor) {
		super();
		this.messageExecutor = messageExecutor;
		this.messageQueue = messageExecutor.getSendMessageQueue();
	}

	@Override
	public void destroy() {
		//TODO 
		super.destroy();
	}

	@Override
	public void run() {
		while (true) {
			if (!pause) {
				if (!messageQueue.isEmpty()) {
					messageQueue.poll().send();
				}
			}
		}
	}
	
	public void send(){
		if (Bot.getInstance().getGroups().size()>0) {
			
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
			Group group = Bot.getInstance().getGroups().entrySet().iterator().next().getValue();
			System.out.println("uin-"+group.getUin());
			System.out.println("code-"+group.getCode());
			String resultJson = HttpHelper.sendPost(HttpHelper.URL_SEND_GROUP,Bot.getInstance().getSendGrouopReqData((group.getUin())),properties);
			System.out.println("send--"+resultJson);
		}
		

	}
	
	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}
	
}
