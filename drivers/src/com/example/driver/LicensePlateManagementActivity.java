package com.example.driver;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.driver.ParkingCouponActivity.UserCouponTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LicensePlateManagementActivity extends Activity {
	private Button mAddLicensePlateBT;
	private TextView mLicensePlateFirstTV;
	private TextView mLicensePlateSecondTV;
	private ImageView mDeleteFirstLicensePlateIV;
	private ImageView mDeleteSecondLicensePlateIV;
	private View mLinearLicensePlateFirst;
	private View mLinearLicensePlateSecond;
	private String mTeleNumber;
    private String mLicensePlateFirst = null;
    private String mLicensePlateSecond = null;
    private int mType = -1;
	private UserDbAdapter mUserDbAdapter;
	private static final int EVENT_DISPLAY_DOUBLE_LICENSE=101;
	private static final int EVENT_DISPLAY_FIRST_LICENSE=102;
	private static final int EVENT_DISPLAY_SECOND_LICENSE=103;
	private static final int TYPE_DELETE_FIRST_LICENSE =201;
	private static final int TYPE_DELETE_SECOND_LICENSE =202;
	private static final int EVENT_DISMISS_FIRST_LICENSE=301;
	private static final int EVENT_DISMISS_SECOND_LICENSE=302;
	private static final int EVENT_DISMISS_BOTH_LICENSE=303;
    private static final String FILE_NAME_TOKEN = "save_pref_token";
    private static final String LOG_TAG = "LicensePlateManagementActivity";
    private UserLicenseDisplayTask  mDisplayLicenseTask= null;
    private UserLicenseDeleteTask  mDeleteLicenseTask= null;
    
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        mUserDbAdapter = new UserDbAdapter(this);
        Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mTeleNumber=bundle.getString("telenumber");
        setContentView(R.layout.activity_license_management);
        mAddLicensePlateBT=(Button)findViewById(R.id.bt_enter_bind_license_plate);
        mAddLicensePlateBT.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	Intent intent = new Intent(LicensePlateManagementActivity.this,InputLicenseActivity.class);
            	Bundle bundle = new Bundle();
            	bundle.putString("telenumber", mTeleNumber);
            	intent.putExtras(bundle);
            	startActivityForResult(intent,0);
            }
        });
        mLicensePlateFirstTV=(TextView)findViewById(R.id.tv_license_plate_first);
        mLicensePlateSecondTV=(TextView)findViewById(R.id.tv_license_plate_second);
        mDeleteFirstLicensePlateIV=(ImageView)findViewById(R.id.iv_delete_license_plate_first);
        mDeleteFirstLicensePlateIV.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	mType = TYPE_DELETE_FIRST_LICENSE;
            	//new DeleteThread().start();
            	mDeleteLicenseTask = new UserLicenseDeleteTask();
            	mDeleteLicenseTask.execute((Void) null);
            }
        });
        mDeleteSecondLicensePlateIV=(ImageView)findViewById(R.id.iv_delete_license_plate_second);
        mDeleteSecondLicensePlateIV.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	mType = TYPE_DELETE_SECOND_LICENSE;
            	//new DeleteThread().start();
            	mDeleteLicenseTask = new UserLicenseDeleteTask();
            	mDeleteLicenseTask.execute((Void) null);
            }
        });
        mLinearLicensePlateFirst=(View)findViewById(R.id.linear_license_plate_first);
        mLinearLicensePlateSecond=(View)findViewById(R.id.linear_license_plate_second);
        //new DisplayThread().start();
        mDisplayLicenseTask = new UserLicenseDisplayTask();
        mDisplayLicenseTask.execute((Void) null);
    	getActionBar().setDisplayHomeAsUpEnabled(true); 
	     IntentFilter filter = new IntentFilter();  
	     filter.addAction("ExitApp");  
	     filter.addAction("BackMain");  
	     registerReceiver(mReceiver, filter);
	}
	
	public void onResume(){
		super.onResume();
	}
	
	public void onPause(){
		super.onPause();
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
        if(requestCode==0){  
        	finish();
        }  
    } 

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){
			    case EVENT_DISPLAY_DOUBLE_LICENSE:
                    Log.e(LOG_TAG,"EVENT_DISPLAY_DOUBLE_LICENSE");
    		    	mLicensePlateFirstTV.setText(mLicensePlateFirst);
    		    	mLicensePlateSecondTV.setText(mLicensePlateSecond);
    		    	break;
			    case EVENT_DISPLAY_FIRST_LICENSE:
                    Log.e(LOG_TAG,"EVENT_DISPLAY_FIRST_LICENSE");
    		    	mLicensePlateFirstTV.setText(mLicensePlateFirst);
    		    	mLinearLicensePlateSecond.setVisibility(View.GONE);
    		    	break;
			    case EVENT_DISPLAY_SECOND_LICENSE:
                    Log.e(LOG_TAG,"EVENT_DISPLAY_SECOND_LICENSE");
    		    	mLicensePlateFirstTV.setText(mLicensePlateSecond);
    		    	mLinearLicensePlateSecond.setVisibility(View.GONE);
    		    	break;
			    case EVENT_DISMISS_FIRST_LICENSE:
			    	mLinearLicensePlateFirst.setVisibility(View.GONE);
			    	break;
			    case EVENT_DISMISS_SECOND_LICENSE:
			    	mLinearLicensePlateSecond.setVisibility(View.GONE);
			    	break;
			    case EVENT_DISMISS_BOTH_LICENSE:
			    	mLinearLicensePlateFirst.setVisibility(View.GONE);
		    	    mLinearLicensePlateSecond.setVisibility(View.GONE);
		    	    break;
    		    default:
    		    	break;
			}
		}
	};
	
	
    
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	         case android.R.id.home:  
	             finish();  
	             break;    
	        default:  
	             break;  
	    }  
	    return super.onOptionsItemSelected(item);  
	  }
	
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){  
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction()!=null && intent.getAction().equals("ExitApp")){
				finish();
			}else if(intent.getAction()!=null && intent.getAction().equals("BackMain")){
				finish();
			}
		}            
    };
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
    
    /**
 	 * Add for request display license plate
 	 * */
 	public boolean clientQueryLicense() throws ParseException, IOException, JSONException{
 		Log.e(LOG_TAG,"clientQueryLicense->enter clientQueryLicense");  
 		HttpClient httpClient = new DefaultHttpClient();
 		  httpClient.getParams().setIntParameter(  
                   HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
 		  httpClient.getParams().setIntParameter(  
                   HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
 		  String strurl = "http://" + this.getString(R.string.ip) + ":8080/park/owner/license/query";
 		  HttpPost request = new HttpPost(strurl);
 		  request.addHeader("Accept","application/json");
 			//request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_QUERY_LICENSE_CODE, mTeleNumber, readToken());
		  param.put("header", header);
 		  StringEntity se = new StringEntity(param.toString(), "UTF-8");
 		  request.setEntity(se);//发送数据
 		  try{
 			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
 			  int code = httpResponse.getStatusLine().getStatusCode();
 			  if(code==HttpStatus.SC_OK){
 				  String strResult = EntityUtils.toString(httpResponse.getEntity());
 				  Log.e(LOG_TAG,"clientQueryLicense->strResult is " + strResult);
 				  CommonResponse res = new CommonResponse(strResult);
 				  if(res.getResCode().equals("100")){
 					  mLicensePlateFirst = (String)res.getPropertyMap().get("licensePlateFirst");
 					  mLicensePlateSecond = (String)res.getPropertyMap().get("licensePlateSecond");
 					  return true;
 				  }else if(res.getResCode().equals("201")){
 			          return false;
 				  }  
 			}else{
 					  Log.e(LOG_TAG, "clientQueryLicense->error code is " + Integer.toString(code));
 					  return false;
 		    }
 		  }catch(InterruptedIOException e){
 			  if(e instanceof ConnectTimeoutException){
 				  toastWrapper("连接超时");  
 			  }else if(e instanceof InterruptedIOException){
 				  toastWrapper("请求超时");  
 			  }
           }finally{  
         	  httpClient.getConnectionManager().shutdown();  
           }  
 		  return false;
     }
 	
 	/**
 	 * 显示已绑定车牌Task
 	 */
 	public class UserLicenseDisplayTask extends AsyncTask<Void, Void, Boolean> {
 		@Override
 		protected Boolean doInBackground(Void... params) {
 			try{
 				Log.e(LOG_TAG,"UserLicensePlateTask->doInBackground");  
 				return clientQueryLicense();
 			}catch(Exception e){
 				Log.e(LOG_TAG,"UserLicensePlateTask->exists exception ");  
 				e.printStackTrace();
 			}
 			return false;
 		}

 		@Override
 		protected void onPostExecute(final Boolean success) {
 			mDisplayLicenseTask = null;
 			if(success){
    		    if(!mLicensePlateFirst.equals("") && !mLicensePlateSecond.equals("")){
    		    	Message msg = new Message();
    		    	msg.what = EVENT_DISPLAY_DOUBLE_LICENSE;
    		    	mHandler.sendMessage(msg);
    		    }else if(!mLicensePlateFirst.equals("")  && mLicensePlateSecond.equals("") ){
    		    	Message msg = new Message();
    		    	msg.what = EVENT_DISPLAY_FIRST_LICENSE;
    		    	mHandler.sendMessage(msg);
    		    }else if(mLicensePlateFirst.equals("")  && !mLicensePlateSecond.equals("") ){
    		    	Message msg = new Message();
    		    	msg.what = EVENT_DISPLAY_SECOND_LICENSE;
    		    	mHandler.sendMessage(msg);
    		    }else if(mLicensePlateFirst.equals("")  && mLicensePlateSecond.equals("") ){
    		    	Message msg = new Message();
    		    	msg.what = EVENT_DISMISS_BOTH_LICENSE;
    		    	mHandler.sendMessage(msg);
    		    }			
 			}
 		}

 		@Override
 		protected void onCancelled() {
 			mDisplayLicenseTask = null;
 		}
 		
 	}
 	
    /**
 	 * Add for request delete  license plate
 	 * */
 	public boolean clientDeleteLicense() throws ParseException, IOException, JSONException{
 		Log.e(LOG_TAG,"clientDeleteLicense->enter clientQueryLicense");  
 		HttpClient httpClient = new DefaultHttpClient();
 		  httpClient.getParams().setIntParameter(  
                   HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
 		  httpClient.getParams().setIntParameter(  
                   HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
 		  String strurl = "http://" + this.getString(R.string.ip) + ":8080/park/owner/license/delete";
 		  HttpPost request = new HttpPost(strurl);
 		  request.addHeader("Accept","application/json");
 			//request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_DELETE_LICENSE_CODE, mTeleNumber, readToken());
		  param.put("header", header);
		    if(mType==TYPE_DELETE_FIRST_LICENSE){
		    	if((mLicensePlateFirstTV.getText().toString()).equals(mLicensePlateFirst)){
		    		param.put("licensePlateDismiss", mLicensePlateFirst);
		    	}else if((mLicensePlateFirstTV.getText().toString()).equals(mLicensePlateSecond)){
		    		param.put("licensePlateDismiss", mLicensePlateSecond);
		    	}
		    }else if(mType==TYPE_DELETE_SECOND_LICENSE){
		    	if((mLicensePlateSecondTV.getText().toString()).equals(mLicensePlateFirst)){
		    		param.put("licensePlateDismiss", mLicensePlateFirst);
		    	}else if((mLicensePlateSecondTV.getText().toString()).equals(mLicensePlateSecond)){
		    		param.put("licensePlateDismiss", mLicensePlateSecond);
		    	}
		    }
 		  StringEntity se = new StringEntity(param.toString(), "UTF-8");
 		  request.setEntity(se);//发送数据
 		  try{
 			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
 			  int code = httpResponse.getStatusLine().getStatusCode();
 			  if(code==HttpStatus.SC_OK){
 				  String strResult = EntityUtils.toString(httpResponse.getEntity());
 				  Log.e(LOG_TAG,"clientDeleteLicense->strResult is " + strResult);
 				  CommonResponse res = new CommonResponse(strResult);
 				 toastWrapper(res.getResMsg());  
 				  if(res.getResCode().equals("100")){
 					  return true;
 				  }else if(res.getResCode().equals("201")){
 			          return false;
 				  }  
 			}else{
 					  Log.e(LOG_TAG, "clientDeleteLicense->error code is " + Integer.toString(code));
 					  return false;
 		    }
 		  }catch(InterruptedIOException e){
 			  if(e instanceof ConnectTimeoutException){
 				  toastWrapper("连接超时");  
 			  }else if(e instanceof InterruptedIOException){
 				  toastWrapper("请求超时");  
 			  }
           }finally{  
         	  httpClient.getConnectionManager().shutdown();  
           }  
 		  return false;
     }
 	
 	/**
 	 * 解绑车牌Task
 	 */
 	public class UserLicenseDeleteTask extends AsyncTask<Void, Void, Boolean> {
 		@Override
 		protected Boolean doInBackground(Void... params) {
 			try{
 				Log.e(LOG_TAG,"UserLicenseDeleteTask->doInBackmDeleteLicenseTaskground");  
 				return clientDeleteLicense();
 			}catch(Exception e){
 				Log.e(LOG_TAG,"UserLicenseDeleteTask->exists exception ");  
 				e.printStackTrace();
 			}
 			return false;
 		}

 		@Override
 		protected void onPostExecute(final Boolean success) {
 			mDeleteLicenseTask = null;
 			if(success){
 			    if(mType==TYPE_DELETE_FIRST_LICENSE){
 			    	Message msg = new Message();
 			    	msg.what = EVENT_DISMISS_FIRST_LICENSE;
 			    	mHandler.sendMessage(msg);
 			    }else if(mType==TYPE_DELETE_SECOND_LICENSE){
 	    		    Message msg = new Message();
 	    		    msg.what = EVENT_DISMISS_SECOND_LICENSE;
 	    		    mHandler.sendMessage(msg);
 			    }	
 			}
 		}

 		@Override
 		protected void onCancelled() {
 			mDeleteLicenseTask = null;
 		}
 		
 	}
 	/**
 	 * 封装Toast
 	 * */
 	 private void toastWrapper(final String str) {
 	      runOnUiThread(new Runnable() {
 	          @Override
 	           public void run() {
 	               Toast.makeText(LicensePlateManagementActivity.this, str, Toast.LENGTH_SHORT).show();
 	           }
 	      });
 	 }
 	 
     private String readToken() {
         SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
         String str = pref.getString("token", "");
         return str;
     }
}
