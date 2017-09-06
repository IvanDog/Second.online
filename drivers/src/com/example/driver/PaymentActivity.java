package com.example.driver;

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

import com.example.driver.ParkingInformationActivity.TimeThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentActivity extends Activity {
	private static final int EVENT_PASSWD_EMPTY = 101;
	private static final int EVENT_ACCOUNT_PAYMENT_SUCCESS = 102;
	private static final int EVENT_MOBILE_PAYMENT_SUCCESS=103;
	private static final int EVENT_MOBILE_PAYMENT_FAIL = 104;
	private static final int PAYMENT_TYPE_ACCOUNT=201;
	private static final int PAYMENT_TYPE_ALIPAY=202;
	private static final int PAYMENT_TYPE_WECHATPAY=203;
	private static final int EVENT_DISPLAY_INFORMATION = 301;
	private static final int COUPON_CODE = 401;
	private static final String LOG_TAG = "PaymentActivity";
    private static final String FILE_NAME_TOKEN = "save_pref_token";

	private TextView mLicensePlateNumberTV;
	private TextView mStartTimeTV;
	private TextView mLeaveTimeTV;
	private TextView mFeeScaleTV;
	private TextView mExpenseTV;
	private TextView mExpenseDiscountTV;
	private TextView mExpenseFinalTV;
	private TextView mCouponTV;
	private Button mConfirmPaymentBT;
	private RadioGroup mPaymentTypeRG;
	private RadioButton  mAccountPaymentTypeRB;
	private RadioButton mAlipayPaymentTypeRB;
	private RadioButton mWechatpayPaymentRB;;
	private int mPaymentType;
	private String mTeleNumber;
	private String mParkNumber;
	private String mLicensePlateNumber;
	private int mLocationNumber;
	private String mCarType;
	private String mParkType;
	private String mStartTime;
	private String mLeaveTime;
	private String mFeeScale;
	private String mExpense;
	private String mDiscount;
	private String mExpenseFinal;
	//private boolean mAccountState;
	private String mParkingRecordrID;
	private String mTradeRecordID;
	private String mCouponID;
	private Context mContext;
	private AlertDialog mDialog;
	private UserQueryTask mQueryTask = null;
	private UserPaymentTask mPaymentTask = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		mContext=this;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mTeleNumber=bundle.getString("telenumber");
        mParkNumber = bundle.getString("parkNumber");
        mParkingRecordrID = bundle.getString("parkingRecordrID");
		mLicensePlateNumber = bundle.getString("licensePlateNumber");
		mCarType=bundle.getString("carType");
		mStartTime = bundle.getString("startTime");
		mLeaveTime = bundle.getString("leaveTime");
		mLicensePlateNumberTV=(TextView)findViewById(R.id.tv_license_number_leaving);
		mStartTimeTV=(TextView)findViewById(R.id.tv_start_time_leaving);
		mLeaveTimeTV=(TextView)findViewById(R.id.tv_leave_time_leaving);
		mFeeScaleTV=(TextView)findViewById(R.id.tv_fee_Scale_leaving);
		mExpenseTV=(TextView)findViewById(R.id.tv_expense_leaving);
		mExpenseDiscountTV=(TextView)findViewById(R.id.tv_expense_discount_leaving);
		mExpenseFinalTV=(TextView)findViewById(R.id.tv_expense_final_leaving);
		mCouponTV=(TextView)findViewById(R.id.tv_coupon_choose);
		mCouponTV.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(PaymentActivity.this, ParkingCouponActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("expnese", mExpense);
				intent.putExtras(bundle);
				startActivityForResult(intent,COUPON_CODE);
			}
		});
		mPaymentTypeRG=(RadioGroup)findViewById(R.id.rg_payment_type_leaving);
		mAccountPaymentTypeRB=(RadioButton)findViewById(R.id.rb_account_payment_leaving);
		mAlipayPaymentTypeRB=(RadioButton)findViewById(R.id.rb_alipay_payment_leaving);
		mWechatpayPaymentRB=(RadioButton)findViewById(R.id.rb_wechatpay_payment_leaving);
		mPaymentTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
			@Override 
			public void onCheckedChanged(RadioGroup group, int checkedId){
			    if (mAccountPaymentTypeRB.getId() == checkedId) {
			    	mPaymentType = PAYMENT_TYPE_ACCOUNT; 
		        }else if (mAlipayPaymentTypeRB.getId() == checkedId){
		        	mPaymentType = PAYMENT_TYPE_ALIPAY; 
			    }else if (mWechatpayPaymentRB.getId() == checkedId){
		        	mPaymentType = PAYMENT_TYPE_WECHATPAY; 
			    }
			    mConfirmPaymentBT.setEnabled(true);
			  } 
			});
		mConfirmPaymentBT=(Button)findViewById(R.id.bt_confirm_payment_leaving);
		mConfirmPaymentBT.setEnabled(false);
		mConfirmPaymentBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(mPaymentType==PAYMENT_TYPE_ACCOUNT){
					showAccountPaymentDialog();
				}else if(mPaymentType==PAYMENT_TYPE_ALIPAY){
					makeMobilePay(PAYMENT_TYPE_ALIPAY);
				}else if(mPaymentType==PAYMENT_TYPE_WECHATPAY){
					makeMobilePay(PAYMENT_TYPE_WECHATPAY);
				}
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

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_DISPLAY_INFORMATION:
        		    mLicensePlateNumberTV.setText("牌照: " + mLicensePlateNumber);
        		    mStartTimeTV.setText("入场时间: " + mStartTime);
        		    mLeaveTimeTV.setText("离场时间: " + mLeaveTime);
        		    mFeeScaleTV.setText("收费标准: " + mFeeScale);
        		    mExpenseTV.setText("停车费用: " +mExpense + "元");
				    mExpenseDiscountTV.setText("停车券抵扣: " + mDiscount + "元" );
				    mExpenseFinalTV.setText("支付金额: " + mExpenseFinal + "元");
            	    break;
                case EVENT_PASSWD_EMPTY:
                	Toast.makeText(getApplicationContext(), "密码为空", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_ACCOUNT_PAYMENT_SUCCESS:
                	mDialog.dismiss();
            		Intent accountIntent = new Intent(PaymentActivity.this,PaymentSuccessActivity.class);
            		Bundle accountBundle = new Bundle();
            		accountBundle.putString("expense", mExpenseFinal);
            		accountBundle.putString("telenumber", mTeleNumber);
            		accountIntent.putExtras(accountBundle);
            		startActivity(accountIntent);
                	break;
                case EVENT_MOBILE_PAYMENT_SUCCESS:
            		Intent mobileIntent = new Intent(PaymentActivity.this,PaymentSuccessActivity.class);
            		Bundle mobileBundle = new Bundle();
            		mobileBundle.putString("expense", mExpense);
            		mobileBundle.putString("telenumber", mTeleNumber);
            		mobileIntent.putExtras(mobileBundle);
            		startActivity(mobileIntent);
                	break;
                case EVENT_MOBILE_PAYMENT_FAIL:
                	mDialog.dismiss();
                	Toast.makeText(getApplicationContext(), "移动支付失败", Toast.LENGTH_SHORT).show();
                	break;
                default:
                    break;
            }
        }
    };
    
    private void showAccountPaymentDialog(){
    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_account_payment, null); // 加载自定义的布局文件
		final EditText passwdET = (EditText)view.findViewById(R.id.et_input_passwd);
		final Button finishPaymentBT=(Button)view.findViewById(R.id.bt_finish_account_payment);
		final AlertDialog.Builder VCdialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
		VCdialogBuilder.setView(view); // 自定义dialog
		finishPaymentBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
            	if(passwdET.getText().toString().equals("")){
            		Message msg = new Message();
            		msg.what = EVENT_PASSWD_EMPTY;
            		mHandler.sendMessage(msg);
            	}else{
    				mPaymentTask = new UserPaymentTask(passwdET.getText().toString(),"余额支付");
    				mPaymentTask.execute((Void) null);
            	}
			}
		});
		mDialog = VCdialogBuilder.create();
		mDialog.show();
    }

    public void makeMobilePay(int mPaymentType){
    	if(mPaymentType == PAYMENT_TYPE_WECHATPAY){
			mPaymentTask = new UserPaymentTask(null,"微信支付");
			mPaymentTask.execute((Void) null);
    	}else if(mPaymentType == PAYMENT_TYPE_ALIPAY){
			mPaymentTask = new UserPaymentTask(null,"支付宝支付");
			mPaymentTask.execute((Void) null);
    	}
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
    
    /**
	 * Add for payment
	 * */
	public boolean clientPayment(String password, String paymentPattern) throws ParseException, IOException, JSONException{
		Log.e(LOG_TAG,"enter clientPayment");  
		HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
		  String strurl = "http://" + this.getString(R.string.ip) + "/itspark/owner/payment/pay";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  PaymentInfo info = new PaymentInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_PAY_CODE, mTeleNumber, readToken());
		  info.setHeader(header);
		  info.setParkNumber(mParkNumber);
		  info.setLicensePlateNumber(mLicensePlateNumber);
		  info.setPassword(getMD5Code(password));
		  info.setParkingRecordID(mParkingRecordrID);
		  info.setTradeRecordID(mTradeRecordID);
		  info.setPaymentPattern(convertPayPattToInteger(paymentPattern));
		  info.setPaidMoney(mExpenseFinal);
		  info.setCouponID(mCouponID);
		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientPayment-> param is " + JacksonJsonUtil.beanToJson(info));
		  request.setEntity(se);//发送数据
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientPayment->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  toastWrapper(res.getResMsg());
				  if(res.getResCode().equals("100")){
					  return true;
				  }else if(res.getResCode().equals("201")){
			          return false;
				  } 
			}else{
					  Log.e(LOG_TAG, "clientPayment->error code is " + Integer.toString(code));
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
	 * 结算Task
	 * 
	 */
	public class UserPaymentTask extends AsyncTask<Void, Void, Boolean> {
		private String password;
		private String paymentPattern;
		public UserPaymentTask(String password,String paymentPattern){
			this.password = password;
			this.paymentPattern = paymentPattern;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				Log.e(LOG_TAG,"UserPaymentTask->doInBackground");  
				return clientPayment(password,paymentPattern);
			}catch(Exception e){
				Log.e(LOG_TAG,"UserPaymentTask->exists exception ");  
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mPaymentTask = null;
		    if(success){
		    	if(mPaymentType==PAYMENT_TYPE_ACCOUNT){
	        		Message msg = new Message();
	        		msg.what = EVENT_ACCOUNT_PAYMENT_SUCCESS;
	        		mHandler.sendMessage(msg);
		    	}else if(mPaymentType==PAYMENT_TYPE_WECHATPAY){
		    		//TODO
		    	}else if(PAYMENT_TYPE_ACCOUNT==PAYMENT_TYPE_ALIPAY){
		    		//TODO
		    	}
		    }
		}

		@Override
		protected void onCancelled() {
			mPaymentTask = null;
		}
		
	}
	
    /**
	 * Add for request query detail expense
	 * */
	public boolean clientQueryExpense() throws ParseException, IOException, JSONException{
		Log.e(LOG_TAG,"clientQueryExpense->enter clientQueryExpense");  
		HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
		  String strurl = "http://" + this.getString(R.string.ip) + "/itspark/owner/payment/queryExpense";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  //request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  SettleAccountInfo info = new SettleAccountInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_QUERY_EXPENSE_CODE, mTeleNumber, readToken());
		  info.setHeader(header);
		  info.setParkNumber(mParkNumber);
		  info.setLicensePlateNumber(mLicensePlateNumber);
		  info.setCarType(mCarType);
		  info.setParkingRecordID(String.valueOf(mParkingRecordrID));
		  info.setCouponID(mCouponID);
		  StringEntity se = new StringEntity( JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientQueryExpense-> param is " + JacksonJsonUtil.beanToJson(info));
		  request.setEntity(se);//发送数据
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientQueryExpense->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  toastWrapper(res.getResMsg());
				  if(res.getResCode().equals("100")){
					  Log.e(LOG_TAG,"clientQueryExpense->res is ok"  );
					  mExpense =  (String)res.getPropertyMap().get("expensePrimary");
					  Log.e(LOG_TAG,"clientQueryExpense->mExpense is" + mExpense );
					  mDiscount =  (String)res.getPropertyMap().get("discount");
					  Log.e(LOG_TAG,"clientQueryExpense->mDiscount is" + mDiscount );
					  mExpenseFinal =  (String)res.getPropertyMap().get("expenseFinal");
					  Log.e(LOG_TAG,"clientQueryExpense->mExpenseFinal is" + mExpenseFinal );
					  mFeeScale =  (String)res.getPropertyMap().get("feeScale");
					  Log.e(LOG_TAG,"clientQueryExpense->mFeeScale is" + mFeeScale );
					  mTradeRecordID =  (String)res.getPropertyMap().get("tradeRecordID");
					  Log.e(LOG_TAG,"clientQueryExpense->mTradeRecordID is" + mTradeRecordID);
					  return true;
				  }else{
			          return false;
				  } 
			}else{
					  Log.e(LOG_TAG, "clientQueryExpense->error code is " + Integer.toString(code));
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
				Log.e(LOG_TAG,"UserQueryTask->doInBackground");  
				return clientQueryExpense();
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
	
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        switch (requestCode) {  
        case COUPON_CODE:  
        	mCouponID = data.getStringExtra("couponID");
    		mQueryTask = new UserQueryTask();
    		mQueryTask.execute((Void) null);
            break;   
        default:  
            break;  
        }  
    } 
    
    
	 //封装Toast
	 private void toastWrapper(final String str) {
	      runOnUiThread(new Runnable() {
	          @Override
	           public void run() {
	               Toast.makeText(PaymentActivity.this, str, Toast.LENGTH_SHORT).show();
	           }
	      });
	 }
	 
    private String readToken() {
        SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
        String str = pref.getString("token", "");
        return str;
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
	
	public Integer convertPayPattToInteger(String paymentPattern){
        if("pos机支付".equals(paymentPattern)){
				return 1;
			}else if("微信支付".equals(paymentPattern)){
				return 2;
			}else if("支付宝支付".equals(paymentPattern)){
				return 3;
			}else if("微信扫码支付".equals(paymentPattern)){
				return 4;
			}else if("支付宝扫码支付".equals(paymentPattern)){
				return 5;
			}else if("微信刷卡支付".equals(paymentPattern)){
				return 6;
			}else if("支付宝条码支付".equals(paymentPattern)){
				return 7;
			}else if("余额支付".equals(paymentPattern)){
				return 8;
			}else if("逃费".equals(paymentPattern)){
				return 9;
			}else{
				return 0;
			}
		}
}
