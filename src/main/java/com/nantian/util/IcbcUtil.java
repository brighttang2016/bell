package com.nantian.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.*;

//import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

public class IcbcUtil {
//	protected TransactionMapData tmd;
	private IcbcCalendar ic = new IcbcCalendar();
	static{
		String path = new IcbcUtil().getPath();
		PropertyConfigurator.configure(path+"/log4j.properties");
	}
	
//	public TransactionMapData getTmd() {
//		return tmd;
//	}
//
//	public void setTmd(TransactionMapData tmd) {
//		this.tmd = tmd;
//	}
	
	/**
	 * 判断给定日期：time 在index年后，是否小于或等于当前日期
	 * @param time 指定时间(格式yyyyMMdd)
	 * @param index 年份偏移量
	 * @return 
	 */
	public boolean dateCompare(String time,int index){
		boolean isBefore = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			Date dateBeore = sdf.parse(time);
			Calendar c = Calendar.getInstance();
			c.setTime(dateBeore);
//			System.out.println("给定时间年:"+c.get(1)+"|日期："+sdf.format(dateBeore));
			c.set(1, c.get(1)+index);
			Date dateAfter = c.getTime();
//			System.out.println(index+"年后的年："+c.get(1)+"|日期date："+sdf.format(dateAfter));
			Date dateNow = new Date();
			dateNow = sdf.parse(sdf.format(dateNow));
//			System.out.println("是否等于当前日期："+dateAfter.equals(dateNow));
//			System.out.println("是否在当前日期之前："+dateAfter.before(dateNow));
			if(dateAfter.equals(dateNow) || dateAfter.before(dateNow)){
				isBefore = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isBefore;
	}
	
	/**
	 * 写入错误日志
	 */
	public void error(Logger logger,Exception e){
		logger.error(e);
		StackTraceElement[]  ste = e.getStackTrace();
		for (int i = 0; i < ste.length; i++) {
			logger.error("	at "+ste[i].toString());
		}
	}
	
	public boolean isNumber(String str){
		boolean blRet = false;
		logger.info("判断数字str:"+str);
		Pattern pattern = Pattern.compile("^[1-9]+\\d*\\.{0,1}\\d*$|^[0]{1}$|^[0]{1}\\.{1}[0-9]+$");
		Matcher matcher = pattern.matcher(str);
		blRet = matcher.find();
		logger.info("判断结果:"+blRet);
		return blRet;
	}
	
	/**
	 * 日期格式化
	 * @param date 待格式化日期对象
	 * @param pattern  格式化模式
	 * @return 已格式化日期字符串
	 */
	public String timeFormate(Date date,String pattern){
		String dateStr = "";
		dateStr = new SimpleDateFormat(pattern).format(date);
		return dateStr;
	}
	
	/**
	 * 时间转换
	 * @param timeSrc 需要转换的时间
	 * @param patternIn 需要转换的时间匹配模式
	 * @param patternOut 目标格式匹配模式
	 * @return 转后结果（目标时间）
	 */
	public String timeParse(String timeSrc,String patternIn,String patternOut){
		String timeRet = "";
		Date dateSrc = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdfIn = new SimpleDateFormat(patternIn);
		SimpleDateFormat sdfOut = new SimpleDateFormat(patternOut);
		try {
			dateSrc = sdfIn.parse(timeSrc);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(dateSrc);
		timeRet = sdfOut.format(dateSrc);
		return timeRet;
	}
	
	/**
	 * 字符串左补0
	 * @param str 字符串
	 * @param length 字符串左补0后总长度
	 * @return
	 */
	public String leftZero(String str,int length){
		StringBuffer sb = new StringBuffer();
		if(str != null){
			for (int j = 0; j < length - str.length(); j++) {
				sb.append("0");
			}
		}else{
			logger.error("字符串为空，左补0失败");
		}
		sb.append(str);
		return sb.toString();
	}
	
	/**
	 * 日志文件打印异常信息
	 * @param e
	 */
	public void printException(Exception e){
		StringBuffer sb = new StringBuffer();
		sb.append("抛出异常异常："+e+"\n");
		StackTraceElement[] ste = e.getStackTrace();
		for (int i = 0; i < ste.length; i++) {
			sb.append("\t"+ste[i]+"\n");
		}
		logger.error(sb.toString());
	}
	
	/**
	 * float转换百分比
	 * @param src
	 * @param pattern
	 * @return
	 */
	public String getPercentage(double src,String pattern){
		//法一：
		/*DecimalFormat df = new DecimalFormat(pattern);
		return Float.parseFloat(df.format(src))*100+"%";*/
		//法二：
		NumberFormat nf3 = NumberFormat.getPercentInstance();
		nf3.setMinimumFractionDigits(2);
		return nf3.format(src);
	}
	
	/**
	 * 返回表格中的时间转换
	 * @param tableList
	 * @param colName列名
	 * @param patternIn转换前模式
	 * @param patternOut转换后模式
	 */
	public void parseRsTime(List<Map<String,String>> tableList,String colName,String patternIn,String patternOut){
		Map<String,String> rowMap = new HashMap<String, String>();
		for (int i = 0; i < tableList.size(); i++) {
			try{
				rowMap = tableList.get(i);
				rowMap.put(colName, this.timeFormate(rowMap.get(colName), patternIn, patternOut));
			}catch(Exception e){
				logger.error(colName +"日期转换错误");
			}
		}
	}
	/**
	 * 求方运算（求a的n次方）
	 * @param a 底数
	 * @param n 指数
	 * @return 计算结果
	 */
	public float getSquare(float a,int n){
		float temp = a;
		float t = a;
		if(n == 0){
			a = 1;
		}else{
			for(int i = 2;i <= n;i ++){
				a *=t;
			}
		}
		System.out.println(temp+"的"+n+"次方为:"+a);
		return a;
	}
	
	/**
	 * 截取小数位数
	 * @param src 源字符串（可转换成浮点类型）
	 * @param n 小数位数
	 * @return 转换后的n位小数
	 */
	public float getFloatDecimal(String src,int n){
		float srcRet = 0;
		String regex = "[0-9]*\\.{1}[0-9]{"+n+"}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(src);
		if(n == 0){
			long temp = Math.round(Float.parseFloat(src));
			srcRet = temp;
		}else{
			long temp = Math.round(Float.parseFloat(src) * this.getSquare(10, n));
			System.out.println("temp:"+temp);
			srcRet = temp/this.getSquare(10, n);
		}
		System.out.println("srcRet:"+srcRet);
		return srcRet;
	}
	
	/**
	 * 时间转换
	 * @param timeSrc 需要转换的时间
	 * @param patternIn 需要转换的时间匹配模式
	 * @param patternOut 目标格式匹配模式
	 * @return 转后结果（目标时间）
	 */
	public String timeFormate(String timeSrc,String patternIn,String patternOut){
		String timeRet = "";
		Date dateSrc = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdfIn = new SimpleDateFormat(patternIn);
		SimpleDateFormat sdfOut = new SimpleDateFormat(patternOut);
		try {
			dateSrc = sdfIn.parse(timeSrc);
			calendar.setTime(dateSrc);
			timeRet = sdfOut.format(dateSrc);
		} catch (ParseException e) {
			logger.error("日期转换出错");
			//e.printStackTrace();
		}
		return timeRet;
	}
	
	/**
	 * 获取给定日期后特定天数的日期
	 * @param dateSrc 日期源
	 * @param pattern 日期匹配模式
	 * @param dayInterval 间隔天数
	 * @return 给定日期源后间隔天数后的日期时间
	 */
	public String getLaterDay(String dateSrc,String pattern,int dayInterval){
		String dateRet = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date =	null;
		try {
			date = sdf.parse(dateSrc);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(date);
		System.out.println("给定时间:"+sdf.format(calendar.getTime()));
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+dayInterval);
		System.out.println(dayInterval+"天后时间:"+sdf.format(calendar.getTime()));
		dateRet = sdf.format(calendar.getTime());
		if(dateRet == null)
			logger.error("-------------获取间隔时间出错---------------");
		return dateRet;
	}
	
	/**
	 * 判断给定日期是否到达开始时间
	 * @param time 给定字符串时间
	 * @param formate 时间转换格式
	 * @param mailBegin 提前mailBegin天触发邮件发送
	 * @param timeOut 截止日期天数
	 * @return 当前日期是否到达开始时间
	 * @throws ParseException
	 */
	public boolean timeReachStart(String time,String formate,int mailBegin,int timeOut) throws ParseException{
		Calendar cdNow = Calendar.getInstance();//当前日期
		Calendar cdBegin = Calendar.getInstance();//预定开始日期
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		Date date = sdf.parse(time);
		cdBegin.setTime(date);
		cdBegin.set(Calendar.DATE,cdBegin.get(Calendar.DATE)-mailBegin+timeOut);
		System.out.println("cdBegin:"+sdf.format(cdBegin.getTime())+" cdNow:"+sdf.format(cdNow.getTime())+" 当前日期到达开始时间:"+cdNow.after(cdBegin));//当前日期在指定日期前一天
		return cdNow.after(cdBegin);
	}
	/**
	 * 判断给定日期是否在截止日期之前
	 * @param time 指定的字符串日期
	 * @param formate 日期转换格式
	 * @param timeOut 截止日期天数
	 * @return 是否在截止日期之前
	 * @throws ParseException
	 */
	public boolean timeBeforeEnd(String time,String formate,int timeOut,HashMap dlTimeMap) throws ParseException{
		Calendar cdNow = Calendar.getInstance();//当前日期
		Calendar cdEnd = Calendar.getInstance();//预定截止日期
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		Date date = sdf.parse(time);
		cdEnd.setTime(date);
		cdEnd.set(Calendar.DATE, cdEnd.get(Calendar.DATE)+timeOut);
		dlTimeMap.put("dlTime", sdf.format(cdEnd.getTime()));
		System.out.println("cdEnd:"+sdf.format(cdEnd.getTime())+" cdNow:"+sdf.format(cdNow.getTime())+" 当前日期在截至日期内:"+cdNow.before(cdEnd));//当前日期小于指定日期
		return cdNow.before(cdEnd);
	}
	
	/**
	 * 2015-02-05
	 * 解析交易配置文件
	 * @param tranCode 交易码
	 * @return hashmap 内容：["tranName":"部门领用汇总","sqlStr":"xml配置文件sql语句"]
	 */
	public HashMap<String,String> getCfgMap(String tranCode){
		HashMap<String,String> cfgMap = new HashMap<String,String>();
		IcbcUtil iu = new IcbcUtil();
		Element targetElement = null;//配置文件目标节点
		List<Element> nodeList = this.getNodeList("consb_tran_cfg.xml", "nt", "http://www.nantian.com.cn/consb/schema/sql", "//nt:consb");
		Iterator elIt = nodeList.iterator();
		while(elIt.hasNext()){
			Element  elTemp = (Element) elIt.next();
			if(elTemp.attributeValue("id").equals(tranCode))
				targetElement = elTemp;
		}
		cfgMap.put("tranName", targetElement.attributeValue("name"));
		cfgMap.put("tranCode", targetElement.attributeValue("id"));
		Iterator celIt = targetElement.elements().iterator();//下一级子节点
		while(celIt.hasNext()){
			Element elTemp = (Element) celIt.next();
			if(elTemp.getName().equals("sql")){
				cfgMap.put("sqlStr", elTemp.getText());
				cfgMap.put("odName", elTemp.attributeValue("odName"));
				cfgMap.put("odWay", elTemp.attributeValue("odWay"));
				cfgMap.put("updFlag", elTemp.attributeValue("updFlag"));
				cfgMap.put("queryType", elTemp.attributeValue("queryType"));
				break;
			}
		}
		return cfgMap;
}
	
	/**
	 * 获取节点list
	 * @param fileName xml文件名
	 * @param prefix 命名空间前缀
	 * @param nameSpace 命名空间
	 * @param node 节点
	 * @return
	 */
	public List<Element> getNodeList(String fileName,String prefix,String nameSpace,String node){
		Document document = null;
		List<Element> nodeList = null;
		String path = "";
		SAXReader reader = new SAXReader();
		try {
			path = IcbcUtil.class.getClassLoader().getResource("").toURI().getPath().toString();
			document = reader.read(new File(path+"//"+fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, String> xmlMap = new HashMap<String, String>();
		xmlMap.put(prefix, nameSpace);
		XPath xPath = document.createXPath(node);
		xPath.setNamespaceURIs(xmlMap);
		nodeList = xPath.selectNodes(document);
		return nodeList;
	}
	
	public int haveChild(JSONArray jaCheck,JSONObject json){
		int childTag = 1;
		return childTag;
	}
	/**
	 * 空值转换
	 * @param strIn 源字符串 
	 * @param strTrg 目标字符串
	 * @return 目标字符串
	 */
	public String parseNoValue(String strIn,String strTrg){
		String strOut = "";
		if(strIn == null)
			strOut = strTrg;
		else if(strIn.trim().equals("")){
			strOut = strTrg;
		}else{
			strOut = strIn;
		}
		return strOut;
	}
	
	/**
	 * extjs日期转换
	 * @param time 2015-01-19T00:00:00
	 * return 2015-01-19
	 */
	public String parseDate(String time){
		String dateStr = "";
		String[] strArr = time.split("T");
		if(strArr.length > 1)
			dateStr = strArr[0];
		else
			dateStr = time;
		return dateStr;
	}
	
	/**
	 * 递归删除当前节点所在之路所有节点
	 * @param jaCheck 节点对象数组
	 * @param json  当前节点对象
	 */
	public void rcyDelNode(JSONArray jaCheck,JSONObject json){
		
		JSONObject jsonParent = null;
		JSONObject jsonTemp = null;
		
		
		for(int i = 0 ; i < jaCheck.size();i++){//保存父节点
			jsonTemp = (JSONObject) jaCheck.get(i);
			if(jsonTemp.get("id").equals(json.get("parent_id"))){
				jsonParent = jsonTemp;
				break;
			}
		}
		//删除子节点
		for(int i = 0;i < jaCheck.size();){
			JSONObject jsonChild = (JSONObject) jaCheck.get(i);
			if(json.get("id").equals(jsonChild.get("parent_id"))){
				logger.info("删除节点对象jsonChild："+jsonChild.toJSONString());
				System.out.println("删除节点对象jsonChild："+jsonChild.toJSONString());
				jaCheck.remove(jsonChild);
			}else{
				i++;
			}
		}
//		jaCheck.remove(json);//删除当前节点
		if(jsonParent != null)
			this.rcyDelNode(jaCheck, jsonParent);
		/*if(jsonTemp != null){
			for(int i = 0 ; i < jaCheck.size();i++){
				JSONObject jsonChild = (JSONObject) jaCheck.get(i);
				if(jsonTemp.get("id").equals(jsonChild.get("parent_id"))){
					jaCheck.remove(jsonChild);
					logger.info("移除jaCheck节点："+jsonChild.toJSONString());
				}
			}
			this.rcyDelNode(jaCheck, jsonTemp);
		}*/
	}
	
	public void parJsonArr(JSONArray ja,JSONArray jaCheck){
		JSONArray jsonParArr = new JSONArray();//存在子节点的节点数组
		for(int i = 0 ; i < jaCheck.size();i++){
			JSONObject jsonParent = (JSONObject) jaCheck.get(i);
//			logger.info("jsonParent:"+jsonParent);
			int existFlag = 0;
			for(int j = 0; j < jaCheck.size();j++){
				JSONObject jsonChild = (JSONObject) jaCheck.get(j);
				if(jsonChild.get("parent_id").equals(jsonParent.get("id"))){
					jsonParArr.add(jsonParent);
					break;
				}
			}
//			logger.info("jsonParArr.size():"+jsonParArr.size());
		}
		this.traversalCheckArr2(ja, jaCheck, jsonParArr);
	}
	
	/**
	 * 遍历JSONArray,生成json树结构
	 * @param ja json对象数组  
	 */
//	public void traversalCheckArr2(JSONArray ja,JSONArray jaCheck){
	
	public void traversalCheckArr2(JSONArray ja,JSONArray jaCheck,JSONArray jsonParArr){
		int childExist = 0;//子节点存在标志（递归标识）
		JSONObject jsonTemp = null;//首先过滤为存在子节点的节点
		
		
		boolean checkFlag = true;
		
		logger.info("jsonParArr.size():"+jsonParArr.size());
		for(int i = 0 ; i < jsonParArr.size();i++){//jsonParArr肯定存在子节点
			int childTag = 1;//1：全为叶子  其他：存在非叶子节点
			JSONObject jsonParent = (JSONObject) jsonParArr.get(i);
			logger.info("jsonParent.toJSONString():"+jsonParent.toJSONString());
			for(int j = 0; j < jaCheck.size();j++){
				JSONObject jsonChild2 = (JSONObject) jaCheck.get(j);
//				logger.info("jsonChild2.toJSONString():"+jsonChild2.toJSONString());
				JSONObject jsonChild = new JSONObject();
//				System.out.println("11111111111");
				if(jsonChild2.get("parent_id").equals(jsonParent.get("id"))){//其中的一个子节点
					jsonChild = jsonChild2;
					logger.info("jsonChild.toJSONString():"+jsonChild.toJSONString());
//					System.out.println("222222222");
				}
//				System.out.println("3333333333");
				
				if(jsonChild.get("id") != null ){//子节点存在
//					childTag = 1;
					for(int k = 0; k < jaCheck.size();k++){//循环判断是否存在孙辈节点
						JSONObject granChild = (JSONObject) jaCheck.get(k);
						if(granChild.get("parent_id").equals(jsonChild.get("id"))){//存在孙辈节点
							childTag = 2;
							logger.info("granChild.toJSONString():"+granChild.toJSONString());
								break;
						}
					}
					logger.info("************childTag:"+childTag);
				}
				if(childTag == 2){
					//说明当前jsonChild存在子节点，即：jsonParent 孙子节点存在
					break;
				}
			}
			if(childTag == 1){
				jsonTemp = jsonParent;
				logger.info("找到一个全为页子节点的节点jsonTemp："+jsonTemp.toJSONString());
				break;
			}
		}/*
		for(int i = 0;i < jaCheck.size();i++){
			JSONObject jsonChild = (JSONObject) jaCheck.get(i);
			logger.info("i="+i+"  jsonChild:"+jsonChild);
			
		}*/
		
		for(int i = 0;i < jaCheck.size();i++){
			JSONObject jsonChild = (JSONObject) jaCheck.get(i);
//			logger.info("jsonTemp.get(\"id\"):"+jsonTemp.get("id"));
//			logger.info("jsonChild.get(\"parent_id\"):"+jsonChild.get("parent_id"));
//			logger.info("jsonChild.toJSONString():"+jsonChild.toJSONString());
			if(jsonTemp.get("id").equals(jsonChild.get("parent_id"))){
				if(jsonChild.get("checked").equals(false)){
					checkFlag = false;
					logger.info("节点存在没有被选中的节点jsonTemp："+jsonTemp.toJSONString()+"没有被选中的子节点之一jsonChild："+jsonChild.toJSONString());
					break;
				}
			}
		}
		if(checkFlag){//子节点全选中
			logger.info("子节点全选中");
			jsonTemp.put("checked", true);
			for(int i = 0; i < ja.size();i++){
				JSONObject jaJson = (JSONObject) ja.get(i);
				if(jaJson.get("id").equals(jsonTemp.get("id"))){
					jaJson.put("checked", true);
				}
				//删除jsonTemp所有子节点
				for(int j = 0; j < jaCheck.size();){
					JSONObject jsonChild = (JSONObject) jaCheck.get(j);
					if(jsonTemp.get("id").equals(jsonChild.get("parent_id"))){
						jaCheck.remove(jsonChild);
						logger.info("删除全选中节点 jsonChild："+jsonChild);
					}else
						j++;
				}
			}
		}else{//存在没选中子节点
//			this.rcyDelNode(jaCheck, jsonTemp);
//			jsonParArr.remove(jsonTemp);
			for(int j = 0; j < jaCheck.size();){
				JSONObject jsonChild = (JSONObject) jaCheck.get(j);
				if(jsonTemp.get("id").equals(jsonChild.get("parent_id"))){
					jaCheck.remove(jsonChild);
					logger.info("存在没选中子节点 删除子节点 jsonChild："+jsonChild);
				}else
					j++;
			}
		}
		
		System.out.println("准备再次递归 jaCheck.size():"+jaCheck.size());
		//子节点存在判断
		JSONObject jsonChildTemp = null;
		for(int i = 0;i < jaCheck.size();i ++){
			jsonChildTemp = (JSONObject) jaCheck.get(i);
			for(int j = 0 ; j < jaCheck.size(); j++){
				JSONObject jsonParentTemp = (JSONObject) jaCheck.get(j);
				if(jsonChildTemp.get("parent_id").equals(jsonParentTemp.get("id"))){
					childExist = 1;
				}
			}
		}
		if(childExist == 1)
//			this.traversalCheckArr2(ja, jaCheck, jsonParArr);
			this.parJsonArr(ja, jaCheck);
	}
	
	/**
	 * 遍历JSONArray,生成json树结构
	 * @param ja json对象数组  
	 */
	public void traversalCheckArr(JSONArray ja,JSONArray jaCheck){
		int childExist = 0;//子节点存在标志（递归标识）
		
		JSONObject jsonChildTemp = null;
		for(int i = 0;i < jaCheck.size();i ++){
			jsonChildTemp = (JSONObject) jaCheck.get(i);
			for(int j = 0 ; j < jaCheck.size(); j++){
				JSONObject jsonParentTemp = (JSONObject) jaCheck.get(j);
				if(jsonChildTemp.get("parent_id").equals(jsonParentTemp.get("id"))){
					childExist = 1;
				}
			}
		}
		logger.info("childExist:"+childExist);
		if(childExist == 1){//jaCheck中存在叶子节点
			for(int i = 0;i < jaCheck.size();i++){
				JSONObject jsonTemp = (JSONObject) jaCheck.get(i);
				logger.info("ggggggjsonTemp:"+jsonTemp);
				String id = (String) jsonTemp.get("id");
				int childTag = 0;//是否是叶子节点（1：叶子 1：非叶子）
				for(int j = 0;j < jaCheck.size();j++){
					JSONObject jsonChild = (JSONObject) jaCheck.get(j);
					if(jsonChild.get("parent_id").equals(id)){
						childTag = 1;
					}
				}
				logger.info("childTag:"+childTag);
				if(childTag == 0){//jsonTemp为叶子节点
					logger.info("找到叶子节点 jsonTemp"+jsonTemp.toJSONString());
					boolean parentCheck = true;//父节点选中标识
					int pCheckTag = 0;
					logger.info("parentCheck1:"+parentCheck);
					for(int j = 0;j < jaCheck.size();j++){//遍历寻找jsonTemp父节点
						JSONObject jsonBro = (JSONObject) jaCheck.get(j);
//						&& !jsonBro.get("id").equals(jsonTemp.get("id"))
						if( jsonBro.get("parent_id").equals(jsonTemp.get("parent_id"))   ){
							logger.info("tttttchecked:"+jsonBro.get("checked"));
							logger.info(jsonBro.get("checked").equals(false));
							if(jsonBro.get("checked").equals(false)){
								logger.info("没选中 jsonBro:"+jsonBro);
								parentCheck = false;
								pCheckTag = 1;
							}else{
								logger.info("rrr选中 jsonBro:"+jsonBro);
							}
						}
					}
					logger.info("parentCheck2:"+parentCheck);
					
					//删除jsonTemp及兄弟节点
					for(int j = 0;j < ja.size();j++){//遍历寻找jsonTemp父节点
						JSONObject jsonBro = (JSONObject) ja.get(j);
						if(jsonBro.get("parent_id").equals(jsonTemp.get("parent_id"))){
							jaCheck.remove(jsonBro);//移除当前节点及兄弟节点
							logger.info("删除节点："+jsonBro);
						}
					}
					//这是ja checked
					if(parentCheck){
						logger.info("父节点选中");
						for(int j = 0; j < ja.size();j++){
							JSONObject jsonParent = (JSONObject) ja.get(j);
							if(jsonTemp.get("parent_id").equals(jsonParent.get("id"))){
								jsonParent.put("checked", parentCheck);
							}	
						}
					}
				break;
				}
			}
			this.traversalCheckArr(ja,jaCheck);
		}
	}
	
	/**
	 * 遍历JSONArray,生成json树结构
	 * @param ja json对象数组  
	 */
	public void traversalJsonArr(JSONArray ja){
		System.out.println("ja.size():"+ja.size()+"  ja.toJSONString():"+ja.toJSONString());
		int childExist = 0;//子节点存在标志（递归标识）
		for(int i = 0;i < ja.size();i++){
			JSONObject jsonTemp = (JSONObject) ja.get(i);
			String id = (String) jsonTemp.get("id");
			int childTag = 0;//是否具有叶子节点，若为叶子节点，追加到其父几点，若存在子节点，不做任何处理
			for(int j = 0;j < ja.size();j++){
				JSONObject jsonChild = (JSONObject) ja.get(j);
				if(jsonChild.get("parent_id").equals(id)){
					childTag = 1;
				}
			}
			if(childTag == 0){//jsonTemp为叶子几点
				for(int j = 0;j < ja.size();j++){//遍历寻找jsonTemp父节点
					JSONObject jsonParent = (JSONObject) ja.get(j);
					if(jsonParent.get("id").equals(jsonTemp.get("parent_id"))){
						JSONArray children = (JSONArray) jsonParent.get("children");
						children.add(jsonTemp);
						ja.remove(jsonTemp);//移除当前节点
					}
				}
			}
		}
		for(int i = 0;i < ja.size();i ++){
			JSONObject jsonChildTemp = (JSONObject) ja.get(i);
			for(int j = 0 ; j < ja.size(); j++){
				JSONObject jsonParentTemp = (JSONObject) ja.get(j);
				if(jsonChildTemp.get("parent_id").equals(jsonParentTemp.get("id"))){
					childExist = 1;
				}
			}
		}
		if(childExist == 1){//ja中存在叶子节点
			this.traversalJsonArr(ja);
		}
	}
	
	public void rmCondition(HashMap condition){
		logger.info("************清空condition***************");
		Iterator it = condition.keySet().iterator();
		if(it.hasNext()){
			try{
				String key = it.next().toString();
				condition.remove(key);
				rmCondition(condition);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 条件初始化，包含空字符串""
	 * @param condition 条件HashMap
	 * @param key map键
	 * @param value	 map值
	 */
	public void putConditionAll(HashMap<String,Object> condition,String key,Object value){
		try{
			if(value!=null){//供应商管理时，需要插入为空字符串的日期，故condition需要加入空字符串2015-04-24
				condition.put(key, value);
				logger.info("key:"+key+"|"+"value:"+value);
			}else if(value==null){
				condition.put(key, "");
			}
		}catch(Exception e){
			logger.info("key:"+key+"无数据");
		}
	}
	
	/**
	 * 条件初始化，植入所有非null和非空字符串条件
	 * @param condition 条件HashMap
	 * @param key map键
	 * @param value	map值
	 */
	public void putCondition(HashMap condition,String key,Object value){
		
		try{
			if(!value.equals("") && value!=null){//2015-04-24之前版本
				condition.put(key, value);
				logger.info("key:"+key+"|"+"value:"+value);
			}else{
				logger.info("key:"+key+"无数据");
			}
		}catch(Exception e){
			logger.info("key:"+key+"无数据");
		}
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
	 * @param str 20140710134804
	 * @return2014-07-10 13:48:04
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
	
	public String getRandom(){
		double init = Double.parseDouble(new IcbcUtil().getTime());
		String result = "0";
		IcbcRandom ir = new IcbcRandom();
		int recursionTime = 7;//递归次数,求得结果为a8
		result = ir.getRandom(init,recursionTime)*ir.getProduct(0.5,32)+"";
		result = result.substring(2, 11);
		return result;
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
	public String getYear(){
		return this.ic.getYear()+"";
	}
	/**
	 * 获取当前时间
	 * YYYYMMDDHHMMSS
	 */
	public String getTime(){
//		System.out.println("****************getTime**************");
//		logger.info("getTime:"+ic.getYear()+ic.getMonth()+ic.getDay()+ic.getHour()+ic.getMinute()+ic.getSecond());
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

