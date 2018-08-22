package com.tang.xmlopr;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class IcbcLogger{
	private static Logger logger = null;
	private IcbcLogger(){}
	public static synchronized  Logger getInstance(){
		if(logger == null){
			logger = Logger.getLogger(IcbcLogger.class);
			String path = new IcbcUtil().getPath();
			PropertyConfigurator.configure(path+"/log4j.properties");
		}
		return logger;
	}
	
}
