package com.sssta.qinbot.core;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.sssta.qinbot.core.filter.BlackNameListFilter;
import com.sssta.qinbot.core.filter.EmptyContentFilter;
import com.sssta.qinbot.core.filter.MessageFilterBase;
import com.sssta.qinbot.model.Message;
import com.sun.org.apache.xalan.internal.xsltc.dom.EmptyFilter;

public class MessageExecutor implements IMessageExecutor {
	private LinkedList<MessageFilterBase> filters = new LinkedList<MessageFilterBase>();
	private Poller poller;
	private Sender sender;
	private Bot bot;
	ExecutorService executor = Executors.newCachedThreadPool();
	private LinkedBlockingQueue<Message> sendMessageQueue = new LinkedBlockingQueue<Message>();
	private int messageID = 23333;

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

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public LinkedBlockingQueue<Message> getSendMessageQueue() {
		return sendMessageQueue;
	}

	public void setSendMessageQueue(LinkedBlockingQueue<Message> sendMessageQueue) {
		this.sendMessageQueue = sendMessageQueue;
	}
	
	
	@Override
	public void loadFilters(){
		filters.add(new EmptyContentFilter());
		filters.add(new BlackNameListFilter());
	}

	@Override
	public void exec(List<Message> messages) {
		
		for (MessageFilterBase filter :filters) {
				filter.filte(messages);
		}
		
		
	}

	@Override
	public void addFilter(MessageFilterBase filter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MessageFilterBase removeFilter(MessageFilterBase filter) {
		// TODO Auto-generated method stub
		return null;
	}


}
