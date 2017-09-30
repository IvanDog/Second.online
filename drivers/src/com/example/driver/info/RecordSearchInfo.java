package com.example.driver.info;

public class RecordSearchInfo {
	private CommonRequestHeader header;
	private int type;
	
	public CommonRequestHeader getheader() {
		return header;
	}
	public void setHeader(CommonRequestHeader header) {
		this.header = header;
	}
	public int getType(){
		return type;
	}
	public void setType(int type){
		this.type = type;
	}
}
