package com.example.driver.view;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.driver.R.id;
import com.example.driver.R.layout;
import com.example.driver.R.string;
import com.example.driver.common.JacksonJsonUtil;
import com.example.driver.info.CommonRequestHeader;
import com.example.driver.info.CommonResponse;
import com.example.driver.info.SetPaymentPasswdInfo;

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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.security.MessageDigest;

public class SetPaymentPasswdActivity extends Activity {
	private static final int EVENT_INPUT_UNFINISHED = 101;
	private static final int EVENT_INPUT_RENEW_ERROR =102;
	private static final int EVENT_MODIFY_SUCCESS = 103;
	private static final String LOG_TAG = "SetPaymentPasswdActivity";
    private static final String FILE_NAME_TOKEN = "save_pref_token";
	private Button mConfirmBT;
	private Button mCancelBT;
	private EditText mPasswdET;
	private EditText mPaymentPasswdET;
	private EditText mRePaymentPasswdET;
	private String mTeleNumber;
    private UserSetTask mSetTask = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	mTeleNumber = bundle.getString("telenumber");
        }
		setContentView(layout.activity_set_payment_passwd);
		mPasswdET =(EditText)findViewById(id.et_login_passwd);
		mPaymentPasswdET =(EditText)findViewById(id.et_payment_passwd);
		mRePaymentPasswdET =(EditText)findViewById(id.et_renew_payment_passwd);
		mConfirmBT=(Button)findViewById(id.bt_confirm_set_payment_passwd);
		mConfirmBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if((mPasswdET.getText().toString()).equals("") || (mPaymentPasswdET.getText().toString()).equals("")
						|| (mRePaymentPasswdET.getText().toString()).equals("")){
					Message msg = new Message();
	                msg.what = EVENT_INPUT_UNFINISHED;
	                mHandler.sendMessage(msg);
				}else if(!(mPaymentPasswdET.getText().toString()).equals(mRePaymentPasswdET.getText().toString())){
					Message msg = new Message();
	                msg.what = EVENT_INPUT_RENEW_ERROR;
	                mHandler.sendMessage(msg);
				}else{
					mSetTask = new UserSetTask(mPasswdET.getText().toString(), mPaymentPasswdET.getText().toString());
					mSetTask.execute((Void) null);
				}
			}
		});
		mCancelBT=(Button)findViewById(id.bt_cancel_set_payment_passwd);
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

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_INPUT_UNFINISHED:
        	        Toast.makeText(getApplicationContext(), "请完成输入", Toast.LENGTH_SHORT).show();
        	        break;
                case EVENT_INPUT_RENEW_ERROR:
            	    Toast.makeText(getApplicationContext(), "两次输入不一致", Toast.LENGTH_SHORT).show();
            	    break;
                case EVENT_MODIFY_SUCCESS:
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
	 * Add for reset password
	 * */
	public boolean clientSet(String passwd, String paymentPasswd)throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时
		  httpClient.getParams().setIntParameter(
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置,"0"代表永不超时
		  String strurl = "http://" + 	this.getString(string.ip) + "/itspark/owner/paymentPasswd/set";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  SetPaymentPasswdInfo info = new SetPaymentPasswdInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_SET_PAYMENT_PASSWORD_CODE, mTeleNumber, readToken());
		  info.setHeader(header);
		  info.setPassword(getMD5Code(passwd));
		  info.setPaymentPassword(getMD5Code(paymentPasswd));
		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientSet-> param is " + JacksonJsonUtil.beanToJson(info));
		  request.setEntity(se);//发送数据
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientSet->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  String resCode = res.getResCode();
				  toastWrapper(res.getResMsg());
				  if(resCode.equals("100")){
					  return true;
				  }else{
					  return false;
				  }
			  }else{
				  Log.e(LOG_TAG, "clientSet->error code is " + Integer.toString(code));
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
	 * 重置密码Task
	 * 
	 */
	public class UserSetTask extends AsyncTask<Void, Void, Boolean> {
		private String passwd;
		private String paymentPasswd;
		public UserSetTask(String passwd, String paymentPasswd){
			this.passwd = passwd;
			this.paymentPasswd = paymentPasswd;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				return clientSet(passwd, paymentPasswd);
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
                msg.what = EVENT_MODIFY_SUCCESS;
                mHandler.sendMessage(msg);
			}
		}

		@Override
		protected void onCancelled() {
			mSetTask = null;
		}
		
	}
	
	/**
	 * MD5 算法
	 * */
	public String getMD5Code(String info) {  
	    try {  
	        MessageDigest md5 = MessageDigest.getInstance("MD5");  
	        md5.update(info.getBytes("UTF-8"));  
	        byte[] encryption = md5.digest();  
	  
	        StringBuffer strBuf = new StringBuffer();  
	        for (int i = 0; i < encryption.length; i++) {  
	            if (Integer.toHexString(0xff & encryption[i]).length() == 1) {  
	                strBuf.append("0").append(  
	                        Integer.toHexString(0xff & encryption[i]));  
	            } else {  
	                strBuf.append(Integer.toHexString(0xff & encryption[i]));  
	            }  
	        }  
	        return strBuf.toString();  
	    } catch (Exception e) {  
	        // TODO: handle exception  
	        return "";  
	    }  
	}
	
	/**
	 * 封装Toast
	 * */
	 private void toastWrapper(final String str) {
	      runOnUiThread(new Runnable() {
	          @Override
	           public void run() {
	               Toast.makeText(SetPaymentPasswdActivity.this, str, Toast.LENGTH_SHORT).show();
	           }
	      });
	 }
	 
	    public String readToken() {
	        SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
	        String str = pref.getString("token", "");
	        return str;
	    }
}
