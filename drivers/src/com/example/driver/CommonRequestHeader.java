package com.example.driver;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

    /** 
     * 基本请求体封装类 
     */  
    public class CommonRequestHeader extends HashMap<String,Object> {
    	public static final int REQUEST_COLLECTOR_LOGIN_CODE = 101;
    	public static final int REQUEST_COLLECTOR_LOGOUT_CODE = 102;
    	public static final int REQUEST_COLLECTOR_GET_WORK_CODE = 103;
    	public static final int REQUEST_COLLECTOR_GET_LOCATION_STATE_CODE = 104;
    	public static final int REQUEST_COLLECTOR_WORK_CLOCK_CODE = 105;
    	public static final int REQUEST_COLLECTOR_LICENSE_INPUT_CODE = 106;
    	public static final int REQUEST_COLLECTOR_NEW_PARKING_CODE = 107;
    	public static final int REQUEST_COLLECTOR_QUERY_EXPENSE_CODE = 108;
    	public static final int REQUEST_COLLECTOR_PAY_CODE = 109;
    	public static final int REQUEST_COLLECTOR_QUERY_PARKING_SPACE_CODE = 110;
    	public static final int REQUEST_COLLECTOR_QUERY_PARKING_INFORMATION_CODE = 111;
    	public static final int REQUEST_COLLECTOR_QUERY_TODAY_PARKING_RECORD_CODE = 112;
    	public static final int REQUEST_COLLECTOR_QUERY_HISTORY_PARKING_RECORD_CODE = 113;
    	public static final int REQUEST_COLLECTOR_MESSAGE_CENTER_CODE = 114;
    	public static final int REQUEST_COLLECTOR_RESET_PASSWORD_CODE = 115;
    	
    	public static final int REQUEST_OWNER_REGISTER_CODE = 201;
    	public static final int REQUEST_OWNER_ANALYSIS_ACCOUNT_CODE = 202;
    	public static final int REQUEST_OWNER_LOGIN_CODE = 203;
    	public static final int REQUEST_OWNER_LOGOUT_CODE = 204;
    	public static final int REQUEST_OWNER_QUERY_PARKING_INFORMATION_CODE = 205;
    	public static final int REQUEST_OWNER_QUERY_EXPENSE_CODE = 206;
    	public static final int REQUEST_OWNER_PAY_CODE = 207;
    	public static final int REQUEST_OWNER_NEARBY_PARKING_CODE = 208;
    	public static final int REQUEST_OWNER_CURRENT_PARKING_RECORD_CODE = 209;
    	public static final int REQUEST_OWNER_USER_CENTER_INFORMATION_CODE = 210;
    	public static final int REQUEST_OWNER_QUERY_ACCOUNT_CODE = 211;
    	public static final int REQUEST_OWNER_QUERY_RECHARGE_CODE = 212;
    	public static final int REQUEST_OWNER_QUERY_COUPON_CODE = 213;
    	public static final int REQUEST_OWNER_USER_INFORMATION_CODE = 214;
    	public static final int REQUEST_OWNER_RESET_PASSWORD = 215;
    	public static final int REQUEST_OWNER_SET_NICK_NAME = 216;
    	public static final int REQUEST_OWNER_SET_HEAD_PORTRAIT = 217;
    	public static final int REQUEST_OWNER_FEEDBACK = 218;
    	public static final int REQUEST_OWNER_MESSAGE_CENTER_CODE = 219;
    	public static final int REQUEST_OWNER_QUERY_LICENSE_CODE = 220;
    	public static final int REQUEST_OWNER_DELETE_LICENSE_CODE = 221;
    	public static final int REQUEST_OWNER_BIND_LICENSE = 222;
    	public static final int REQUEST_QUERY_PARKING_RECORD_CODE = 223;
        /** 
         * 请求码
         */  
        private int requestCode;  
        /** 
         * 账户
         */  
        private String account;  
        /** 
         * token
         */  
        private String token;   
      
        public CommonRequestHeader() {  
        	this.put("requestCode", -1);  
        	this.put("account", "");  
        	this.put("token", "");  
        }  
      

        /** 
         * 为请求报文设置报头
         */  
        public void addRequestHeader(int requestCode,String account,String token) {  
        	this.put("requestCode", requestCode);  
        	this.put("account", account);  
        	this.put("token", token);  
        }
        /** 
         * 获取请求码
         */  
        public int getRequestCode() {  
        	return Integer.parseInt(String.valueOf(this.get("requestCode")));
        }
        /** 
         * 获取账户名
         */  
        public String getAccount() {  
        	return (String)this.get("account");
        }
        /** 
         * 获取token
         */  
        public String getToken() {  
        	return (String)this.get("token");
        }
    }  