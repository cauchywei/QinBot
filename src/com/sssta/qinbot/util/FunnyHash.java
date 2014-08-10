package com.sssta.qinbot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class FunnyHash {
	public static String getPswHash(String psw, String uni, String vcode) {
		// 通过pass.js计算出加密后的密码p
		String p = null;
		ScriptEngineManager m = new ScriptEngineManager();
		ScriptEngine se = m.getEngineByName("javascript");

		try {
			se.eval(new FileReader(
					new File("src/com/sssta/qinbot/util/pass.js")));
			Object t = se.eval("getEncryption(\"" + psw + "\",\"" + uni
					+ "\",\"" + vcode + "\");");
			p = t.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	public static  String getNewbiHash(String ptwebqq,String uni){
		String hash = null;
		ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptEngine se = manager.getEngineByName("jruby");
	 
	    try {
			se.eval(new FileReader(
					new File("src/com/sssta/qinbot/util/hash.rb")));
		    Object t = se.eval("hash_get(\"" + uni + "\",\"" + ptwebqq+ "\");");
		    hash = t.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return hash;
	}
}
