/**
 * 系统变量池类
 * 2014-11-20
 */
package com.tang.xmlopr;

import java.util.HashMap;

public class TransactionMapData {
	//TransactionMapData tmd = new TransactionMapData();
	HashMap<String, Object> map = new HashMap<String, Object>();
	/*private TransactionMapData(){}
	public static synchronized  TransactionMapData getInstance(){
		if(tmd == null){
			tmd = new TransactionMapData();
		}
		return tmd;
	}*/
	public TransactionMapData(){}
	public Object get(String key){
		return this.map.get(key);
	}
	public void put(String key,Object value){
		this.map.put(key, value);
	}
}
