package com.sssta.qinbot.core.plugin;

import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.util.Log;

public class TestPlugin extends PluginBase {

	@Override
	public boolean onMessage(Message message) {
		Log.i("Plugin "+name+" onMessage！！！--");

		if (message.content.contains("test")) {
			Log.i("Plugin "+name+" reply message:"+message.getFrom()+"-"+message.content);
			message.reply("收到！！");
			return true;
		}
		return false;
	}
	
}
