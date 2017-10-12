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

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.example.driver.R;
import com.example.driver.R.color;
import com.example.driver.R.id;
import com.example.driver.R.layout;
import com.example.driver.R.string;
import com.example.driver.common.JacksonJsonUtil;
import com.example.driver.common.UserDbAdapter;
import com.example.driver.info.BindLicenseInfo;
import com.example.driver.info.CommonRequestHeader;
import com.example.driver.info.CommonResponse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InputLicenseActivity extends FragmentActivity {
	private static final int LICENSE_PLATE_NUMBER_SIZE=7;
	private static final int EVENT_BIND_SUCCESS=201;
	private static final int EVENT_BIND_FAIL=202;
	private static final int EVENT_EXIST_LICENSE_PLATE=203;
	private static final int EVENT_INVALID_LICENSE_PLATE=204;
	private static final int EVENT_BIND_FULL=205;
	private static final int EVENT_NOTIFY_CHOOSE_CAR_TYPE =  206;
    private static final String FILE_NAME_TOKEN = "save_pref_token";
    private static final String LOG_TAG = "InputLicenseActivity";
	private String mTeleNumber;
	private String mCarType = null;
	private Fragment mNumberFragment;
	private Fragment mLetterFragment;
	private Fragment mLocationFragment;
	private Fragment mCarTypeFragment;
	private EditText mLicensePlateET;
	private TextView mNumberTV;
	private TextView mLetterTV;
	private TextView mLocationTV;
	private TextView mCarTypeTV;
	private Button mConfirmBT;
	private int mCurrentId;
	private int mType;
    private LicenseTask mLicenseTask = null;
	private UserDbAdapter mUserDbAdapter;
	private OnClickListener mTabClickListener = new OnClickListener() {
        @Override  
        public void onClick(View v) {  
            if (v.getId() != mCurrentId) {//如果当前选中跟上次选中的一样,不需要处理  
                changeSelect(v.getId());//改变图标跟文字颜色的选中   
                changeFragment(v.getId());//fragment的切换  
                mCurrentId = v.getId();//设置选中id  
            }  
        }  
    };  
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        mUserDbAdapter = new UserDbAdapter(this);
        Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mTeleNumber=bundle.getString("telenumber");
        setContentView(R.layout.activity_input_license);
        mLicensePlateET = (EditText) findViewById(R.id.et_input_license_plate);
        mLetterTV = (TextView) findViewById(R.id.tv_letter_input_license_title);
        mNumberTV = (TextView) findViewById(R.id.tv_number_input_license_title);
        mLocationTV = (TextView) findViewById(R.id.tv_location_input_license_title);
		mCarTypeTV= (TextView) findViewById(R.id.tv_car_type_input);
        mConfirmBT = (Button) findViewById(R.id.bt_confirm__input_license_title);
    	mLocationTV.setOnClickListener(mTabClickListener);
        mLetterTV.setOnClickListener(mTabClickListener); 
    	mNumberTV.setOnClickListener(mTabClickListener);
		mCarTypeTV.setOnClickListener(mTabClickListener);
    	changeSelect(R.id.tv_location);
    	changeFragment(R.id.tv_location);
    	mLicensePlateET.setOnTouchListener(new OnTouchListener() {	 
    	      @Override
    	      public boolean onTouch(View v, MotionEvent event) {
    	        // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
    	        Drawable drawable = mLicensePlateET.getCompoundDrawables()[2];
    	        //如果右边没有图片，不再处理
    	        if (drawable == null)
    	            return false;
    	        //如果不是按下事件，不再处理
    	        if (event.getAction() != MotionEvent.ACTION_UP)
    	            return false;
    	        if (event.getX() > mLicensePlateET.getWidth()
                   -mLicensePlateET.getPaddingRight()
    	           - drawable.getIntrinsicWidth()){
    	        	mLicensePlateET.setText("");
    	        }
    	          return false;
    	      }
    	    });
    	mLicensePlateET.setInputType(InputType.TYPE_NULL);
    	mLicensePlateET.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	if(mLicensePlateET.getText()!=null && mLicensePlateET.getText().length()>LICENSE_PLATE_NUMBER_SIZE){
            		Message msg = new Message();
            		msg.what=EVENT_INVALID_LICENSE_PLATE;
            		mHandler.sendMessage(msg);
            	}
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
    	});
    
    	mConfirmBT.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				if(mLicensePlateET.getText().length() !=LICENSE_PLATE_NUMBER_SIZE){
            		Message msg = new Message();
            		msg.what=EVENT_INVALID_LICENSE_PLATE;
            		mHandler.sendMessage(msg);
            		return;
				}
				if(mCarType == null || "".equals(mCarType)){
					Message msg = new Message();
					msg.what=EVENT_NOTIFY_CHOOSE_CAR_TYPE;
					mHandler.sendMessage(msg);
					return;
				}
				//new SQLThread().start();
		        mLicenseTask = new LicenseTask();
		        mLicenseTask.execute((Void) null);
			}
		});
    	getActionBar().setDisplayHomeAsUpEnabled(true); 
	     IntentFilter filter = new IntentFilter();  
	     filter.addAction("ExitApp");  
	     filter.addAction("BackMain");  
	     registerReceiver(mReceiver, filter);
	}

	private void changeFragment(int resId) {  
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();//开启一个Fragment事务  
        hideFragments(transaction);//隐藏所有fragment  
        if(resId==R.id.tv_location_input_license_title){
            if(mLocationFragment==null){//如果为空先添加进来.不为空直接显示  
            	mLocationFragment = new LocationFragment();  
                transaction.add(R.id.main_container,mLocationFragment);  
            }else {  
                transaction.show(mLocationFragment);  
            }
        }else if(resId==R.id.tv_letter_input_license_title){
            if(mLetterFragment==null){//如果为空先添加进来.不为空直接显示  
            	mLetterFragment = new LetterFragment();  
                transaction.add(R.id.main_container,mLetterFragment);  
            }else {  
                transaction.show(mLetterFragment);  
            }
        }else if(resId==R.id.tv_number_input_license_title){
            if(mNumberFragment==null){//如果为空先添加进来.不为空直接显示  
            	mNumberFragment = new NumberFragment();  
                transaction.add(R.id.main_container,mNumberFragment);  
            }else {  
                transaction.show(mNumberFragment);  
            }
        }else if(resId==R.id.tv_car_type_input){
			if(mCarTypeFragment==null){//如果为空先添加进来.不为空直接显示
				mCarTypeFragment = new CarTypeFragment();
				transaction.add(R.id.main_container,mCarTypeFragment);
			}else {
				transaction.show(mCarTypeFragment);
			}
		}
        transaction.commit();//一定要记得提交事务  
    }

	private void hideFragments(FragmentTransaction transaction){  
        if (mLetterFragment != null)  
            transaction.hide(mLetterFragment);
        if (mNumberFragment != null)
            transaction.hide(mNumberFragment);
        if (mLocationFragment != null)
            transaction.hide(mLocationFragment);
		if(mCarTypeFragment != null)
			transaction.hide(mCarTypeFragment);
    }

	private void changeSelect(int resId) {  
		mLetterTV.setSelected(false);
		mLetterTV.setBackgroundResource(R.color.gray);
		mNumberTV.setSelected(false);
		mNumberTV.setBackgroundResource(R.color.gray);
		mLocationTV.setSelected(false);
		mLocationTV.setBackgroundResource(R.color.gray);
		mCarTypeTV.setSelected(false);
		mCarTypeTV.setBackgroundResource(R.color.gray);
        switch (resId) {  
            case R.id.tv_location_input_license_title:  
        	    mLocationTV.setSelected(true);  
        	    mLocationTV.setBackgroundResource(R.color.orange);
                break;  
            case R.id.tv_letter_input_license_title:  
        	    mLetterTV.setSelected(true);  
        	    mLetterTV.setBackgroundResource(R.color.orange);
                break;  
            case R.id.tv_number_input_license_title:  
        	    mNumberTV.setSelected(true);  
        	    mNumberTV.setBackgroundResource(R.color.orange);
                break;
			case R.id.tv_car_type_input:
				mCarTypeTV.setSelected(true);
				mCarTypeTV.setBackgroundResource(R.color.orange);
				break;
		}
    }

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_BIND_SUCCESS:
                	Toast.makeText(getApplicationContext(), "绑定成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(InputLicenseActivity.this,LicensePlateManagementActivity.class);
    				Bundle bundle = new Bundle();
    				bundle.putString("telenumber", mTeleNumber);
    				intent.putExtras(bundle);
       	        	startActivity(intent); 
       	        	finish();
                	break;
                case EVENT_BIND_FAIL:
                	Toast.makeText(getApplicationContext(), "绑定失败", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_EXIST_LICENSE_PLATE:
                	Toast.makeText(getApplicationContext(), "该牌照已被绑定", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_INVALID_LICENSE_PLATE:
                    Toast.makeText(getApplicationContext(), "请输入正确牌照", Toast.LENGTH_SHORT).show();
            	    break;
                 case EVENT_BIND_FULL:
                     Toast.makeText(getApplicationContext(), "无法绑定新的牌照", Toast.LENGTH_SHORT).show();
             	    break;
				case EVENT_NOTIFY_CHOOSE_CAR_TYPE:
					Toast.makeText(getApplicationContext(), "请选择车辆类型", Toast.LENGTH_SHORT).show();
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
	 * Add for bind license and receive result  
	 * */
	public boolean clientSendLicense()throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT,5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置,"0"代表永不超时
		  String strurl = "http://" + this.getString(R.string.ip) + "/itspark/owner/license/bind";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  BindLicenseInfo info = new BindLicenseInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_BIND_LICENSE, mTeleNumber, readToken());
		  info.setHeader(header);
		  info.setLicensePlateBind(mLicensePlateET.getText().toString());
		  info.setCarType(mCarType);
		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientSendLicense-> param is " + JacksonJsonUtil.beanToJson(info));
		  request.setEntity(se);//发送数据
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
 				  Log.e(LOG_TAG,"clientSendLicense->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  String resCode = res.getResCode();
				  toastWrapper(res.getResMsg());  
				  if(resCode.equals("100")){
					  return true;
				  }else{
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
	 * 牌照Task
	 * 
	 */
	public class LicenseTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				Log.e(LOG_TAG,"LicenseTask->doInBackground");  
				return clientSendLicense();
			}catch(Exception e){
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			 mLicenseTask = null;
			 Log.e(LOG_TAG,"LicenseTask->onPostExecute " + success.toString());  
			if(success){
        		Message msg = new Message();
        		msg.what=	EVENT_BIND_SUCCESS;
        		mHandler.sendMessage(msg);
			}
		}

		@Override
		protected void onCancelled() {
			mLicenseTask = null;
		}
		
	}
	
 	/**
 	 * 封装Toast
 	 * */
 	 private void toastWrapper(final String str) {
 	      runOnUiThread(new Runnable() {
 	          @Override
 	           public void run() {
 	               Toast.makeText(InputLicenseActivity.this, str, Toast.LENGTH_SHORT).show();
 	           }
 	      });
 	 }
 	 
    public String readToken() {
        SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
        String str = pref.getString("token", "");
        return str;
    }

	public void setCarType(String type){
		mCarType = type;
	}
}
