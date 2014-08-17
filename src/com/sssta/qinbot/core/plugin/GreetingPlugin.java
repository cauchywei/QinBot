package com.sssta.qinbot.core.plugin;

import java.util.Calendar;
import java.util.Random;

import com.sssta.qinbot.model.Message;

public class GreetingPlugin extends PluginBase{
	
	static String[] moringWords = new String[]{"早上好","moring","起床失败","早安","起床"};
	static String[] nigthWords = new String[]{"古耐","晚安","睡觉","强者早睡","晚上好"};
	
	static String[] moringResponseWord = new String[]{"早上好","good moring","起床失败2333","早安","起床咯"};
	static String[] nigthResponseWords = new String[]{"古耐~","晚安~","做个好梦~","强者早睡！！！","春梦了无痕~","睡个好觉~"};
	
	public GreetingPlugin(){
		name = "greeting";
		version = "0.2";
		author = "Cauchywei";
		descrition = "打造一个有礼貌的QinBot";
		help = "在早上10点前发送包含早安等关键字的信息或者在晚上21点之后发送包含晚安等关键字的信息，Bot会随机回复问候语句";
	}

	@Override
	public boolean onResponse(Message message) {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hour <= 9) {
			for (String word :moringWords) {
				if (message.content.contains(word)) {
						message.reply(moringResponseWord[new Random().nextInt(moringResponseWord.length)]);
						return true;
				}
			}
		}else if (hour >= 21|| hour < 3) {
			for (String word :nigthWords) {
				if (message.content.contains(word)) {
						message.reply(nigthResponseWords[new Random().nextInt(nigthResponseWords.length)]);
						return true;
				}
			}
		}

		return false;
	}
	
}
