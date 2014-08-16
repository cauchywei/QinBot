package com.sssta.qinbot.core.filter;

import java.util.Iterator;
import java.util.List;

import com.sssta.qinbot.model.Message;

public abstract class MessageFilterBase {
	
	public abstract boolean filte(Message message);
	
	public final void filte(List<Message> messages){
		Iterator<Message> iterator = messages.iterator();
		while (iterator.hasNext()) {
			Message message = iterator.next();
			if (filte(message)) {
				messages.remove(message);
			}
		}
	}
}
