package com.sssta.qinbot.core.filter;

import java.util.List;

import com.sssta.qinbot.model.Message;

public class BlackNameListFilter extends MessageFilterBase {
	@Override
	public boolean filte(Message message) {

		return false;
	}
}
