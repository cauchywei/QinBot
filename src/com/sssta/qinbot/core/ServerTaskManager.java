package com.sssta.qinbot.core;

import java.util.HashMap;
import java.util.LinkedList;

import com.sssta.qinbot.model.GroupMessage;
import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.model.NormalMessage;

public class ServerTaskManager {
	public static final int MAX_NUM = 15;
	private HashMap<String, ServerTask> runningTasks = new HashMap<String, ServerTask>();
	private LinkedList<GroupServerTask> freeGroupServerTasks = new LinkedList<GroupServerTask>();
	private LinkedList<FriendServerTask> freeFriendServerTasks = new LinkedList<FriendServerTask>();
	private static ServerTaskManager manager = new ServerTaskManager();
	
	private ServerTaskManager(){
		
	}
	
	public static synchronized ServerTaskManager getInstance(){
		return manager;
	}
	
	public void dispatch(Message message){
		if (message instanceof GroupMessage) {
			dispatchGroupMessage((GroupMessage) message);
		}else if (message instanceof NormalMessage) {
			dispatchFriendMessage((NormalMessage) message);
		}
		
	}
	
	public void dispatchGroupMessage(GroupMessage message){
		if (freeGroupServerTasks.isEmpty()) {
			if (runningTasks.size() >= MAX_NUM) {
				message.reply("小Qin正忙着，稍等:-D！");
			}else {
				GroupServerTask task = new GroupServerTask();
				task.setServeUni(message.getFrom());
				task.st
			}
		}else {
			GroupServerTask task =  freeGroupServerTasks.remove();
			task.setServeUni(message.getFrom());
			task.notify();
			runningTasks.put(message.getFrom(), task);
		}
	}
	
	public void dispatchFriendMessage(NormalMessage message){
		if (freeFriendServerTasks.isEmpty()) {
			if (runningTasks.size() >= MAX_NUM) {
				message.reply("小Qin正忙着，稍等:-D！");
			}else {
					
			}
		}else {
			FriendServerTask task =  freeFriendServerTasks.remove();
			task.setServeUni(message.getFrom());
			task.notify();
			runningTasks.put(message.getFrom(), task);
		}
	}
	
	
}
