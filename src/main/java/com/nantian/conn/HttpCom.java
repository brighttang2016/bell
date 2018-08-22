package com.nantian.conn;

import java.util.HashMap;

public interface HttpCom {
	/**
	 * http通信开始
	 * @param paramMap 参数map
	 * @param encoding 编码方式
	 * @param comType 通信方式（get\post）
	 * @return
	 */
	public String CommStart(HashMap<String,String> paramMap,String encoding,String comType);
	/**
	 * get方式
	 * @param paramMap
	 * @param encoding
	 * @return
	 */
	public String commGet(HashMap<String,String> paramMap,String encoding);
	/**
	 * post方式
	 * @param paramMap
	 * @param encoding
	 * @return
	 */
	public String commPost(HashMap<String,String> paramMap,String encoding);
	public String getXmlHpptRet(String appcode,String trxcode,String xmlHttpReq,String encoding);
	public  String readUseGet(String appcode,String trxcode,String xmlHttpReq,String encoding);
	public  String readUsePost(String appcode,String trxcode,String xmlHttpReq,String encoding);
}
