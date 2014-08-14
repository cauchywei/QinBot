package com.sssta.qinbot.model;

import java.util.ArrayList;
import java.util.HashMap;

import atg.taglib.json.util.JSONObject;

public class Group {
	private String uin;
	private String code;
	private String flag;
	private String name;
	private HashMap<String,Friend> members = new HashMap<String, Friend>();
	private String owner;

	public Group(JSONObject group) {
		flag = group.optString("flag");
		code = group.optString("code");
		uin = group.optString("gid");
		name = group.optString("name");
		System.out.println("Group--"+uin);
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uni) {
		this.uin = uni;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Friend> getMembers() {
		return members;
	}

	public void setMembers(HashMap<String, Friend> members) {
		this.members = members;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}


	
}
