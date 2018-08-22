/**
 * xml报文解包2014-11-24
 */
package com.tang.xmlopr;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class XmlUnformate extends Formater{
	private String tranPrefix;
	private Logger logger  = IcbcLogger.getInstance();
	@Override
	public String getTranPrefix() {
		return this.tranPrefix;
	}
	@Override
	public void setTranPrefix(String tranPrefix) {
		this.tranPrefix = tranPrefix;
	}
	/**
	 * xml报文解包
	 * @param tmd 变量池
	 * @param xmlRcvBuf 服务端返回xml报文字符串
	 */
	public void xmlUnpack(TransactionMapData tmd,String xmlRcvBuf){
		logger.info("******************xml解包开始******************");
		logger.info("xmlRcvBuf:"+xmlRcvBuf);
		String dataBuf = "";//明细数据
		StringBuffer sb = new StringBuffer();
		StringBuffer xmlBuf = new StringBuffer();
		Element targetElement = null;//配置文件目标节点
		Element root = null;
		Document  document = null;//返回xml报文字符串转document对象
		List<Element> nodeList = this.getNodeList("msg_xml_cfg.xml", "nt", "http://www.nantian.com.cn/tvbank/schema/packet", "//nt:xmlx");
		//根据交易吗，查找目标交易xml上送报文格式配置
//		for(Iterator<Element> it=nodeList.iterator();it.hasNext();){
		for(ListIterator<Element> it = nodeList.listIterator();it.hasNext();){
			Element element = it.next();
//			System.out.println("it.nextIndex():"+it.nextIndex());
			if(element.attribute("id").getText().equals("sOut"+tmd.get("tranCode"))){
				targetElement = element;
			}
		}
		try {
			byte[] xmlStrRetArr = xmlRcvBuf.getBytes();
			ByteArrayInputStream bis = new ByteArrayInputStream(xmlStrRetArr);
			SAXReader reader = new SAXReader();
			document = reader.read(bis);
			root = document.getRootElement();
		} catch (Exception e) {
			logger.info("服务端返回xml报文转document对象异常，请检查返回xml报文是否正确");
//			e.printStackTrace();
		}
		for(ListIterator<Element> it = targetElement.elements().listIterator();it.hasNext();){
			Element element = it.next();//当前元素
			String varname = "";
			String path = "";  
			String nodeText = "";
			List<Element> nodeListRet = null;
			String padding = "";
			if(element.attribute("padding") == null){
				varname = element.attribute("varname").getText();
				path = element.attribute("path").getText();
				try{
					nodeText = document.selectSingleNode(path).getText();
				}catch(Exception e){
					logger.info(path+"获取节点数据失败");
				}
				tmd.put(varname, nodeText);
				logger.info("xml解包结果：key："+varname+"|value:"+nodeText);
			}else{
				padding = element.attribute("padding").getText();//明细分隔符
				varname = element.attribute("varname").getText();
				path = element.attribute("path").getText();
				try{
					nodeListRet = document.selectNodes(path);
					String recStr = "";//#分隔
					for(int i = 0;i < nodeListRet.size();i++){
						Element elementRec = nodeListRet.get(i);
						if(i == 0)
							recStr = elementRec.getText();
						else
							recStr = recStr+padding+elementRec.getText();
					}
					if(dataBuf.equals(""))
						dataBuf = recStr;
					else
						dataBuf = dataBuf+"&"+recStr;
					tmd.put(varname, recStr);
					logger.info("xml解包结果：key:"+varname+"|value:"+recStr);
				}catch(Exception e){
					logger.info(path+"获取节点数据失败");
				}
			}
		}
		tmd.put("dataBuf", dataBuf);
		logger.info("******************xml解包结束******************");
	}

}
