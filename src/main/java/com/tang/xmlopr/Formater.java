package com.tang.xmlopr;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;


public class Formater {
	public String getTranPrefix(){
		return null;
	}
	public void setTranPrefix(String tranPrefix){
	}
	
	/**
	 * 读取报文配置文件
	 * @param fileName
	 * @return
	 */
	public List<Element> getNodeList(String fileName,String prefix,String nameSpace,String node){
		Document document = null;
		List<Element> nodeList = null;
		String path = "";
		SAXReader reader = new SAXReader();
		try {
			path = Formater.class.getClassLoader().getResource("").toURI().getPath().toString();
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
	
}
