package com.sssta.qinbot.core;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.sssta.qinbot.model.Message;

public class MessageExecutor implements IMessageExecutor {
	private Poller poller;
	private Sender sender;
	private Bot bot;
	Executor executor = Executors.newCachedThreadPool();
	private LinkedBlockingQueue<Message> sendMessageQueue = new LinkedBlockingQueue<Message>();
	private int messageID = 24220008;

	public MessageExecutor(Bot bot) {
		this.bot = bot;
		poller = new Poller(this);
		 sender = new Sender(this);
	}

	@Override
	public void start() {
		poller.start();
		sender.start();
	}
	
	@Override
	public void restart() {
		poller.setPause(false);
		sender.setPause(false);
	}

	@Override
	public void pause() {
		poller.setPause(true);
		sender.setPause(true);
	}

	@Override
	public void stop() {
		poller.stop();
		sender.stop();
	}

	public Poller getPoller() {
		return poller;
	}

	public void setPoller(Poller poller) {
		this.poller = poller;
	}

	public Sender getSender() {
		return sender;
	}

	public void setSender(Sender sender) {
		this.sender = sender;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public LinkedBlockingQueue<Message> getSendMessageQueue() {
		return sendMessageQueue;
	}

	public void setSendMessageQueue(LinkedBlockingQueue<Message> sendMessageQueue) {
		this.sendMessageQueue = sendMessageQueue;
	}


	
	

}
