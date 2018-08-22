/**
 * 调用webService服务
 * 2015-09-21
 * @author brighttang
 */
package com.nantian.conn;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.nantian.util.IcbcUtil;
public class CallService {
	Logger logger = IcbcUtil.getLogger();
	@Value("${endPoint}")
	String endPoint;
	/**
	 * 调用webService服务
	 * @param methodName 服务名
	 * @param paramsArray 参数数组
	 * @return
	 */
	public String call(String methodName,Object[] paramsArray){
		String result = "";
		try {
//        	String endPoint = "http://localhost:8080/IcbcWebServiceServer/services/icbcService";//服务url（末尾服务名）
        	logger.info("endpoint:"+endPoint);
			URL url = new URL(endPoint);
        	//调用过程
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(url);
            call.setOperationName(methodName);//账户验证 
            call.setUseSOAPAction( true );
            result = (String)call.invoke(paramsArray);
            System.out.println("服务端返回:"+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;
	}
}
