package com.example.driver.info;


public class SetHeadPortraitInfo {
	private CommonRequestHeader header;
	private byte[] headPortrait;
	
	public CommonRequestHeader getheader() {
		return header;
	}
	public void setHeader(CommonRequestHeader header) {
		this.header = header;
	}
	
	public byte[] getHeadPortrait(){
		return headPortrait;
	}
	public void setHeadPortrait(byte[] headPortrait){
		this.headPortrait = headPortrait;
	}
}
