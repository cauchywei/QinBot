package com.sssta.qinbot.core.plugin;

import java.util.Calendar;
import java.util.Random;

import com.sssta.qinbot.model.Message;

public class GreetingPlugin extends PluginBase{
	
	static String[] moringWords = new String[]{"早上好","moring","起床失败","早安","起床"};
	static String[] nigthWords = new String[]{"古耐","晚安","睡觉","强者早睡"};
	
	static String[] moringResponseWord = new String[]{"早上好","good moring","起床失败2333","早安","起床咯"};
	static String[] nigthResponseWords = new String[]{"古耐~","晚安~","做个好梦~","强者早睡！！！","春梦了无痕~"};
	
	public GreetingPlugin(){
		name = "问候";
		version = "0.1";
		author = "Cauchywei";
		descrition = "打造一个有礼貌的QinBot";
	}

	@Override
	public boolean onMessage(Message message) {
		for (String word :moringWords) {
			if (message.content.contains(word)) {
				if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <= 10) {
					message.reply(moringResponseWord[new Random().nextInt(moringResponseWord.length)]);
					return true;
				}
			}
		}
		for (String word :nigthWords) {
			if (message.content.contains(word)) {
				if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 21) {
					message.reply(nigthResponseWords[new Random().nextInt(nigthResponseWords.length)]);
					return true;
				}
			}
		}
		
		return false;
	}
	
}
