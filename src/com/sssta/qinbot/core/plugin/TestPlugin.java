package com.sssta.qinbot.core.plugin;

import java.util.List;

import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.util.Log;

public class TestPlugin extends PluginBase {
	
	public TestPlugin(){
		name = "test";
		version = "0.1";
		descrition = "供前期测试用";
		help = "发送包含test关键字的信息，Bot将会回复 收到！！";
	}

	@Override
	public boolean onResponse(Message message) {
		if (message.content.contains("test")) {
			Log.i("Plugin "+name+" reply message:"+message.getFrom()+"-"+message.content);
			message.reply("收到！！");
			return true;
		}
		return false;
	}
	
}
