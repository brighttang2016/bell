package com.nantian.conn;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.nantian.util.IcbcUtil;
import com.nantian.util.PropertiesHelper;



public class HttpComImpl implements HttpCom{
	String xmlHttpRet = "";
	String encoding = "UTF-8";
	public String getXmlHpptRet(String appcode,String trxcode,String xmlHttpReq,String encoding){
		if(new PropertiesHelper().getMethod().equals("get")){
			xmlHttpRet = new HttpComImpl().readUseGet(appcode,trxcode,xmlHttpReq,encoding);
		}else{
			xmlHttpRet = new HttpComImpl().readUsePost(appcode,trxcode,xmlHttpReq,encoding);
		}
		return xmlHttpRet.replaceAll("\\t","");
	}
	
	/**
	 * 2014-05-20
	 * get方式发送http请求
	 * @author brighttang
	 * @return void
	 */
	Logger logger = IcbcUtil.getLogger();
	public  String readUseGet(String appcode,String trxcode,String xmlHttpReq,String encoding){
		StringBuffer xmlHttpRet = new StringBuffer();
		Properties ppt = new PropertiesHelper().getPropertes();
		String url = new PropertiesHelper().getHttpUrl();
		url +="?appcode="+appcode+"&&trxcode="+trxcode+"&&cosp="+xmlHttpReq;
		url = new IcbcUtil().strToHex(url);//get方式需处理
		System.out.println("url:"+url);
		URL urlConnect;
		String line = "";
		String strRet = "";
		try {
			urlConnect = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlConnect.openConnection();
			connection.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),encoding));
//			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
//			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"gbk"));
			while((line = br.readLine()) != null){
				xmlHttpRet.append(line);
				logger.info("get方式->接收http返回:"+line);
				System.out.println("get方式->接收http返回:"+line);
			}
//			strRet = new String(xmlHttpRet.toString().getBytes("iso8859-1"),"utf-8");
//			strRet = new String(URLEncoder.encode(xmlHttpRet.toString()));
			strRet = xmlHttpRet.toString();
			logger.info("strRet:"+strRet);
			System.out.println("strRet:"+strRet);
			br.close();
//			byte[] buf = new byte[200];
//			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			connection.disconnect();
		} catch (Exception e) {
			System.out.println("e.getCause()"+e.getCause());
			System.out.println("e.getLocalizedMessage():"+e.getLocalizedMessage());
			System.out.println("e.getMessage():"+e.getMessage());
			
			StringBuffer sb = new StringBuffer();
			sb.append(e.getMessage()+"\n");
			logger.info("http连接异常");
			StackTraceElement[] ste = e.getStackTrace();
			for (int i = 0; i < ste.length; i++) {
				sb.append("	"+ste[i]+"\n");
			}
			System.out.println(e.getCause()+"异常信息："+sb.toString());
			
			e.printStackTrace();
		}
		return xmlHttpRet.toString();
//		return strRet;
	}
	
	/**
	 * post发送http
	 * brighttang 20150521
	 */
	public  String readUsePost(String appcode,String trxcode,String xmlHttpReq,String encoding){
		StringBuffer xmlHttpRet = new StringBuffer();
		String url = new PropertiesHelper().getHttpUrl();
		String line = "";
		String parameter = "";
		String strRet = "";
		try {
			URL urlConnect = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlConnect.openConnection();
			connection.setRequestMethod("POST");
			connection.setUseCaches(false); 
			connection.setInstanceFollowRedirects(true); 
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
			connection.setDoOutput(true);
			connection.setDoInput(true);
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			//参数设置
			parameter = "appcode="+appcode
						+"&trxcode="+trxcode
						+"&cosp="+URLEncoder.encode(xmlHttpReq,"gbk"); 
			dos.writeBytes(parameter);
			dos.flush();
			dos.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),encoding));
			while((line = br.readLine()) != null){
				xmlHttpRet.append(line);
			}
			logger.info("IcbcHttpHelper 接收http返回："+xmlHttpRet.toString());
//			strRet = new String(xmlHttpRet.toString().getBytes("utf-8"),"gbk");
			strRet = xmlHttpRet.toString();
			System.out.println("strRet:"+strRet);
			br.close();
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		return xmlHttpRet.toString();
		return strRet;
	}


	public String CommStart(HashMap<String, String> paramMap, String encoding,
			String comType) {
		// TODO Auto-generated method stub
		if(comType.equals("get")){
			xmlHttpRet = new HttpComImpl().commGet(paramMap,encoding);
		}else{
			xmlHttpRet = new HttpComImpl().commPost(paramMap,encoding);
		}
		return xmlHttpRet.replaceAll("\\t","");
	}

	public String commGet(HashMap<String, String> paramMap, String encoding) {
		StringBuffer xmlHttpRet = new StringBuffer();
		Properties ppt = new PropertiesHelper().getPropertes();
		String url = new PropertiesHelper().getHttpUrl();
		logger.info("执行commGet url:"+url);
		int firstBlood = 1;//第一个参数标识
		URL urlConnect;
		String line = "";
		String strRet = "";
		Iterator keyIt = paramMap.keySet().iterator();
		while(keyIt.hasNext()){
			String key = keyIt.next().toString();
			if(firstBlood == 1)
				url += "?"+key+"="+paramMap.get(key);
			else 
				url += "&&"+key+"="+paramMap.get(key);
			firstBlood++;
		}
		logger.info("准备http通信 url="+url);
//		url +="?appcode="+appcode+"&&trxcode="+trxcode+"&&cosp="+xmlHttpReq;
		url = new IcbcUtil().strToHex(url);//get方式需处理
		logger.info("中文转十六进制 url:"+url);
//		System.out.println("url:"+url);
		try {
			urlConnect = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlConnect.openConnection();
			connection.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),encoding));
			while((line = br.readLine()) != null){
				xmlHttpRet.append(line);
				logger.info("get方式->接收http返回:"+line);
			}
			strRet = xmlHttpRet.toString();
			logger.info("strRet:"+strRet);
			br.close();
			connection.disconnect();
		} catch (Exception e) {
			System.out.println("e.getCause()"+e.getCause());
			System.out.println("e.getLocalizedMessage():"+e.getLocalizedMessage());
			System.out.println("e.getMessage():"+e.getMessage());
			
			StringBuffer sb = new StringBuffer();
			sb.append(e.getMessage()+"\n");
			logger.info("http连接异常");
			StackTraceElement[] ste = e.getStackTrace();
			for (int i = 0; i < ste.length; i++) {
				sb.append("	"+ste[i]+"\n");
			}
			logger.info(e.getCause()+"异常信息："+sb.toString());
			System.out.println(e.getCause()+"异常信息："+sb.toString());
			e.printStackTrace();
		}
		return strRet;
	}

	public String commPost(HashMap<String, String> paramMap, String encoding) {
		// TODO Auto-generated method stub
		return null;
	}


}
