package com.example.driver.info;

public class BindLicenseInfo {
	private CommonRequestHeader header;
	private String licensePlateBind;
	
	public CommonRequestHeader getheader() {
		return header;
	}
	public void setHeader(CommonRequestHeader header) {
		this.header = header;
	}
	public String getLicensePlateBind(){
		return licensePlateBind;
	}
	public void setLicensePlateBind(String licensePlateBind){
		this.licensePlateBind = licensePlateBind;
	}
}
