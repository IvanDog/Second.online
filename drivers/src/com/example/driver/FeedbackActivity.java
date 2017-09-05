package com.example.driver;

import java.io.IOException;
import java.io.InterruptedIOException;

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

import com.example.driver.LoginActivity.UserLoginTask;
import com.example.driver.MessageCenterActivity.UserGetTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends Activity {
	private String mTeleNumber;
	private EditText mContentET;
	private Button mConfirmBT;
	private Button mCancelBT;
	private static final int EVENT_CONFIRM_SUCCESS = 101;
    private static final String FILE_NAME_TOKEN = "save_pref_token";
    private static final String LOG_TAG = "FeedbackActivity";
	private UserFeedbackTask mFeedbackTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	mTeleNumber = bundle.getString("telenumber");
        }
        mContentET = (EditText)findViewById(R.id.et_feedback_content);
        mConfirmBT=(Button)findViewById(R.id.bt_confirm_feedback);
        mConfirmBT.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	mFeedbackTask = new UserFeedbackTask();
            	mFeedbackTask.execute((Void) null);
            }
        });
        mCancelBT=(Button)findViewById(R.id.bt_cancel_feedback);
        mCancelBT.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	finish();
            }
        });
		 getActionBar().setDisplayHomeAsUpEnabled(true);
	     IntentFilter filter = new IntentFilter();  
	     filter.addAction("ExitApp");  
	     filter.addAction("BackMain");  
	     registerReceiver(mReceiver, filter);
    }
    
    private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_CONFIRM_SUCCESS:
                	finish();
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
	 * Add for user feedback
	 * */
	public boolean clientFeedBack()throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置,"0"代表永不超时
		  String strurl = "http://" + 	this.getString(R.string.ip) + ":8080/park/owner/feedback/feedback";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
			//request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_FEEDBACK, mTeleNumber, readToken());
		  param.put("header", header);
		  param.put("feedContent", mContentET.getText().toString());
		  StringEntity se = new StringEntity(param.toString(), "UTF-8");
		  request.setEntity(se);//发送数据
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientFeedBack->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  String resCode = res.getResCode();
				  toastWrapper(res.getResMsg());
				  if(resCode.equals("100")){
					  return true;
				  }else if(resCode.equals("201")){
					  return false;
				  }
			  }else{
				  Log.e(LOG_TAG, "clientFeedBack->error code is " + Integer.toString(code));
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
	 * 用户反馈task
	 * 
	 */
	public class UserFeedbackTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				return clientFeedBack();
			}catch(Exception e){
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mFeedbackTask = null;
			if (success) {
            	Message msg = new Message();
            	msg.what=EVENT_CONFIRM_SUCCESS;
            	mHandler.sendMessage(msg);
			}
		}

		@Override
		protected void onCancelled() {
			mFeedbackTask = null;
		}
	}
	
	/**
	 * 封装Toast
	 * */
	 private void toastWrapper(final String str) {
	      runOnUiThread(new Runnable() {
	          @Override
	           public void run() {
	               Toast.makeText(FeedbackActivity.this, str, Toast.LENGTH_SHORT).show();
	           }
	      });
	 }
	 
    private String readToken() {
        SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
        String str = pref.getString("token", "");
        return str;
    }
}
