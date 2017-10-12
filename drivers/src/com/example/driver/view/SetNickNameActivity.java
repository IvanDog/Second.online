package com.example.driver.view;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.security.MessageDigest;

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

import com.example.driver.R;
import com.example.driver.R.id;
import com.example.driver.R.layout;
import com.example.driver.R.string;
import com.example.driver.common.JacksonJsonUtil;
import com.example.driver.common.UserDbAdapter;
import com.example.driver.info.CommonRequestHeader;
import com.example.driver.info.CommonResponse;
import com.example.driver.info.SetHeadPortraitInfo;
import com.example.driver.info.SetNickNameInfo;
import com.example.driver.view.ResetPasswdActivity.UserResetTask;

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetNickNameActivity extends Activity {
	private static final int EVENT_SET_NICKNAME_SUCCESS = 101;
	private static final int EVENT_SET_NICKNAME_FAIL = 102;
	private static final int EVENT_NICKNAME_EMPTY = 103;
    private static final String FILE_NAME_TOKEN = "save_pref_token";
    private static final String LOG_TAG = "SetNickNameActivity";
    private UserSetTask mSetTask;
	private UserDbAdapter mUserDbAdapter;
	private EditText mNickNameET;
	private Button mConfirmBT;
	private String mTeleNumber;
	private String mNickName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserDbAdapter = new UserDbAdapter(this);
		mUserDbAdapter.open();
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	mTeleNumber = bundle.getString("telenumber");
			mNickName = bundle.getString("nickname");
        }
		setContentView(R.layout.activity_set_nickname);
		mNickNameET=(EditText)findViewById(R.id.et_set_nickname);
		if(mNickName!=null){
			mNickNameET.setText(mNickName);
		}
		mConfirmBT=(Button)findViewById(R.id.bt_confirm_nickname);
		mConfirmBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(mNickNameET.getText().toString()==null){
					Message msg = new Message();
					msg.what=EVENT_NICKNAME_EMPTY;
					mHandler.sendMessage(msg);
					return;
				}
				mSetTask = new UserSetTask();
				mSetTask.execute((Void) null);
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	     IntentFilter filter = new IntentFilter();  
	     filter.addAction("ExitApp");  
	     filter.addAction("BackMain");  
	     registerReceiver(mReceiver, filter);
	}
	
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_SET_NICKNAME_SUCCESS:
            	    //Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
            	    finish();
            	    break;
                case EVENT_SET_NICKNAME_FAIL:
            	    //Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
            	    break;
                case EVENT_NICKNAME_EMPTY:
            	    Toast.makeText(getApplicationContext(), "设置不可为空", Toast.LENGTH_SHORT).show();
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
		 * Add for set nick name
		 * */
		public boolean clientSetNick()throws ParseException, IOException, JSONException{
			  HttpClient httpClient = new DefaultHttpClient();
			  httpClient.getParams().setIntParameter(  
	                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
			  httpClient.getParams().setIntParameter(  
	                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置,"0"代表永不超时
			  String strurl = "http://" + 	this.getString(R.string.ip) + "/itspark/owner/nick/set";
			  HttpPost request = new HttpPost(strurl);
			  request.addHeader("Accept","application/json");
			  request.setHeader("Content-Type", "application/json; charset=utf-8");
			  SetNickNameInfo info = new SetNickNameInfo();
			  CommonRequestHeader header = new CommonRequestHeader();
			  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_SET_HEAD_PORTRAIT, mTeleNumber, readToken());
			  info.setHeader(header);
			  info.setNickName(mNickNameET.getText().toString());
			  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
			  Log.e(LOG_TAG,"clientSetNick-> param is " + JacksonJsonUtil.beanToJson(info));
			  request.setEntity(se);//发送数据
			  try{
				  HttpResponse httpResponse = httpClient.execute(request);//获得响应
				  int code = httpResponse.getStatusLine().getStatusCode();
				  if(code==HttpStatus.SC_OK){
					  String strResult = EntityUtils.toString(httpResponse.getEntity());
					  Log.e(LOG_TAG,"clientSetNick->strResult is " + strResult);
					  CommonResponse res = new CommonResponse(strResult);
					  String resCode = res.getResCode();
					  toastWrapper(res.getResMsg());
					  if(resCode.equals("100")){
						  return true;
					  }else{
						  return false;
					  }
				  }else{
					  Log.e(LOG_TAG, "clientSetNick->error code is " + Integer.toString(code));
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
		 * 设置昵称Task
		 * 
		 */
		public class UserSetTask extends AsyncTask<Void, Void, Boolean> {
			@Override
			protected Boolean doInBackground(Void... params) {
				try{
					return clientSetNick();
				}catch(Exception e){
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(final Boolean success) {
				mSetTask = null;
				if(success){
					Message msg = new Message();
					msg.what=EVENT_SET_NICKNAME_SUCCESS;
					mHandler.sendMessage(msg);
				}else{
					Message msg = new Message();
					msg.what=EVENT_SET_NICKNAME_FAIL;
					mHandler.sendMessage(msg);
				}
			}

			@Override
			protected void onCancelled() {
				mSetTask = null;
			}
			
		}
		
		/**
		 * 封装Toast
		 * */
		 private void toastWrapper(final String str) {
		      runOnUiThread(new Runnable() {
		          @Override
		           public void run() {
		               Toast.makeText(SetNickNameActivity.this, str, Toast.LENGTH_SHORT).show();
		           }
		      });
		 }
		 
		    public String readToken() {
		        SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
		        String str = pref.getString("token", "");
		        return str;
		    }
}
