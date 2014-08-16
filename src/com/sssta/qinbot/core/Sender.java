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

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.sssta.qinbot.model.Group;
import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.util.HttpHelper;
import com.sun.media.jai.opimage.AddCollectionCRIF;

public class Sender extends Thread {
	private boolean pause = false;
	private MessageExecutor messageExecutor;
	private static LinkedBlockingQueue<Message> messageQueue;
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
				try {
					Message message = messageQueue.take();
					message.send();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
		
	
	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}
	
	public static void queue(Message message){
		messageQueue.add(message);
	}
	
}
