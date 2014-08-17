package com.sssta.qinbot.core.plugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.util.Log;

public class HelpPlugin extends AtNamePluginBase {
	
	List<PluginBase> plugins;
	public HelpPlugin(List<PluginBase> plugins){
		this.plugins = plugins;
		name = "帮助插件";
		version = "0.1";
		descrition = "查看帮助";
	}
	@Override
	public boolean onResponse(Message message) {
			String msg = message.content.toLowerCase();
			Pattern pattern = Pattern.compile("^@((qinbot)|(亲妹子)) +help(.*)");
			Matcher matcher = pattern.matcher(msg);
			if (matcher.find()) {
				//总帮助提示
				if (matcher.group(4)==null||matcher.group(4).trim().equals("")) {
					StringBuilder sb = new StringBuilder();
					sb.append("\\\\n输入@QinBot help 插件名称 获取具体帮助\\\\n当前插件如下:\\\\n");
					for (int i = 1; i < plugins.size(); i++) {
						sb.append(plugins.get(i).name).append(" ver:").append(plugins.get(i).version).append("\\\\n");
					}
					message.reply(sb.toString());
				}else {
					String help = null;
					for (int i = 1; i < plugins.size(); i++) {
						if (plugins.get(i).name.equals(matcher.group(4).trim())) {
							PluginBase pluginBase = plugins.get(i);
							help =String.format("\\\\n简介:%s\\\\n帮助:%s\\\\n",pluginBase.descrition,pluginBase.help);
							break;
						}
					}
					if (help!=null) {
						message.reply(help);
					}else {
						message.reply("未找到"+matcher.group(4));
					}
				}
				return true;
			}
		return false;
	}
	
}
