package com.sssta.qinbot.core.plugin;

import java.util.LinkedList;
import java.util.List;

import sun.tools.java.Package;


public class PluginThreadLocal extends ThreadLocal<List<PluginBase>> {

	@Override
	protected List<PluginBase> initialValue() {
		List<PluginBase> plugins = new LinkedList<PluginBase>();
		
		plugins.add(new HelpPlugin(plugins));
		
		plugins.add(new TestPlugin());
		plugins.add(new GreetingPlugin());

		return plugins;
	}
	
}
