package com.example.driver;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.example.driver.MessageCenterActivity.UserGetTask;
import com.example.driver.R.drawable;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ParkingCouponActivity extends FragmentActivity {
    private ListView mListView = null;
    private TextView mEmptyCouponNotifyTV;
	private UserDbAdapter mUserDbAdapter;
	private String mTeleNumber;
	private int mParkingCoupon;
    private static final String FILE_NAME_TOKEN = "save_pref_token";
    private UserCouponTask mCouponTask= null;
    private ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
    private ParkingCouponAdapter mParkingCouponAdapter;
	public static final int NO_COUPON =101;
	public static final int EVENT_SET_ADAPTER = 102;
    private static final String LOG_TAG = "ParkingCouponActivity";

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        mUserDbAdapter = new UserDbAdapter(this);
        setContentView(R.layout.activity_parking_coupon);
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	mTeleNumber = bundle.getString("telenumber");
        }
        mListView=(ListView)findViewById(R.id.list_parking_coupon);  
        mEmptyCouponNotifyTV=(TextView)findViewById(R.id.tv_empty_coupon_notify_coupon);  
        mCouponTask = new UserCouponTask();
        mCouponTask.execute((Void) null);
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	     IntentFilter filter = new IntentFilter();  
	     filter.addAction("ExitApp");  
	     filter.addAction("BackMain");  
	     registerReceiver(mReceiver, filter);
	}
    
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
    
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NO_COUPON:
                	mEmptyCouponNotifyTV.setVisibility(View.VISIBLE);
                    break;
                case EVENT_SET_ADAPTER:
                	mParkingCouponAdapter= new ParkingCouponAdapter(getApplicationContext(), mList);
    	            mListView.setAdapter(mParkingCouponAdapter); 
    	            mParkingCouponAdapter.notifyDataSetChanged();
            	    break;
                default:
                    break;
            }
        }
    };
    
    /**
	 * Add for request today record and receive result
	 * */
	public boolean clientQueryCoupon() throws ParseException, IOException, JSONException{
		Log.e(LOG_TAG,"clientQueryCoupon->enter clientQueryCoupon");  
		HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
		  String strurl = "http://" + this.getString(R.string.ip) + ":8080/park/owner/queryCoupon/query";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
	 	//request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_QUERY_COUPON_CODE, mTeleNumber, readToken());
		  param.put("header", header);
		  StringEntity se = new StringEntity(param.toString(), "UTF-8");
		  request.setEntity(se);//发送数据
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientQueryCoupon->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  if(res.getResCode().equals("100")){
					  mList = res.getDataList();
					  return true;
				  }else if(res.getResCode().equals("201")){
			          return false;
				  }  
			}else{
					  Log.e(LOG_TAG, "clientQueryCoupon->error code is " + Integer.toString(code));
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
	 * 获取优惠券Task
	 */
	public class UserCouponTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				Log.e(LOG_TAG,"UserCouponTask->doInBackground");  
				return clientQueryCoupon();
			}catch(Exception e){
				Log.e(LOG_TAG,"UserCouponTask->exists exception ");  
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mCouponTask = null;
			if(success){
			    if(mList.size()!=0){
				    Message msg = new Message();
				    msg.what=EVENT_SET_ADAPTER;
				    mHandler.sendMessage(msg);
			    }else{
				    Message msg = new Message();
				    msg.what=NO_COUPON;
				    mHandler.sendMessage(msg);
			    }					
			}
		}

		@Override
		protected void onCancelled() {
			mCouponTask = null;
		}
		
	}
	
	/**
	 * 封装Toast
	 * */
	 private void toastWrapper(final String str) {
	      runOnUiThread(new Runnable() {
	          @Override
	           public void run() {
	               Toast.makeText(ParkingCouponActivity.this, str, Toast.LENGTH_SHORT).show();
	           }
	      });
	 }
	 
    private String readToken() {
        SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
        String str = pref.getString("token", "");
        return str;
    }

}
