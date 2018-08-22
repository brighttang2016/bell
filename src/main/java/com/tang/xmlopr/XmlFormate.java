/**
 * xml报文打包 2014-11-24
 */
package com.tang.xmlopr;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class XmlFormate extends Formater{
	private Logger logger  = IcbcLogger.getInstance();
	private String tranPrefix;
	
	/**
	 * 设置节点text
	 * @param index  path当前层索引（begin：0）
	 * @param nodeNameArr 节点名数组
	 * @param nodeValue path目标节点节点值
	 * @param tempNode path目标节点
	 */
	public void setNodeText(int index,String[] nodeNameArr,String nodeValue,Element tempNode){
		System.out.println(tempNode.getPath());
		if(index == nodeNameArr.length-1){
			try{
				logger.info("nodeValue:"+nodeValue);
				if(nodeValue.equals(""))
					tempNode.setText(null);//节点text置空
				else 
					tempNode.setText(nodeValue);//设置节点text
			}catch(Exception e){
				logger.error(tempNode.getPath()+"节点text设置异常");
			}
		}
	}
	
	/**
	 * 递归解析xml上送报文各配置节点
	 * @param node 父节点
	 * @param nodeNameArr
	 * @param index path当前层索引（begin：0）
	 * @param nodeValue path目标节点节点值
	 * @return
	 */
	public void addNextElement(TransactionMapData tmd,Element node,String[] nodeNameArr,int index,String nodeValue){
		Element tempNode= null ;
		logger.debug("nodeValue:"+nodeValue);
		if(index <= nodeNameArr.length-1){
			if(tmd.get("node_"+nodeNameArr[index]) == null){//变量池中该节点不存在
				tempNode = DocumentHelper.createElement(nodeNameArr[index]);
				tmd.put("node_"+nodeNameArr[index], tempNode);
				this.setNodeText(index, nodeNameArr, nodeValue, tempNode);
				node.add(tempNode);
			}else{//变量池中该节点存在
				tempNode = (Element) tmd.get("node_"+nodeNameArr[index]);
				this.setNodeText(index, nodeNameArr, nodeValue, tempNode);
			}
			this.addNextElement(tmd,tempNode, nodeNameArr,++index,nodeValue);
		}
	}
	
	/**
	 * xml报文打包
	 * @param TransactionMapData tmd 交易数据
	 */
	public String xmlPack(TransactionMapData tmd){
		StringBuffer sb = new StringBuffer();
		StringBuffer xmlBuf = new StringBuffer();
		Element targetElement = null;
		List<Element> nodeList = this.getNodeList("msg_xml_cfg.xml", "nt", "http://www.nantian.com.cn/tvbank/schema/packet", "//nt:xmlx");
		//根据交易吗，查找目标交易xml上送报文格式配置
		for(Iterator<Element> it=nodeList.iterator();it.hasNext();){
			Element element = it.next();
			if(element.attribute("id").getText().equals("sIn"+tmd.get("tranCode"))){
				targetElement = element;
			}
		}
		Element root = null;
		for(Iterator<Element> it = targetElement.elements().iterator();it.hasNext();){
			Element element = it.next();
			String varname = element.attribute("varname").getText();
			String nodeValue = (String) tmd.get(varname);
			String path = element.attribute("path").getText();
//			System.out.println(path);
			String[] nodeNameArr = path.split("\\/");
			Document document = DocumentHelper.createDocument();
			if(tmd.get("node_"+nodeNameArr[0]) == null){
				root = DocumentHelper.createElement(nodeNameArr[0]);
				tmd.put("node_"+nodeNameArr[0], root);
			}else{
				root = (Element) tmd.get("node_"+nodeNameArr[0]);
			}
			
//			Element root  = document.addElement(nodeNameArr[0]);
//			root.addElement("CONST_HEAD").addElement("ACCNO").addElement("ttt");
			int index = 1;
			this.addNextElement(tmd,root, nodeNameArr,index,nodeValue);
		}
		sb.append("<?xml version=\"1.0\" encoding=\"gbk\"?>");
		sb.append(root.asXML().toString());
		tmd.put("xmlSndBuf", sb.toString());//xml上送报文
		logger.info("xml报文打包-打包结束：xmlSndBuf:"+sb.toString());
		return sb.toString();
	}
	
	@Override
	public String getTranPrefix() {
		return this.tranPrefix;
	}
	@Override
	public void setTranPrefix(String tranPrefix) {
		this.tranPrefix = tranPrefix;
	}
	

	
}
