package com.tang.xmlopr;

import org.apache.log4j.Logger;

public class IcbcRandom {
	int length = 10000;
	Logger logger = new IcbcUtil().getLogger();
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	/**
	 * 
	 * @param init 种子数
	 * @return 返回10位随机数
	 */
/*	public String getRandom(double init){
		String result = "0";
		int recursionTime = 7;//递归次数,求得结果为a8
		result = this.getRandom(init,recursionTime)*this.getProduct(0.5,32)+"";
		result = result.substring(2, 11);
//		System.out.println("  result:"+result);
		return result;
	}*/
	public double getProduct(double d,double b){
		double temp = d;
		for(int i = 1; i < b; i ++){
			temp = temp*d;
		}
		return temp;
	}
	public double getRandom(double init,int n){
		IcbcRandom rd1 = new IcbcRandom();
		double db = 0;
		if(n == 1){
			db = init*rd1.getProduct(2,1)+init*rd1.getProduct(0.5,1);
		}
		if(n > 1){
			db = (rd1.getRandom(init,n-1)*rd1.getProduct(2,7)+rd1.getRandom(init,n-1)*rd1.getProduct(0.5,7))%getProduct(2,32);
		}
//		System.out.println("IcbcRandom->getRandom 随机数db:"+db);
//		logger.info("IcbcRandom->getRandom 随机数db:"+db);
		return db;
	}
	//测试
	/*public static void main(String[] args) {
		IcbcRandom rd1 = new IcbcRandom();
		rd1.getRandom(20120101010103.0);
	}*/
	
}
