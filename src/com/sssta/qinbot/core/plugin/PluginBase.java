package com.sssta.qinbot.core.plugin;

import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.util.Log;

public class PluginBase implements IPlugin{
	public String name;
	public String author;
	public String descrition;
	public String version;
	public String help="暂无";
	public boolean groupAcceptedEnable = true;
	public boolean friendAcceptedEnable = true;
	boolean isEnable = true;
	@Override
	public void load() {
		Log.i("LoadPligin--"+name);
	}

	@Override
	public void unLoad() {
		Log.i("UnLoadPligin--"+name);
	}

	/**
	 * 返回true代表拦截此消息
	 */
	@Override
	public boolean onMessage(Message message) {
		// TODO Auto-generated method stub
		return onResponse(message);
	}
	
	public boolean onResponse(Message message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return String.format("Plugin:{\n\tname:\n\t\t%s\n\tauthor:\n\t\t%s\n\tdescrition:\n\t\t%s\n}", name,author,descrition);
	}

	
}
