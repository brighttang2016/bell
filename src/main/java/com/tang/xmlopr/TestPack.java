package com.tang.xmlopr;




public class TestPack {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestPack tp = new TestPack();
		XmlFormate xmlFormate = new XmlFormate();
		XmlUnformate xmlUnformate = new XmlUnformate();
//		TransactionMapData tmd = TransactionMapData.getInstance();
		TransactionMapData tmd = new TransactionMapData();
		tmd.put("tranCode", "f0001");
		tmd.put("mdCardNo", "mdCardNo");
		tmd.put("passWord", "passWord");
		String xmlPackRes = xmlFormate.xmlPack(tmd);							// xml报文打包
		System.out.println("xmlPackRes:"+xmlPackRes);
		
		String xmlRcvBuf = "<?xml version=\"1.0\" encoding=\"gbk\"?><UTILITY_PAYMENT><CONST_HEAD><REQUEST_TYPE><t1><t2>01</t2></t1></REQUEST_TYPE><COUNT>1000</COUNT></CONST_HEAD><DATA_AREA><USER_CODE>200810405234</USER_CODE><DESC><REC><DATE>20140101</DATE><JE>100</JE><ZNJ>10</ZNJ></REC><REC><DATE>20140202</DATE><JE>200</JE><ZNJ>20</ZNJ></REC><REC><DATE>20140303</DATE><JE>300</JE><ZNJ>30</ZNJ></REC></DESC></DATA_AREA></UTILITY_PAYMENT>";
		
		xmlUnformate.xmlUnpack(tmd, xmlRcvBuf);
		System.out.println(tmd.get("date"));
		System.out.println(tmd.get("je"));
		System.out.println(tmd.get("znj"));
		
		System.out.println(tmd.get("agentCode"));
		
	}
}
