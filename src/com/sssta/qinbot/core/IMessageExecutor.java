package com.sssta.qinbot.core;

import java.util.List;

import com.sssta.qinbot.core.filter.IMessageFilter;
import com.sssta.qinbot.model.Message;


public interface IMessageExecutor {
	public void start();
	public void pause();
	public void stop();
	public void restart();
	public void exec(List<Message> messages);
	public void addFilter(IMessageFilter filter);
	public IMessageFilter removeFilter(IMessageFilter filter);
	public void loadPlugins();
	public void loadFilters();
}
