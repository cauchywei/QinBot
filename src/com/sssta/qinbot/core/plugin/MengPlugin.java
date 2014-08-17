package com.sssta.qinbot.core.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sssta.qinbot.core.Bot;
import com.sssta.qinbot.model.Group;
import com.sssta.qinbot.model.GroupMessage;
import com.sssta.qinbot.model.Message;

public class MengPlugin extends PluginBase {
	Random random = new Random(System.currentTimeMillis());
	String[] whats = new String[]{"What the F*ck？！","你在说什么...","不要装傻！！","我什么也不知道"};
	String[] agree = new String[]{"对呀对呀~","就是这样喵~"};
	String[] jiecao = new String[]{"么么哒","我要和你生猴子！！！"};
	String[] prefixName = new String[]{"现充","富贵","强者","后宫"};
	
	public MengPlugin(){
		name = "meng";
		version = "0.1";
		author = "Cauchywei";
		descrition = "卖萌新技能~";
		help = "就不告诉你";
	}
	
	@Override
	public boolean onResponse(Message message) {
		if (message.content.toLowerCase().startsWith("@QinBot")||message.content.startsWith("@亲妹子")) {
			if (message.content.contains("是不是")||message.content.contains("对不对")) {
				message.reply(agree[random.nextInt(agree.length)]);
				return true;
			}else if(message.content.contains("么么哒")){
				message.reply(jiecao[random.nextInt(jiecao.length)]);
				return true;
			}
		}
		else if (message.content.contains("在说什么")) {
			message.reply(whats[random.nextInt(whats.length)]);
			return true;
		}else if (message.content.trim().equals("亲妹子")) {
			message.reply(jiecao[random.nextInt(jiecao.length)]);
			return true;
		}else if (message.content.contains("秦")) {
			for (String prefix:prefixName) {
				if (message.content.contains(prefix)) {
					StringBuilder sBuilder = new StringBuilder();
					int times = random.nextInt(prefixName.length)+1;
					List<Integer> list = getSomeNumber(times);
					for (Integer integer:list) {
						sBuilder.append(prefixName[integer]);
					}
					
					if (message instanceof GroupMessage) {
						String nameWithPrefix = Bot.getInstance().getGroups().get(message.from).getMembers().get(((GroupMessage)message).getSendUin()).getCard();
						sBuilder.append(nameWithPrefix.substring(nameWithPrefix.length()-1, nameWithPrefix.length()));
					}
					message.reply(sBuilder.toString());
					return true;
				}
			}		
		}
		return false;
	}
	
	List<Integer> getSomeNumber(int n)
	{
		if (n<=0) {
			return new ArrayList<Integer>();
		}
		Random rd = new Random(System.currentTimeMillis());
		List<Integer> list = new ArrayList<Integer>();
			for(int i = 0; i < n; i++)  // 个数随机
			{
			    int tmpIdx = rd.nextInt(n); 
			    while( list.contains(tmpIdx) ) 
			        tmpIdx = rd.nextInt(n);   
			    list.add(tmpIdx);
			}
		Integer[] a = new Integer[n];
		list.toArray(a);
		return list;
	}
	
}
