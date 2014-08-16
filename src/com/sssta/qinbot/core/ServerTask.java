package com.sssta.qinbot.core;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.sssta.qinbot.core.plugin.PluginBase;
import com.sssta.qinbot.core.plugin.PluginThreadLocal;
import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.util.Log;

public class ServerTask extends Thread {
	public static PluginThreadLocal pluginThreadLocal = new PluginThreadLocal();
	private List<PluginBase> plugins;
	private String serveUni;
	private LinkedBlockingQueue<Message> messageQueue;
	private boolean pause = false;
	private long lastActiveTime;
	
	public  void reset(String serveUni){
		this.serveUni = serveUni;
		messageQueue.clear();
		lastActiveTime = System.currentTimeMillis();
		notify();
	}

	public ServerTask() {
		super();
		plugins = pluginThreadLocal.get();
		messageQueue = new LinkedBlockingQueue<Message>();
		lastActiveTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		while (true) {
			if (!pause) {
				try {
					onMessage(messageQueue.take());
				} catch (InterruptedException e) {
					Log.i(Thread.currentThread()+" has beed interupted");
				}
				lastActiveTime = System.currentTimeMillis();
			}
		}
	}
	
	public void onMessage(Message message){
		for (PluginBase plugin :plugins) {
			if (plugin.onMessage(message)) {
				return;
			}
		}
	}

	public String getServeUni() {
		return serveUni;
	}

	public void setServeUni(String serveUni) {
		this.serveUni = serveUni;
	}

	public LinkedBlockingQueue<Message> getMessageQueue() {
		return messageQueue;
	}

	public void setMessageQueue(LinkedBlockingQueue<Message> messageQueue) {
		this.messageQueue = messageQueue;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public long getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTime(long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}
	
	
	
	
}
