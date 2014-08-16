package com.sssta.qinbot.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.sssta.qinbot.model.GroupMessage;
import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.model.NormalMessage;
import com.sssta.qinbot.util.Log;

public class ServerTaskManager {
	public static final int MAX_NUM = 15;
	public static long CLEAR_PERIOD = 30000l;
	public static long MAX_TASK_FREE_TIME = 60000;
	
	private HashMap<String, ServerTask> runningTasks = new HashMap<String, ServerTask>();
	private LinkedList<GroupServerTask> freeGroupServerTasks = new LinkedList<GroupServerTask>();
	private LinkedList<FriendServerTask> freeFriendServerTasks = new LinkedList<FriendServerTask>();
	private static ServerTaskManager manager = new ServerTaskManager();
	private TaskClearer taskClearer;
	
	private ServerTaskManager(){
		taskClearer = new TaskClearer();
		taskClearer.start();
	}
	
	public static synchronized ServerTaskManager getInstance(){
		return manager;
	}
	
	public void dispatch(Message message){
		Log.i("dispatch---"+message.content);
		try{
			if (message instanceof GroupMessage) {
				Log.i("dispatch group---");
				dispatchGroupMessage((GroupMessage) message);
			}else if (message instanceof NormalMessage) {
				Log.i("dispatch friend---");
				dispatchFriendMessage((NormalMessage) message);
			}
		}catch(InterruptedException e){
			Log.e(e.getMessage());
		}
		
	}
	
	public void dispatchGroupMessage(GroupMessage message) throws InterruptedException{
		
		ServerTask serverTask = runningTasks.get(message.getFrom());
		if (serverTask!=null && serverTask instanceof GroupServerTask) {
			serverTask.getMessageQueue().put(message);
			return;
		}
		
		if (freeGroupServerTasks.isEmpty()) {
			if (runningTasks.size() >= MAX_NUM) {
				message.reply("小Qin正忙着，稍等:-D！");
			}else {
				GroupServerTask task = new GroupServerTask();
				task.setServeUni(message.getFrom());
				task.getMessageQueue().put(message);
				task.start();
				runningTasks.put(message.from, task);
			}
		}else {
			GroupServerTask task =  freeGroupServerTasks.remove();
			task.reset(message.getFrom());
			task.notify();
			runningTasks.put(message.getFrom(), task);
		}
	}
	
	public void dispatchFriendMessage(NormalMessage message) throws InterruptedException{
		ServerTask serverTask = runningTasks.get(message.getFrom());
		if (serverTask!=null && serverTask instanceof FriendServerTask) {
			serverTask.getMessageQueue().put(message);
			return;
		}
		
		if (freeFriendServerTasks.isEmpty()) {
			if (runningTasks.size() >= MAX_NUM) {
				message.reply("小Qin正忙着，稍等:-D！");
			}else {
				FriendServerTask task = new FriendServerTask();
				task.setServeUni(message.getFrom());
				task.getMessageQueue().put(message);
				task.start();
				runningTasks.put(message.from, task);	
			}
		}else {
			FriendServerTask task =  freeFriendServerTasks.remove();
			task.setServeUni(message.getFrom());
			task.notify();
			runningTasks.put(message.getFrom(), task);
		}
	}
	
	class TaskClearer extends Thread{
		private boolean pause;
		@Override
		public void run() {
			while (true) {
				if (!pause) {
					Log.i("TaskClearing!!!");

					try {
						Set<String> keys = runningTasks.keySet();
						Iterator<String> iterator = keys.iterator();
						while (iterator.hasNext()) {
							String key = iterator.next();
							ServerTask task = runningTasks.get(key);
							if (System.currentTimeMillis() - task.getLastActiveTime() > MAX_TASK_FREE_TIME) {
								task.wait();
								runningTasks.remove(key);
								if (task instanceof FriendServerTask) {
									freeGroupServerTasks.add((GroupServerTask) task);
								}else if(task instanceof GroupServerTask){
									freeFriendServerTasks.add((FriendServerTask) task);
								}
								Log.i("TaskClearing!!!--clear "+task.getServeUni());
							}
							
						}
						
						sleep(CLEAR_PERIOD);
					} catch (InterruptedException e) {
						Log.e(e.getMessage());
					}
				}
				
			}
		}
		
	}
	
	
	
}
