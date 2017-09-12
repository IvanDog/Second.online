package com.example.driver.view;

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

import com.example.driver.R;
import com.example.driver.R.id;
import com.example.driver.R.layout;
import com.example.driver.R.string;
import com.example.driver.common.UserDbAdapter;
import com.example.driver.info.CommonRequestHeader;
import com.example.driver.info.CommonResponse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class RecordFragment extends Fragment {
	public static final int TYPE_CURRENT_RECORD = 101;
	public static final int TYPE_HISTORY_RECORD = 102;
	public static final int NO_PARKING_RECORD =201;
	public static final int EVENT_DISPLAY_QUERY_RESULT = 301;
	public static final int EVENT_DISPLAY_REQUEST_TIMEOUT = 302;
	public static final int EVENT_DISPLAY_CONNECT_TIMEOUT = 303;
	public static final int EVENT_SET_ADAPTER = 304;
	public static final String LOG_TAG = "RecordFragment";
    private ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
    private RecordListAdapter mRecordListAdapter;
	private View mView;
	private ListView mListView;
	private TextView mEmptyNotifyTV;;
	private int mType;
	private boolean mEqual;
	private UserDbAdapter mUserDbAdapter;
    private UserQueryTask mQueryTask = null;
	public RecordFragment(int type,boolean equal){
		mType = type;
		mEqual = equal;
	}
	 @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	mView = inflater.inflate(R.layout.fragment_record, container, false);
	        mListView=(ListView)mView.findViewById(R.id.list_record);  
	        mEmptyNotifyTV=(TextView)mView.findViewById(R.id.tv_empty_list_notify_record);  
	        mUserDbAdapter = new UserDbAdapter(getActivity());
			mQueryTask = new UserQueryTask(mType);
			mQueryTask.execute((Void) null);
	        return mView;
	    }

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	    }

	    @Override
	    public void onStart() {
	        super.onStart();
	    }

	    @Override
	    public void onResume() {
	        super.onResume();
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	    }

	    @Override
	    public void onDestroyView() {
	        super.onDestroyView();
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	    }

	    @Override
	    public void onDetach() {
	        super.onDetach();
	    }	    
	    
		private Handler mHandler = new Handler() {
		    @Override
		    public void handleMessage (Message msg) {
		        super.handleMessage(msg);
		        switch (msg.what) {
		            case NO_PARKING_RECORD:
		            	mEmptyNotifyTV.setVisibility(View.VISIBLE);
		                break;
		            case EVENT_DISPLAY_QUERY_RESULT:
		            	Toast.makeText(getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
		            	break;
		            case EVENT_DISPLAY_REQUEST_TIMEOUT:
		            	Toast.makeText(getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
		            	break;
		            case EVENT_DISPLAY_CONNECT_TIMEOUT:
		            	Toast.makeText(getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
		            	break;
		            case EVENT_SET_ADAPTER:
		            	mRecordListAdapter= new RecordListAdapter(getActivity(), mList);
		    	        mListView.setAdapter(mRecordListAdapter); 
		    	        mRecordListAdapter.notifyDataSetChanged();
		            	break;
		            default:
		                break;
		        }
		    }
		};
		
	    /**
		 * Add for request parking record and receive result
		 * */
		public boolean clientQuery(int type) throws ParseException, IOException, JSONException{
			Log.e(LOG_TAG,"clientQuery->enter clientQuery");  
			HttpClient httpClient = new DefaultHttpClient();
			  httpClient.getParams().setIntParameter(  
	                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
			  httpClient.getParams().setIntParameter(  
	                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
			  String strurl = "http://" + this.getString(R.string.ip) + ":8080/park/owner/queryRecord/query";
			  HttpPost request = new HttpPost(strurl);
			  request.addHeader("Accept","application/json");
				//request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			  request.setHeader("Content-Type", "application/json; charset=utf-8");
			  JSONObject param = new JSONObject();
			  CommonRequestHeader header = new CommonRequestHeader();
			  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_MESSAGE_CENTER_CODE, 
					  ((ParkingRecordActivity)getActivity()).getAccount(), ((ParkingRecordActivity)getActivity()).readToken());
			  param.put("header", header);
			  param.put("searchType", type);
			  StringEntity se = new StringEntity(param.toString(), "UTF-8");
			  request.setEntity(se);//发送数据
			  try{
				  HttpResponse httpResponse = httpClient.execute(request);//获得响应
				  int code = httpResponse.getStatusLine().getStatusCode();
				  if(code==HttpStatus.SC_OK){
					  String strResult = EntityUtils.toString(httpResponse.getEntity());
					  Log.e(LOG_TAG,"clientQuery->strResult is " + strResult);
					  CommonResponse res = new CommonResponse(strResult);
					  Message msg = new Message();
			          msg.what=EVENT_DISPLAY_QUERY_RESULT;
			          msg.obj= res.getResMsg();
			          mHandler.sendMessage(msg);
					  if(res.getResCode().equals("100")){
						  mList = res.getDataList();
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
					  Message msg = new Message();
			          msg.what=EVENT_DISPLAY_CONNECT_TIMEOUT;
			          msg.obj= "连接超时";
			          mHandler.sendMessage(msg);
				  }else if(e instanceof InterruptedIOException){
					  Message msg = new Message();
			          msg.what=EVENT_DISPLAY_REQUEST_TIMEOUT;
			          msg.obj="请求超时";
			          mHandler.sendMessage(msg);
				  }
	          }finally{  
	        	  httpClient.getConnectionManager().shutdown();  
	          }  
			  return false;
	    }
		
		/**
		 * Represents an asynchronous registration task used to authenticate
		 * the user.
		 */
		public class UserQueryTask extends AsyncTask<Void, Void, Boolean> {
			private int type;
			public UserQueryTask(int type){
				this.type = type;
			}
			@Override
			protected Boolean doInBackground(Void... params) {
				try{
					Log.e(LOG_TAG,"UserQueryTask->doInBackground");  
					return clientQuery(type);
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
				    if(mList.size()!=0){
					    Message msg = new Message();
					    msg.what=EVENT_SET_ADAPTER;
					    mHandler.sendMessage(msg);
				    }else{
					    Message msg = new Message();
					    msg.what=NO_PARKING_RECORD;
					    mHandler.sendMessage(msg);
				    }					
				}
			}

			@Override
			protected void onCancelled() {
				mQueryTask = null;
			}
			
		}

}
