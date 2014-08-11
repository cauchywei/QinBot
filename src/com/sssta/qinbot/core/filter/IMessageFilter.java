package com.sssta.qinbot.core.filter;

import java.util.List;

import com.sssta.qinbot.model.Message;

public interface IMessageFilter {
	public boolean filte(Message message);
	public void filte(List<Message> messages);
}
