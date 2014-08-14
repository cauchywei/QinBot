package com.sssta.qinbot.core.filter;

import java.util.List;

import com.sssta.qinbot.model.Message;

public class BlackNameListFilter implements IMessageFilter {
	@Override
	public boolean filte(Message message) {

		return false;
	}

	@Override
	public void filte(List<Message> messages) {
		
	}

}
