package com.nantian.util;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesHelper {
	private String httpUrl = "";
	private String method = "";
	static Properties ppt = null;
	//静态区初始化执行一次，无法动态读取property文件的改变
		/*
		 * static{
			ppt = getPropertes();
		}*/
	public PropertiesHelper(){
		ppt = getPropertes();
	}
	
	public String getMethod(){
//		System.out.println("*********PropertiesHelper   getMethod");
		String method = ppt.getProperty("method");
		System.out.println("method:"+method);
		return method;
	}
	
	public String getHttpUrl(){
		System.out.println("*********PropertiesHelper   getHttpUrl");
		String httpUrl = ppt.getProperty("httpUrl");
		System.out.println("httpUrl:"+httpUrl);
		return httpUrl;
	}
	/**
	 * property文件读取
	 * @return Properties
	 */
	public static Properties getPropertes(){
		Properties ppt = new Properties();
		String path = "";
		try{
			path = new IcbcUtil().getPath();
			System.out.println("path:"+path);
			FileInputStream fis = new FileInputStream(path+"/property.properties");
			ppt.load(fis);
			System.out.println("读取properties文件结果：\n"+"dirver:"+ppt.getProperty("driver")+"\nurl:"+ppt.getProperty("url")+"\nuser:"+ppt.getProperty("user")+"\npassword:"+ppt.getProperty("password"));
		}catch(Exception e){
			e.printStackTrace();
		}
		return ppt;
	}
}
