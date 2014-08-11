package com.sssta.qinbot.core.filter;

import java.util.Iterator;
import java.util.List;

import com.sssta.qinbot.model.Message;

public class EmptyContentFilter implements IMessageFilter {

	@Override
	public boolean filte(Message message) {
		if (message.getContent().trim().equals("")) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public void filte(List<Message> messages) {
		Iterator<Message> iterator = messages.iterator();
		while (iterator.hasNext()) {
			Message message = iterator.next();
			if (filte(message)) {
				messages.remove(message);
			}
		}
	}

}
