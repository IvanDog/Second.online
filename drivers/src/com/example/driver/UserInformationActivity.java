package com.example.driver;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
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

import com.example.driver.R.drawable;
import com.example.driver.SetNickNameActivity.UserSetTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserInformationActivity extends Activity {
	private UserDbAdapter mUserDbAdapter;
	private UserInformationListAdapter mUserInformationListAdapter;
	private ListView mListView;
	private String mTeleNumber;
	private Context mContext;
	private String mNickName = null;
	private Drawable mHeadPortrait = null;
	private final static int EVENT_UPDATE_DISPLAY = 101;
    private static final String FILE_NAME_TOKEN = "save_pref_token";
    private static final String LOG_TAG = "UserInformationActivity";
    private UserInformationDisplayTask mDisplayInformationTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_information);
		mUserDbAdapter = new UserDbAdapter(this);
		mUserDbAdapter.open();
		mContext = this;
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	mTeleNumber = bundle.getString("telenumber");
        }
		mListView=(ListView)findViewById(R.id.list_user_information);  
        mListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
             	Map<String,Object> map=(Map<String,Object>)mListView.getItemAtPosition(arg2);
                String userInformation=(String)map.get("userInformation");
                if(userInformation.equals("头像: ")){
                 	Intent intent = new Intent(UserInformationActivity.this,HeadPortraitActivity.class);
                 	Bundle bundle = new Bundle();
                 	bundle.putString("telenumber", mTeleNumber);
                 	intent.putExtras(bundle);
                 	startActivity(intent);
                 }else if(userInformation.equals("昵称: ")){
                 	Intent intent = new Intent(UserInformationActivity.this,SetNickNameActivity.class);
                 	Bundle bundle = new Bundle();
                 	bundle.putString("telenumber", mTeleNumber);
                 	intent.putExtras(bundle);
                 	startActivity(intent);
                 }else if(userInformation.equals("重置密码: ")){
                	Intent intent = new Intent(UserInformationActivity.this,ResetPasswdActivity.class);
                	Bundle bundle = new Bundle();
                	bundle.putString("telenumber", mTeleNumber);
                	intent.putExtras(bundle);
                	startActivity(intent);
                 }
            }
        });
        mDisplayInformationTask = new UserInformationDisplayTask();
        mDisplayInformationTask.execute((Void) null);
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	     IntentFilter filter = new IntentFilter();  
	     filter.addAction("ExitApp");  
	     filter.addAction("BackMain");  
	     registerReceiver(mReceiver, filter);
	}
	
    public List<Map<String, Object>> getUserInformationData(){  
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        for (int i = 1; i <= 4; i++) {  
            Map<String, Object> map=new HashMap<String, Object>();  
            if(i==1){
                map.put("userInformation",  "头像: ");
                map.put("userInformationDetail",  mHeadPortrait);
                map.put("userInformationSpreadImage",  drawable.ic_chevron_right_black_24dp);
            }else if(i==2){
                map.put("userInformation",  "昵称: ");
                map.put("userInformationDetail",  /*getNickName()*/mNickName);
                map.put("userInformationSpreadImage",  drawable.ic_chevron_right_black_24dp);
            }else if(i==3){
            	map.put("userInformation",  "绑定手机: ");
            	map.put("userInformationDetail",  mTeleNumber);
            	map.put("userInformationSpreadImage",  null);
            }else if(i==4){
            	map.put("userInformation",  "重置密码: ");
            	map.put("userInformationDetail",  "**********");
            	map.put("userInformationSpreadImage",  drawable.ic_chevron_right_black_24dp);
            }
            list.add(map);  
        }  
        return list;  
      }
    
    public String getNickName(){
		mUserDbAdapter.open();
		Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
		try{
			mNickName = cursor.getString(cursor.getColumnIndex("nickname"));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
        	if(cursor!=null){
        		cursor.close();
            }
		}
		return mNickName;
    }
    
    /*public Drawable getHeadPortrait(){
		mUserDbAdapter.open();
		Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
		try{
			byte[] headPortraitByteArray = cursor.getBlob(cursor.getColumnIndex("headportrait"));
			if(headPortraitByteArray!=null){
				mHeadPortrait=bytes2Drawable(headPortraitByteArray);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
        	if(cursor!=null){
        		cursor.close();
            }
		}
		return mHeadPortrait;
    }*/
    
    // byte[]转换成Drawable  
    public Drawable bytes2Drawable(byte[] b) {  
        Bitmap bitmap = this.bytes2Bitmap(b);  
        return this.bitmap2Drawable(bitmap);  
    }
    
    // byte[]转换成Bitmap  
    public Bitmap bytes2Bitmap(byte[] b) {  
        if (b.length != 0) {  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        }  
        return null;  
    }  
    
    // Bitmap转换成Drawable  
    public Drawable bitmap2Drawable(Bitmap bitmap) {  
        BitmapDrawable bd = new BitmapDrawable(bitmap);  
        Drawable d = (Drawable) bd;  
        return d;  
    } 
    
    /*public class UpdateDisplayThread extends Thread{
    	@Override
    	public void run(){
    		do{
    			Cursor cursor = null;
        		try{
            		cursor = mUserDbAdapter.getUser(mTeleNumber);
            		byte[] headPortraitByteArray = cursor.getBlob(cursor.getColumnIndex("headportrait"));
        			if(headPortraitByteArray!=null){
        				mHeadPortrait=bytes2Drawable(cursor.getBlob(cursor.getColumnIndex("headportrait")));
        			}
            		if((mNickName!=cursor.getString(cursor.getColumnIndex("nickname")))){
            			mNickName = cursor.getString(cursor.getColumnIndex("nickname"));
            		}
    			    Message msg = new Message();
    				msg.what=EVENT_UPDATE_DISPLAY;
    				mHandler.sendMessage(msg);
        			Thread.sleep(1000);
        		}catch(Exception e){
        			e.printStackTrace();
        		}finally{
                	if(cursor!=null){
                		cursor.close();
                    }
        		}
    		}while(true);
    	}
    }*/
    
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_UPDATE_DISPLAY:
        	        List<Map<String, Object>> list=getUserInformationData();  
        	        mUserInformationListAdapter = new UserInformationListAdapter(mContext, list);
        	        mListView.setAdapter(mUserInformationListAdapter); 
        			mUserInformationListAdapter.notifyDataSetChanged();
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
	 	 * Add for request display user information
	 	 * */
	 	public boolean clientQueryUser() throws ParseException, IOException, JSONException{
	 		Log.e("clientQueryUser","enter clientQueryAccount");  
	 		HttpClient httpClient = new DefaultHttpClient();
	 		  httpClient.getParams().setIntParameter(  
	                   HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
	 		  httpClient.getParams().setIntParameter(  
	                   HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
	 		  String strurl = "http://" + this.getString(R.string.ip) + ":8080/itspark/owner/userInformation/query";
	 		  HttpPost request = new HttpPost(strurl);
	 		  request.addHeader("Accept","application/json");
				//request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			  request.setHeader("Content-Type", "application/json; charset=utf-8");
			  JSONObject param = new JSONObject();
			  QueryUserInfo info = new QueryUserInfo();
			  CommonRequestHeader header = new CommonRequestHeader();
			  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_USER_INFORMATION_CODE, mTeleNumber, readToken());
			  //param.put("header", header);
			  info.setHeader(header);
			  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
			  Log.e(LOG_TAG,"clientQueryUser-> param is " + JacksonJsonUtil.beanToJson(info));
	 		  request.setEntity(se);//发送数据
	 		  try{
	 			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
	 			  int code = httpResponse.getStatusLine().getStatusCode();
	 			  if(code==HttpStatus.SC_OK){
	 				  String strResult = EntityUtils.toString(httpResponse.getEntity());
	 				  //Log.e(LOG_TAG,"clientQueryUser->strResult is " + strResult);
	 				  CommonResponse res = new CommonResponse(strResult);
	 				  toastWrapper(res.getResMsg());
	 				  if(res.getResCode().equals("100")){
	         			mNickName = (String)res.getPropertyMap().get("nickName");
	         			byte[] headPortraitByteArray = new byte[1024];
	         			headPortraitByteArray = ((String)res.getPropertyMap().get("headportrait")).getBytes();
	         			if(headPortraitByteArray!=null){
	         				mHeadPortrait=bytes2Drawable(Base64.decode(headPortraitByteArray, Base64.NO_WRAP));
	         			}
	 					  return true;
	 				  }else if(res.getResCode().equals("201")){
	 			          return false;
	 				  }  
	 			}else{
	 					  Log.e(LOG_TAG, "clientQueryUser->error code is " + Integer.toString(code));
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
	 	 * 显示用户信息Task
	 	 */
	 	public class UserInformationDisplayTask extends AsyncTask<Void, Void, Boolean> {
	 		@Override
	 		protected Boolean doInBackground(Void... params) {
	 			try{
	 				Log.e(LOG_TAG,"UserInformationDisplayTask->doInBackground");  
	 				return clientQueryUser();
	 			}catch(Exception e){
	 				Log.e(LOG_TAG,"UserInformationDisplayTask->exists exception ");  
	 				e.printStackTrace();
	 			}
	 			return false;
	 		}

	 		@Override
	 		protected void onPostExecute(final Boolean success) {
	 			mDisplayInformationTask = null;
	 			if(success){
			    	Message msg = new Message();
			    	msg.what = EVENT_UPDATE_DISPLAY;
			    	mHandler.sendMessage(msg);
	 			}
	 		}

	 		@Override
	 		protected void onCancelled() {
	 			mDisplayInformationTask = null;
	 		}
	 		
	 	}
	 	
		 //封装Toast
		 private void toastWrapper(final String str) {
		      runOnUiThread(new Runnable() {
		          @Override
		           public void run() {
		               Toast.makeText(UserInformationActivity.this, str, Toast.LENGTH_SHORT).show();
		           }
		      });
		 }
		 
	    public String readToken() {
	        SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
	        String str = pref.getString("token", "");
	        return str;
	    }
}
