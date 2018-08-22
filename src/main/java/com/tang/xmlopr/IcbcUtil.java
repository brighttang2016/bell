package com.tang.xmlopr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class IcbcUtil {
	
	/**
	 * 获取目标数组长度（竖线分隔后）
	 * @param dmtStr 竖线分隔上送报文
	 */
	public static int getUpdDltArrLength(String cIn){
		
		char[] charArr = cIn.toCharArray();
		int count = 0;
		for(char c:charArr){
			if(c == '|'){
				count++;
			}
		}
		count++;
		logger.info("获取上送竖线分隔报文-解析数组长度:"+count);
		return count;
	}
	
	private IcbcCalendar ic = new IcbcCalendar();
	static{
		String path = new IcbcUtil().getPath();
		PropertyConfigurator.configure(path+"/log4j.properties");
	}
	
	public String getProperty(String keyName){
		String value = "";
		Properties ppt = new Properties();
		String pptPath = new IcbcUtil().getPath();
		logger.info("IcbcUtil->getProperty pptPath:"+pptPath);
		InputStream is;
		try {
			is = new FileInputStream(new File(pptPath+"\\property.properties"));
			ppt.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		value = ppt.getProperty(keyName);
		return value;
	}
	/**
	 * 时间字符串格式化
	 */
	public String timeFormate(String str){
		//str:20140710134804
		String strRet = str;
		//logger.info("IcbcUtil->timeFormate str:"+str);
		try{
			if(str.length() == 14){
				String year = str.substring(0,4);
				String month = str.substring(4,6);
				String day = str.substring(6,8);
				if(Integer.parseInt(year) >= 2014 && Integer.parseInt(month) >0 && Integer.parseInt(month) <13 && Integer.parseInt(day) >0 && Integer.parseInt(day) < 31){
					String hour = str.substring(8,10);
					String minute = str.substring(10,12);
					String second = str.substring(12,14);
					strRet = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
				}
			}
		}catch(Exception e){
		}
		//logger.info("IcbcUtil->timeFormate strRet:"+strRet);
		return strRet;
	}
	/**
	 * 
	 * @param init 种子数
	 * @return 返回10位随机数
	 */
	public String getRandom(double init){
		String result = "0";
		IcbcRandom ir = new IcbcRandom();
		int recursionTime = 7;//递归次数,求得结果为a8
		result = ir.getRandom(init,recursionTime)*ir.getProduct(0.5,32)+"";
		result = result.substring(2, 11);
		return result;
	}
	/**
	 * 获取当前时间
	 * YYYYMMDDHHMMSS
	 */
	public String getTime(){
		return ic.getYear()+ic.getMonth()+ic.getDay()+ic.getHour()+ic.getMinute()+ic.getSecond();
	}
	/**
	 * 获取当前日期
	 * @return YYYYMMDD
	 */
	public String getDate(){
		return ic.getYear()+ic.getMonth()+ic.getDay();
	}
	/**
	 * list 转换 json 
	 * @param list
	 * @return
	 */
	public static JSONArray parseJsonArray(List list){
		JSONArray json = new JSONArray();  
        json.addAll(list); 
        return json;
	}
	
	private static  Logger logger = Logger.getLogger(IcbcUtil.class);
	/**
	 * 获取随机数
	 */
	public double getRandomNum(){
		double randomNum = 0;
		randomNum = new IcbcRandom().getRandom(Double.parseDouble(this.getTime()), 7);
		logger.info("IcbcUtil->getRandomNum randomNum:"+randomNum);
		return randomNum;
	}
	/**
	 * 获取logger对象
	 * @return Logger
	 */
	public static Logger getLogger(){
		return IcbcUtil.logger;
	}
	
	/**
	 * 获取class路径
	 * @return String
	 */
	public String getPath(){
		String path = "";
		try {
			path = IcbcUtil.class.getClassLoader().getResource("").toURI().getPath().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
	}
	/**
	 * 字符串特殊字符（空格）转十六进制
	 * @author brightang 20140522
	 * @param String
	 * @return String
	 */
	public String strToHex(String str){
		String ascii_hex = "";//字符对应的ascii十六进制String表示
		int ascii = 0;        //字符对应的ascii十进制整数标识
		StringBuffer sb = new StringBuffer();
		char[] c = str.toCharArray();
		for(int i = 0; i < c.length; i++){
			ascii = c[i];
			if(ascii < 16){
				ascii_hex = "0"+Integer.toHexString(ascii);
			}else{
				ascii_hex = Integer.toHexString(ascii);
			}
			if(ascii <= 32){
				sb.append("%"+ascii_hex);
			}else{
				sb.append(c[i]);
			}
		}
		return sb.toString();
	}
}

