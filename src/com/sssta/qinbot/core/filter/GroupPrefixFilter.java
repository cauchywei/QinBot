package com.sssta.qinbot.core.filter;

import java.util.List;

import com.sssta.qinbot.model.GroupMessage;
import com.sssta.qinbot.model.Message;

public class GroupPrefixFilter extends MessageFilterBase {

	@Override
	public boolean filte(Message message) {
		if (message  instanceof GroupMessage) {
			
		}
		return false;
	}
}
