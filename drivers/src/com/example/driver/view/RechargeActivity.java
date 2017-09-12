package com.example.driver.view;

import java.io.IOException;
import java.io.InterruptedIOException;
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

import com.alipay.sdk.app.PayTask;
import com.example.driver.R;
import com.example.driver.R.id;
import com.example.driver.R.layout;
import com.example.driver.R.string;
import com.example.driver.common.JacksonJsonUtil;
import com.example.driver.common.UserDbAdapter;
import com.example.driver.info.CommonRequestHeader;
import com.example.driver.info.CommonResponse;
import com.example.driver.info.QueryBalanceInfo;
import com.example.driver.info.RechargeInfo;
import com.example.driver.view.payment.AlipayResult;


import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RechargeActivity extends Activity {
	private static final int EVENT_PAYMENT_FINISHED=101;
	private static final int EVENT_UPDATE_ACCOUNT_RB_STATE=102;
	private static final int EVENT_MOBILE_PAYMENT_SUCCESS=103;
	private static final int EVENT_MOBILE_PAYMENT_FAIL = 104;
	private final static int EVENT_DISPLAY_WALLET_DETAIL_MONEY=105;
	
	private static final int PAYMENT_TYPE_ACCOUNT=201;
	private static final int PAYMENT_TYPE_ALIPAY=202;
	private static final int PAYMENT_TYPE_WECHATPAY=203;
	
	private static final int SDK_PAY_FLAG = 301;
    private static final String FILE_NAME_TOKEN = "save_pref_token";
    private UserRechargeTask mRechargeTask = null;
    private UserAccountDisplayTask  mDisplayAccountTask= null;
	
	private TextView mWalletDetailMoneyTV;
	private Button mTwentyBT;
	private Button mFiftyBT;
	private Button mHundredBT;
	private Button mConfirmPaymentBT;
	private RadioGroup mPaymentTypeRG;
	private RadioButton mAlipayPaymentTypeRB;
	private RadioButton mWechatpayPaymentRB;;
	private String mPaymentPattern;
	private  int mPaymentType;
	private String mAlipayOrderInfo;
	private UserDbAdapter mUserDbAdapter;
	private String mTeleNumber;
	private String mAccountbalance;
	private String mCharge;
	private Context mContext;
	private AlertDialog mDialog;
    private static final String LOG_TAG = "RechargeActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		mContext=this;
		mUserDbAdapter = new UserDbAdapter(this);
		mUserDbAdapter.open();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mTeleNumber=bundle.getString("telenumber");
		mWalletDetailMoneyTV=(TextView)findViewById(R.id.tv_wallet_detail_money_recharge);
		mTwentyBT=(Button)findViewById(R.id.bt_payment_twenty_recharge);
		mTwentyBT.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
		    	if(mFiftyBT.isSelected()){
		    		mFiftyBT.setSelected(false);
		    	}
		    	if(mHundredBT.isSelected()){
		    		mHundredBT.setSelected(false);
		    	}
		    	mTwentyBT.setSelected(!mTwentyBT.isSelected());
		    	mCharge="20";
		    }
		});
		mFiftyBT=(Button)findViewById(R.id.bt_payment_fifty_recharge);
		mFiftyBT.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
		    	if(mTwentyBT.isSelected()){
		    		mTwentyBT.setSelected(false);
		    	}
		    	if(mHundredBT.isSelected()){
		    		mHundredBT.setSelected(false);
		    	}
		    	mFiftyBT.setSelected(!mFiftyBT.isSelected());
		    	mCharge="50";
		    }
		});
		mHundredBT=(Button)findViewById(R.id.bt_payment_hundred_recharge);
		mHundredBT.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
		    	if(mTwentyBT.isSelected()){
		    		mTwentyBT.setSelected(false);
		    	}
		    	if(mFiftyBT.isSelected()){
		    		mFiftyBT.setSelected(false);
		    	}
				mHundredBT.setSelected(!mHundredBT.isSelected());
				mCharge="100";
		    }
		});
		mPaymentTypeRG=(RadioGroup)findViewById(R.id.rg_payment_type_recharge);
		mAlipayPaymentTypeRB=(RadioButton)findViewById(R.id.rb_alipay_payment_recharge);
		mWechatpayPaymentRB=(RadioButton)findViewById(R.id.rb_wechatpay_payment_recharge);
		mPaymentTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
			@Override 
			public void onCheckedChanged(RadioGroup group, int checkedId){
                if (mAlipayPaymentTypeRB.getId() == checkedId){
                	mPaymentType = PAYMENT_TYPE_ALIPAY; 
                	mPaymentPattern = "支付宝支付"; 
			    }else if (mWechatpayPaymentRB.getId() == checkedId){
			    	mPaymentType = PAYMENT_TYPE_WECHATPAY; 
			    	mPaymentPattern = "微信支付"; 
			    }
                mConfirmPaymentBT.setEnabled(true);
			  } 
			});
		mConfirmPaymentBT=(Button)findViewById(R.id.bt_confirm_payment_leaving_recharge);
		mConfirmPaymentBT.setEnabled(false);
		mConfirmPaymentBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
		        mRechargeTask = new UserRechargeTask();
		        mRechargeTask.execute((Void) null);
			}
		});
        mDisplayAccountTask = new UserAccountDisplayTask();
        mDisplayAccountTask.execute((Void) null);
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
                case EVENT_MOBILE_PAYMENT_SUCCESS:
                	showPaymentSuccessDialog();
                	break;
                case EVENT_MOBILE_PAYMENT_FAIL:
                	Toast.makeText(getApplicationContext(), "移动支付失败", Toast.LENGTH_SHORT).show();
                	break;
                case 	EVENT_DISPLAY_WALLET_DETAIL_MONEY:
                	mWalletDetailMoneyTV.setText(mAccountbalance);
                	break;
                case SDK_PAY_FLAG:
                	@SuppressWarnings("unchecked")
                	AlipayResult payResult = new AlipayResult((Map<String, String>) msg.obj);
    				/**
    				 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
    				 */
    				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
    				String resultStatus = payResult.getResultStatus();
    				// 判断resultStatus 为9000则代表支付成功
    				if (TextUtils.equals(resultStatus, "9000")) {
    					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
    					Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
    				} else {
    					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
    					Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
    				}
    				break;
                default:
                    break;
            }
        }
    };
    
    private void showPaymentSuccessDialog(){
    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_charge_success, null); // 加载自定义的布局文件
        TextView chargeSuccessNotifyTV=(TextView)view.findViewById(R.id.tv_charge_success_notify);
        chargeSuccessNotifyTV.setText("成功充值" + mCharge + "元");
		Button confirmChargeSuccessBT=(Button)view.findViewById(R.id.bt_confirm_success_charge);
		confirmChargeSuccessBT.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
		    	finish();
		    }
		});
		AlertDialog.Builder VCdialogBuilder = new AlertDialog.Builder(RechargeActivity.this);
		VCdialogBuilder.setView(view); // 自定义dialog
		mDialog = VCdialogBuilder.create();
		mDialog.show();
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
 	public boolean clientRecharge() throws ParseException, IOException, JSONException{
 		Log.e(LOG_TAG,"clientRecharge->enter clientPayment");  
 		HttpClient httpClient = new DefaultHttpClient();
 		  httpClient.getParams().setIntParameter(  
                   HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
 		  httpClient.getParams().setIntParameter(  
                   HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
 		  String strurl = "http://" + this.getString(R.string.ip) + "/ipippay-pay/owner/recharge/recharge";
 		  HttpPost request = new HttpPost(strurl);
 		  request.addHeader("Accept","application/json");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  RechargeInfo info = new RechargeInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_QUERY_RECHARGE_CODE, mTeleNumber, readToken());
		  info.setHeader(header);
		  info.setRecharge(mCharge);
		  info.setPaymentPattern(convertPayPattToInteger(mPaymentPattern));
 		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
 		  request.setEntity(se);//发送数据
 		  try{
 			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
 			  int code = httpResponse.getStatusLine().getStatusCode();
 			  if(code==HttpStatus.SC_OK){
 				  String strResult = EntityUtils.toString(httpResponse.getEntity());
 				  Log.e(LOG_TAG,"clientRecharge->strResult is " + strResult);
 				  CommonResponse res = new CommonResponse(strResult);
 				  if(res.getResCode().equals("100")){
					  if(mPaymentType == PAYMENT_TYPE_ALIPAY){
						  mAlipayOrderInfo = (String)res.getPropertyMap().get("signedOrderInfo");
					  }
 					  return true;
 				  }else{
 			          return false;
 				  }  
 			}else{
 					  Log.e(LOG_TAG, "clientRecharge->error code is " + Integer.toString(code));
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
 	 * 账户充值Task
 	 */
 	public class UserRechargeTask extends AsyncTask<Void, Void, Boolean> {
 		@Override
 		protected Boolean doInBackground(Void... params) {
 			try{
 				Log.e(LOG_TAG,"UserRechargeTask->doInBackground");  
 				return clientRecharge();
 			}catch(Exception e){
 				Log.e(LOG_TAG,"UserRechargeTask->exists exception ");  
 				e.printStackTrace();
 			}
 			return false;
 		}

 		@Override
 		protected void onPostExecute(final Boolean success) {
 			mRechargeTask = null;
        	if(success){
        		if(mPaymentType==PAYMENT_TYPE_ALIPAY){
		    		Runnable payRunnable = new Runnable() {
		    			@Override
		    			public void run() {
		    				PayTask alipay = new PayTask(RechargeActivity.this);
		    				Map<String, String> result = alipay.payV2(mAlipayOrderInfo , true);
		    				Log.e(LOG_TAG, "Alipay result is " + result.toString());	    				
		    				Message msg = new Message();
		    				msg.what = SDK_PAY_FLAG;
		    				msg.obj = result;
		    				mHandler.sendMessage(msg);
		    			}
		    		};
		    		Thread payThread = new Thread(payRunnable);
		    		payThread.start();
		    	}
		    	/*Message msg = new Message();
		    	msg.what = EVENT_MOBILE_PAYMENT_SUCCESS;
		    	mHandler.sendMessage(msg);*/
        	}else{
		    	Message msg = new Message();
		    	msg.what = EVENT_MOBILE_PAYMENT_FAIL;
		    	mHandler.sendMessage(msg);
        	}
 		}

 		@Override
 		protected void onCancelled() {
 			mRechargeTask = null;
 		}
 		
 	}
 	
    /**
 	 * Add for request display account state
 	 * */
 	public boolean clientQueryBalance() throws ParseException, IOException, JSONException{
 		Log.e(LOG_TAG,"clientQueryAccount->enter clientQueryAccount");  
 		HttpClient httpClient = new DefaultHttpClient();
 		  httpClient.getParams().setIntParameter(  
                   HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
 		  httpClient.getParams().setIntParameter(  
                   HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
 		  String strurl = "http://" + this.getString(R.string.ip) + "/ipippay-pay/owner/recharge/queryBalance";
 		  HttpPost request = new HttpPost(strurl);
 		  request.addHeader("Accept","application/json");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  QueryBalanceInfo info = new QueryBalanceInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_QUERY_ACCOUNT_CODE, mTeleNumber, readToken());
		  info.setHeader( header);
		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
 		  request.setEntity(se);//发送数据
 		  try{
 			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
 			  int code = httpResponse.getStatusLine().getStatusCode();
 			  if(code==HttpStatus.SC_OK){
 				  String strResult = EntityUtils.toString(httpResponse.getEntity());
 				  Log.e(LOG_TAG,"clientQueryAccount->strResult is " + strResult);
 				  CommonResponse res = new CommonResponse(strResult);
 				  if(res.getResCode().equals("100")){
 					 mAccountbalance = String.valueOf(res.getPropertyMap().get("accountBalance"));
 					  return true;
 				  }else{
 			          return false;
 				  }  
 			}else{
 					  Log.e(LOG_TAG, "clientQueryAccount->error code is " + Integer.toString(code));
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
 	 * 显示账户余额Task
 	 */
 	public class UserAccountDisplayTask extends AsyncTask<Void, Void, Boolean> {
 		@Override
 		protected Boolean doInBackground(Void... params) {
 			try{
 				Log.e(LOG_TAG,"UserLicensePlateTask->doInBackground");  
 				return clientQueryBalance();
 			}catch(Exception e){
 				Log.e(LOG_TAG,"UserLicensePlateTask->exists exception ");  
 				e.printStackTrace();
 			}
 			return false;
 		}

 		@Override
 		protected void onPostExecute(final Boolean success) {
 			mDisplayAccountTask = null;
 			if(success){
		    	Message msg = new Message();
		    	msg.what = EVENT_DISPLAY_WALLET_DETAIL_MONEY;
		    	mHandler.sendMessage(msg);
 			}
 		}

 		@Override
 		protected void onCancelled() {
 			mDisplayAccountTask = null;
 		}
 		
 	}
 	
 	/**
 	 * 封装Toast
 	 * */
 	 private void toastWrapper(final String str) {
 	      runOnUiThread(new Runnable() {
 	          @Override
 	           public void run() {
 	               Toast.makeText(RechargeActivity.this, str, Toast.LENGTH_SHORT).show();
 	           }
 	      });
 	 }
 	 
     private String readToken() {
         SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
         String str = pref.getString("token", "");
         return str;
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
