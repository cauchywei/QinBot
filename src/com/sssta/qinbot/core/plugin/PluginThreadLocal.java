package com.sssta.qinbot.core.plugin;

import java.util.LinkedList;
import java.util.List;


public class PluginThreadLocal extends ThreadLocal<List<PluginBase>> {

	@Override
	protected List<PluginBase> initialValue() {
		List<PluginBase> plugins = new LinkedList<PluginBase>();
		//TODO 添加新插件
		return plugins;
	}
	
}
