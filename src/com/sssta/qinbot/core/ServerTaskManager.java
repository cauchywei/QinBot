package com.sssta.qinbot.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.sssta.qinbot.model.GroupMessage;
import com.sssta.qinbot.model.Message;
import com.sssta.qinbot.model.NormalMessage;
import com.sssta.qinbot.util.Log;

public class ServerTaskManager{
	public static final int MAX_NUM = 15;
	public static long CLEAR_PERIOD = 30000l;
	public static long MAX_TASK_FREE_TIME = 60000;
	
	private HashMap<String, ServerTask> runningTasks = new HashMap<String, ServerTask>();
	private LinkedList<GroupServerTask> freeGroupServerTasks = new LinkedList<GroupServerTask>();
	private LinkedList<FriendServerTask> freeFriendServerTasks = new LinkedList<FriendServerTask>();
	private static ServerTaskManager manager = new ServerTaskManager();
	private TaskClearer clearer;
	private ServerTaskManager(){
		clearer = new TaskClearer();
		clearer.start();
	}
	
	public static synchronized ServerTaskManager getInstance(){
		return manager;
	}
	
	public void dispatch(Message message){
		Log.i("dispatch---"+message.content);
		try{
			if (message instanceof GroupMessage) {
				Log.i("dispatch to group");
				dispatchGroupMessage((GroupMessage) message);
			}else if (message instanceof NormalMessage) {
				Log.i("dispatch to friend");
				dispatchFriendMessage((NormalMessage) message);
			}
		}catch(InterruptedException e){
			Log.e(e.getMessage());
		}
		
	}
	
	public void dispatchGroupMessage(GroupMessage message) throws InterruptedException{
		
		ServerTask serverTask = runningTasks.get(message.getFrom());
		if (serverTask!=null && serverTask instanceof GroupServerTask) {
			Log.i("Put to running Thread");
			serverTask.getMessageQueue().put(message);
			return;
		}
		
		if (freeGroupServerTasks.isEmpty()) {
			if (runningTasks.size() >= MAX_NUM) {
				message.reply("小Qin正忙着，稍等:-D！");
			}else {
				Log.i("New Thread");
				GroupServerTask task = new GroupServerTask();
				task.setServeUni(message.getFrom());
				task.getMessageQueue().put(message);
				task.start();
				runningTasks.put(message.from, task);
			}
		}else {
			Log.i("Reuse");
			GroupServerTask task =  freeGroupServerTasks.remove();
			task.reset(message.getFrom());
			task.getMessageQueue().add(message);
			task.notify();
			runningTasks.put(message.getFrom(), task);
		}
	}
	
	public void dispatchFriendMessage(NormalMessage message) throws InterruptedException{
		ServerTask serverTask = runningTasks.get(message.getFrom());
		if (serverTask!=null && serverTask instanceof FriendServerTask) {
			Log.i("Put to running Thread");
			serverTask.getMessageQueue().put(message);
			return;
		}
		
		if (freeFriendServerTasks.isEmpty()) {
			if (runningTasks.size() >= MAX_NUM) {
				message.reply("小Qin正忙着，稍等:-D！");
			}else {
				Log.i("New Thread");
				FriendServerTask task = new FriendServerTask();
				task.setServeUni(message.getFrom());
				task.getMessageQueue().put(message);
				task.start();
				runningTasks.put(message.from, task);	
			}
		}else {
			Log.i("Reuse");
			FriendServerTask task =  freeFriendServerTasks.remove();
			task.getMessageQueue().add(message);
			task.reset(message.getFrom());
			task.getMessageQueue().add(message);
			task.notify();
			runningTasks.put(message.getFrom(), task);
		}
	}
	
	
	
	public HashMap<String, ServerTask> getRunningTasks() {
		return runningTasks;
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
								runningTasks.remove(key);
								if (task instanceof FriendServerTask) {
									freeFriendServerTasks.add((FriendServerTask) task);
								}else if(task instanceof GroupServerTask){
									freeGroupServerTasks.add((GroupServerTask) task);
								}
								Log.i("TaskClearing!!!--clear "+task.getServeUni());
							}
							
						}
						
						sleep(CLEAR_PERIOD);
					} catch (InterruptedException e) {
						//Log.e(e.getMessage());
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				
			}
		}
		
	}
	
}
