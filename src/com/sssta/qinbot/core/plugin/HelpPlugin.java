package com.sssta.qinbot.core.plugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sssta.qinbot.model.Message;

public class HelpPlugin extends PluginBase {
	
	List<PluginBase> plugins;
	public HelpPlugin(List<PluginBase> plugins){
		this.plugins = plugins;
		name = "帮助插件";
		version = "0.1";
		descrition = "查看帮助";
	}
	@Override
	public boolean onMessage(Message message) {
		String msg = message.content.toLowerCase();
		Pattern pattern = Pattern.compile("^@qinbot +help(.*)");
		Matcher matcher = pattern.matcher(msg);
		if (matcher.find()) {
			//总帮助提示
			if (matcher.group(1)==null||matcher.group(1).trim().equals("")) {
				StringBuilder sb = new StringBuilder();
				sb.append("\\\\n输入@QinBot help 插件名称 获取具体帮助\\\\n当前插件如下:\\\\n");
				for (int i = 1; i < plugins.size(); i++) {
					sb.append(plugins.get(i).name).append(" ver:").append(version).append("\\\\n");
				}
				message.reply(sb.toString());
			}else {
				String help = null;
				for (int i = 1; i < plugins.size(); i++) {
					if (plugins.get(i).name.equals(matcher.group(1).trim())) {
						PluginBase pluginBase = plugins.get(i);
						help =String.format("\\\\n简介:%s\\\\n帮助:%s\\\\n",pluginBase.descrition,pluginBase.help);
						break;
					}
				}
				if (help!=null) {
					message.reply(help);
				}else {
					message.reply("未找到"+matcher.group(1));
				}
			}
			return true;
		}
		
		return false;
	}
	
}
