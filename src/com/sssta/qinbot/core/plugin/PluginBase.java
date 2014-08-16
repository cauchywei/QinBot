package com.sssta.qinbot.core.plugin;

import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.util.Log;
import com.sun.tools.javac.resources.version;

public class PluginBase implements IPlugin{
	protected String name;
	protected String author;
	protected String descrition;
	protected String version;
	protected boolean isEnable = true;
	@Override
	public void onLoad() {
		Log.i("LoadPligin--"+name);
	}

	@Override
	public void unload() {
		Log.i("UnLoadPligin--"+name);
	}

	@Override
	public boolean onMessage(Message message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return String.format("Plugin:{\n\tname:\n\t\t%s\n\tauthor:\n\t\t%s\n\tdescrition:\n\t\t%s\n}", name,author,descrition);
	}
}
