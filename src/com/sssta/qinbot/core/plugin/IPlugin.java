package com.sssta.qinbot.core.plugin;

import com.sssta.qinbot.model.Message;

public interface IPlugin {
	public void onLoad();
	public void unload();
	public boolean onMessage(Message message);
}
