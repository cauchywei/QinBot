package com.sssta.qinbot.core.filter;

import java.util.Iterator;
import java.util.List;

import com.sssta.qinbot.model.Message;

public class EmptyContentFilter extends MessageFilterBase {

	@Override
	public boolean filte(Message message) {
		if (message.getContent().trim().equals("")) {
			return true;
		}else {
			return false;
		}
	}

	

}
