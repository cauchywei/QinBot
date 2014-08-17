package com.sssta.qinbot.core.plugin;

import com.sssta.qinbot.model.Message;

public class AtNamePluginBase extends PluginBase{

	@Override
	public boolean onMessage(Message message) {
		if (message.isAtName()) {
			return onResponse(message);
		}else {
			return false;
		}
	}
	
	

}
