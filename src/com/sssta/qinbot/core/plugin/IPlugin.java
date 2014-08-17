package com.sssta.qinbot.core.plugin;

import com.sssta.qinbot.model.Message;

public interface IPlugin {
	void load();
	void unLoad();
	boolean onMessage(Message message);
}
