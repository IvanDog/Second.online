package com.example.driver.view;

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

import com.example.driver.R;
import com.example.driver.R.id;
import com.example.driver.R.layout;
import com.example.driver.R.string;
import com.example.driver.common.JacksonJsonUtil;
import com.example.driver.common.UserDbAdapter;
import com.example.driver.info.CommonRequestHeader;
import com.example.driver.info.CommonResponse;
import com.example.driver.info.LotDetailQueryInfo;
import com.example.driver.view.MainActivity.UserQueryTask;

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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ParkingInformationActivity extends Activity {
	private final static int EVENT_DISPLAY_TIME = 101;
	private final static int EVENT_DISPLAY_INFORMATION = 102;
	public static String LOG_TAG = "ParkingInformationActivity";
    private static final String FILE_NAME_TOKEN = "save_pref_token";
    private UserQueryTask mQueryTask = null;
	private TextView mParkNameTV;
	private TextView mParkNumberTV;
	private TextView mLocationNumberTV;
	private TextView mLicenseNumberTV;
	private TextView mCarTypeTV;
	private TextView mParkTypeTV;
	private TextView mFeeScaleTV;
	private TextView mStartTimeTV;
	private TextView mLeaveTimeTV;
	private Button mConfirmLeavingBT;
	private Button mCancelLeavingBT;
	private UserDbAdapter mUserDbAdapter;
	
	private String mTeleNumber;
	private Long mParkingEnterID;
	private String mLicensePlateNumber;
	private String mParkName;
	private String mParkNumber;
	private String mLocationNumber;
	private String mCarType;
	private String mParkType;
	private String mFeeScale;
	private String mStartTime;
	private String mLeaveTime;;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mUserDbAdapter = new UserDbAdapter(this);
        setContentView(R.layout.activity_parking_detail);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTeleNumber= bundle.getString("telenumber");
        mParkingEnterID= bundle.getLong("parkingEnterID");
        mLicensePlateNumber= bundle.getString("licensePlateNumber");
        mParkName= bundle.getString("parkName");
        mStartTime= bundle.getString("startTime");
        mParkNumber = bundle.getString("parkNumber");
        mLocationNumber = bundle.getString("parkingLocation");
		mLicenseNumberTV=(TextView)findViewById(R.id.tv_license_plate_number_parking_detail);
		mParkNameTV = (TextView)findViewById(R.id.tv_parking_name_parking_detail);
		mParkNumberTV = (TextView) findViewById(R.id.tv_parking_number_parking_detail);
		mCarTypeTV=(TextView)findViewById(R.id.tv_car_type_parking_detail);
		mParkTypeTV=(TextView)findViewById(R.id.tv_parking_type_parking_detail);
        mLocationNumberTV=(TextView)findViewById(R.id.tv_location_number_parking_detail);
        mFeeScaleTV=(TextView)findViewById(R.id.tv_expense_standard_parking_detail);
        mStartTimeTV=(TextView) findViewById(R.id.tv_start_time_parking_detail);
        mLeaveTimeTV=(TextView) findViewById(R.id.tv_leave_time_parking_detail);
        mConfirmLeavingBT=(Button)findViewById(R.id.bt_confirm_leaving);
        mConfirmLeavingBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		Intent intent = new Intent(ParkingInformationActivity.this,PaymentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("telenumber", mTeleNumber);
				bundle.putString("parkNumber",mParkNumber);
				bundle.putString("licensenumber", mLicensePlateNumber);
				bundle.putString("starttime", mStartTime);
				bundle.putString("leavetime", mLeaveTime);
				bundle.putString("expensestandard", mFeeScale);
				bundle.putLong("parkingEnterID", mParkingEnterID);
				intent.putExtras(bundle);
				startActivity(intent);
        	}
        });
        mCancelLeavingBT=(Button)findViewById(R.id.bt_cancel_leaving);
        mCancelLeavingBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		finish();
        	}
        });
		mQueryTask = new UserQueryTask();
		mQueryTask.execute((Void) null);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        IntentFilter filter = new IntentFilter();  
        filter.addAction("ExitApp");  
        filter.addAction("BackMain");  
        registerReceiver(mReceiver, filter);
	}
	
	public class TimeThread extends Thread {
        @Override
        public void run () {
            do {
                try {
                    Message msg = new Message();
                    msg.what = EVENT_DISPLAY_TIME;
                    mHandler.sendMessage(msg);
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }
	
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_DISPLAY_TIME:
                    CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
                    mLeaveTime=sysTimeStr.toString();
                    mLeaveTimeTV.setText("离场时间：" + mLeaveTime);
                    break;
                case EVENT_DISPLAY_INFORMATION:
    				  mLicenseNumberTV.setText("车牌号: " + mLicensePlateNumber);
      				  mParkNameTV.setText("车场名称: " + mParkName);
      				  mParkNumberTV.setText("车场编号: " + mParkNumber);
      				  mLocationNumberTV.setText("泊位号: " + mLocationNumber);
      		    	  mCarTypeTV.setText("车辆类型: " + mCarType);
      		          mParkTypeTV.setText("泊车类型: " + mParkType);
      		          mFeeScaleTV.setText("收费标准: " + mFeeScale);
      		          mStartTimeTV.setText("入场时间: " + mStartTime);
      		          new TimeThread().start();
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
	 * Add for request detail for charge
	 * */
	public boolean clientQuery() throws ParseException, IOException, JSONException{
		Log.e(LOG_TAG,"clientQuery->enter clientQueryParkingDetail");  
		HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
		  String strurl = "http://" + this.getString(R.string.ip) + ":8080/itspark/owner/parkingInformation/query";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  //request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  LotDetailQueryInfo info = new LotDetailQueryInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_QUERY_PARKING_INFORMATION_CODE, mTeleNumber, readToken());
		  info.setHeader(header);
		  info.setParkNumber(mParkNumber);
		  info.setParkingLocation(mLocationNumber);
		  StringEntity se = new StringEntity( JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientQuery-> param is " + JacksonJsonUtil.beanToJson(info));
		  request.setEntity(se);
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientQuery->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  if(res.getResCode().equals("100")){
	  		          mFeeScale =  (String)res.getPropertyMap().get("feeScale");
					  mCarType = (String)res.getPropertyMap().get("carType");
					  mParkType = (String)res.getPropertyMap().get("parkType");
					  return true;
				  }else if(res.getResCode().equals("201")){
			          return false;
				  } 
			}else{
				  Log.e(LOG_TAG, "clientQuery->error code is " + Integer.toString(code));
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
	 * 查询j结算信息Task
	 * 
	 */
	public class UserQueryTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				Log.e(LOG_TAG,"UserQueryTask-> doInBackground");  
				return clientQuery();
			}catch(Exception e){
				Log.e(LOG_TAG,"UserQueryTask-> exists exception ");  
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mQueryTask = null;
			if(success){
			    Message msg = new Message();
			    msg.what=EVENT_DISPLAY_INFORMATION;
			    mHandler.sendMessage(msg);	
			}
		}

		@Override
		protected void onCancelled() {
			mQueryTask = null;
		}
		
	}
	
	 //封装Toast
	 private void toastWrapper(final String str) {
	      runOnUiThread(new Runnable() {
	          @Override
	           public void run() {
	               Toast.makeText(ParkingInformationActivity.this, str, Toast.LENGTH_SHORT).show();
	           }
	      });
	 }
	 
	    public String readToken() {
	        SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
	        String str = pref.getString("token", "");
	        return str;
	    }
}
