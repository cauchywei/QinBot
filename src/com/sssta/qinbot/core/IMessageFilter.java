package com.sssta.qinbot.core;

import com.sssta.qinbot.model.Message;
import com.sun.tools.javac.util.List;

public interface IMessageFilter {
	public void filte(List<Message> messages);
}
