package com.sssta.qinbot.core;

import com.sssta.qinbot.core.plugin.PluginBase;

public class FriendServerTask extends ServerTask {
	@Override
	public boolean specialCondiction(PluginBase plugin) {
		return plugin.friendAcceptedEnable;
	}
}
