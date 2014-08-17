package com.sssta.qinbot.core;

import com.sssta.qinbot.core.plugin.PluginBase;


public class GroupServerTask extends ServerTask {

	@Override
	public boolean specialCondiction(PluginBase plugin) {
		return plugin.groupAcceptedEnable;
	}
	
	
}
