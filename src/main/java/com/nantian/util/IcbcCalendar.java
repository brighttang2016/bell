package com.nantian.util;

import java.util.Calendar;

public class IcbcCalendar extends Calendar {
//	Calendar cd = Calendar.getInstance();
	
	/**
	 * @param timeBegin 格式：2012-01-01
	 * @return 格式20120101000000
	 */
	public String setTimeBegin(String timeBegin){
		String strRet = "";
		String[] strArr = timeBegin.split("-");
		if(strArr.length == 3){
			for(int i = 0;i < strArr.length;i++){
				strRet += strArr[i];
			}
			strRet += "000000";
		}
		return strRet;
	}
	/**
	 * @param timeEnd 格式：2012-01-01
	 * @return 格式20120101595959
	 */
	public String setTimeEnd(String timeEnd){
		String strRet = "";
		String[] strArr = timeEnd.split("-");
		if(strArr.length == 3){
			for(int i = 0;i < strArr.length;i++){
				strRet += strArr[i];
			}
			strRet += "595959";
		}
		
		return strRet;
	}
	/**
	 * 小于10前补0
	 * @param dataIn
	 * @return
	 */
	public String prePadding(int dataIn){
		String dataOut = "";
		if(dataIn < 10){
			dataOut = "0"+dataIn;
		}else{
			dataOut =dataIn+"";
		}
		return dataOut;
	}
	public int getYear(){
		return Calendar.getInstance().get(YEAR);
	}
	public String getMonth(){
		int month = Calendar.getInstance().get(MONTH)+1;
		return this.prePadding(month);
	}
	public String getDay(){
		return this.prePadding(Calendar.getInstance().get(DAY_OF_MONTH));
	}
	public String getHour(){
		return this.prePadding(Calendar.getInstance().get(HOUR_OF_DAY));
	}
	public String getMinute(){
		return this.prePadding(Calendar.getInstance().get(MINUTE));
	}
	public String getSecond(){
//		System.out.println("getSecond:"+Calendar.getInstance().get(SECOND));
		return this.prePadding(Calendar.getInstance().get(SECOND));
	}
	@Override
	protected void computeTime() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void computeFields() {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(int field, int amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void roll(int field, boolean up) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMinimum(int field) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaximum(int field) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getGreatestMinimum(int field) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLeastMaximum(int field) {
		// TODO Auto-generated method stub
		return 0;
	}

}
