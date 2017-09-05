package com.example.driver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentSuccessActivity extends Activity {
	private static final int EVENT_PAYMENT_SUCCESS= 101;
	private TextView mPaymentSuccessNotifyTV;
	private Button mPaymentSuccessBT;
	private String mTeleNumber;
	private String mExpense;
	@Override  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_payment_success);
    	Intent intent = getIntent();
		mExpense=intent.getExtras().getString("expense");
		mTeleNumber=intent.getExtras().getString("telenumber");
 		mPaymentSuccessNotifyTV=(TextView)findViewById(R.id.tv_payment_success_notify);
 		mPaymentSuccessNotifyTV.setText("您已成功支付" + mExpense + "元");
        mPaymentSuccessBT=(Button)findViewById(R.id.bt_finish_payment_success);
        mPaymentSuccessBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
				Message msg = new Message();
                msg.what = EVENT_PAYMENT_SUCCESS;
                mHandler.sendMessage(msg);
                
                Intent intentBack = new Intent();
                intentBack.setAction("BackMain");
                sendBroadcast(intentBack);
                
        		Intent intent = new Intent(PaymentSuccessActivity.this,MainActivity.class);
        		Bundle bundle = new Bundle();
        		bundle.putString("telenumber", mTeleNumber);
        		intent.putExtras(bundle);
        		startActivity(intent);
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
                case EVENT_PAYMENT_SUCCESS:
                	Toast.makeText(getApplicationContext(), "收款成功", Toast.LENGTH_SHORT).show();
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
}
