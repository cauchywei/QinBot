package com.sssta.qinbot.core;

import java.util.List;

import com.sssta.qinbot.core.filter.MessageFilterBase;
import com.sssta.qinbot.model.Message;


public interface IMessageExecutor {
	public void start();
	public void pause();
	public void stop();
	public void restart();
	public void exec(List<Message> messages);
	public void addFilter(MessageFilterBase filter);
	public MessageFilterBase removeFilter(MessageFilterBase filter);
	public void loadFilters();
}
