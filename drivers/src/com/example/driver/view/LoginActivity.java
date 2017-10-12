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

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.driver.R;
import com.example.driver.R.id;
import com.example.driver.R.layout;
import com.example.driver.R.string;
import com.example.driver.common.JacksonJsonUtil;
import com.example.driver.common.RSAUtils;
import com.example.driver.common.UserDbAdapter;
import com.example.driver.info.AnalysisInfo;
import com.example.driver.info.CommonRequestHeader;
import com.example.driver.info.CommonResponse;
import com.example.driver.info.LoginInfo;
import com.example.driver.info.RegisterInfo;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyPair;
import java.security.MessageDigest;

public class LoginActivity extends Activity {
	public static String LOG_TAG = "LoginActivity";
	private UserLoginTask mLoginTask = null;

	private String mTeleNumber = "";
	private String mPassword = "";

	private EditText mTeleNumberET;
	private EditText mPasswordET;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
    private CheckBox mKeepUserinfo;
    private CheckBox mKeepPassword;
    private TextView mForgetPasswdTV;
    private Button mRegisterBT;
	private UserDbAdapter mUserDbAdapter;
	private Button mApplyVerificationCodeBT;
	private AlertDialog mDialog;
	private Object mLock = new Object();
	private Thread mTimeThread;
	private  boolean mUpdateTime =true;
	private int mThreadTime = 60;
	private boolean mForget = false;
    private static final String FILE_NAME_NAME_PASSWD = "save_spref_name_passwd";
    private static final String FILE_NAME_TOKEN = "save_pref_token";
	private String mPublicKey;
	private String mPrivateKey;
    private static final int EVENT_EXIST_NUMBER=101;
    private static final int EVENT_EMPTY_TELE_NUMBER=102;
    private static final int EVENT_EMPTY_VERIFICATION_CODE=103;
    private static final int EVENT_VERIFY_SUCCESS=104;
    private static final int EVENT_EMPTY_PASSWD=105;
    private static final int EVENT_EMPTY_RE_PASSWD=106;
    private static final int EVENT_INCONSISTENT_PASSWD=107;
    private static final int EVENT_NO_EXIST_USER=108;
	private static final int EVENT_LOGIN_SUCCESS=109;
    
	private static final int ATTENDANCE_TYPE_START=301;
	private static final int ATTENDANCE_TYPE_END=302;
	private static final int ERROR_TYPE_TELE=401;
	private static final int ERROR_TYPE_PASSWD=402;
	private static final int ERROR_TYPE_NO_ERROR=403;
	private static final int ERROR_TYPE_EMPTY_TELE=404;
	
	private static final int EVENT_UPDATE_TIME=501;
	private int mErrorType = ERROR_TYPE_NO_ERROR;
	
    private UserAnalysisTask mAnalysisTask = null;
    private UserRegisterTask mRegisterTask = null;
	 //这是中国区号，如果需要其他国家列表，可以使用getSupportedCountries();获得国家区号
	 public String mCountryCode = "86";
	 //APPKEY
	 private static String mAppKey = "1ebb1f5523fe0";
	 // 填写从短信SDK应用后台注册得到的APPSECRET
	 private static String mAppSecret = "023214fa9ad7ec7fb2f46828a2485efa";
	 //表示是否使用了registerEventHandler
	 private boolean ready;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
		mUserDbAdapter = new UserDbAdapter(this);
		setContentView(R.layout.activity_login);
		initSDK();
		mTeleNumberET = (EditText) findViewById(R.id.et_tele_login);
		mTeleNumberET.setText(mTeleNumber);

		mPasswordET = (EditText) findViewById(R.id.password);
		mPasswordET
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		mRegisterBT=(Button)findViewById(R.id.register_button);
		mRegisterBT.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	if(mTimeThread!=null){
                	mTimeThread.interrupt();
            	}
		    	mUpdateTime = false;
		    	mThreadTime = 60;
		    	showRegisterDialog(false);
            }
		});
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		mForgetPasswdTV=(TextView)findViewById(R.id.tv_forget_passwd);
		mForgetPasswdTV.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
            	if(mTimeThread!=null){
                	mTimeThread.interrupt();
            	}
		    	mUpdateTime = false;
		    	mThreadTime = 60;
		    	showRegisterDialog(true);
		    }
		});
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
        mKeepUserinfo = (CheckBox) findViewById(R.id.ck_userinfo);
        mKeepPassword = (CheckBox) findViewById(R.id.ck_password);
        mKeepUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBoolean(mKeepUserinfo.isChecked(), mKeepPassword.isChecked());
            }
        });
        mKeepPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBoolean(mKeepUserinfo.isChecked(), mKeepPassword.isChecked());
            }
        });
        initView();
	}

	 // 初始化短信SDK
	 private void initSDK() {
		SMSSDK.initSDK(this, mAppKey, mAppSecret);//初始化SDK 
	    EventHandler eventHandler = new EventHandler() {
	       public void afterEvent(int event, int result, Object data) {
	        if (result == SMSSDK.RESULT_COMPLETE) {  //回调完成
	            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){  //验证码验证成功
            		  Message msg = new Message();
            		  msg.what = EVENT_VERIFY_SUCCESS;
            		  mHandler.sendMessage(msg);
            		  mDialog.dismiss();
            		  toastWrapper("验证成功");
	              }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){  //已发送验证码
	            	  toastWrapper("验证码已经发送");
	              } else{
	                   ((Throwable) data).printStackTrace();
	                   String str = data.toString();
	              }
	        }
	        if(result==SMSSDK.RESULT_ERROR) {  //验证码验证失败
	        	toastWrapper("验证码错误");
	        }
	      }
	  };
	    SMSSDK.registerEventHandler(eventHandler);// 去注册验证码回调监听接口
	    ready = true;
	 }
	 
	 //销毁短信注册
	 @Override
	 protected void onDestroy() {
	      if(ready){
	          SMSSDK.unregisterAllEventHandler();// 去注册短信验证码回调监听接口
	      }
	      super.onDestroy();
	 }
	 
	 //封装Toast
	 private void toastWrapper(final String str) {
	      runOnUiThread(new Runnable() {
	          @Override
	           public void run() {
	               Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();
	           }
	      });
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

    private void initView() {
        if (readBoolean("isuserinfo")) {
        	mKeepUserinfo.setChecked(true);
        	mTeleNumberET.setText(readData("userinfo").toString());
        }
        if (readBoolean("ispassword")) {
        	mKeepPassword.setChecked(true);
            mPasswordET.setText(readData("password").toString());
        }
    }

    private String readData(String data) {
        SharedPreferences pref = getSharedPreferences(FILE_NAME_NAME_PASSWD, MODE_MULTI_PROCESS);
        String str = pref.getString(data, "");
        return str;
    }

    private boolean writeData(String userinfo, String password, boolean isUserinfo, boolean isPassword) {
        SharedPreferences.Editor share_edit = getSharedPreferences(FILE_NAME_NAME_PASSWD,
                MODE_MULTI_PROCESS).edit();
        share_edit.putString("userinfo", userinfo);
        share_edit.putString("password", password);
        share_edit.putBoolean("isuserinfo", isUserinfo);
        share_edit.putBoolean("ispassword", isPassword);
        share_edit.commit();
        return true;
    }

    private boolean readBoolean(String data) {
        SharedPreferences pref = getSharedPreferences(FILE_NAME_NAME_PASSWD, MODE_MULTI_PROCESS);
        return pref.getBoolean(data, false);
    }

    private void writeBoolean(boolean isUserinfo, boolean isPassword) {
        SharedPreferences.Editor share_edit = getSharedPreferences(FILE_NAME_NAME_PASSWD,
                MODE_MULTI_PROCESS).edit();
        share_edit.putBoolean("isuserinfo", isUserinfo);
        share_edit.putBoolean("ispassword", isPassword);
        share_edit.commit();
    }

    private boolean writeToken(String token) {
        SharedPreferences.Editor share_edit = getSharedPreferences(FILE_NAME_TOKEN,
                MODE_MULTI_PROCESS).edit();
        share_edit.putString("token", token);
        share_edit.commit();
        return true;
    }
    
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_NO_EXIST_USER:
            	     Toast.makeText(getApplicationContext(), "该号码不存在", Toast.LENGTH_SHORT).show();
            	    mDialog.dismiss();
                    break;
                case EVENT_EXIST_NUMBER:
                	Toast.makeText(getApplicationContext(), "该号码已注册", Toast.LENGTH_SHORT).show();
                	mDialog.dismiss();
                    break;
                case EVENT_EMPTY_TELE_NUMBER:
                	Toast.makeText(getApplicationContext(), "手机号不可为空", Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_EMPTY_VERIFICATION_CODE:
                	Toast.makeText(getApplicationContext(), "验证码不可为空", Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_VERIFY_SUCCESS:
                	if(mForget){
                		showSetPasswdDialog(true);
                	}else{
                        showSetPasswdDialog(false);
                	}
                    mUpdateTime=false;
                    mApplyVerificationCodeBT.setText("验证码");
                    mApplyVerificationCodeBT.setEnabled(true);
                	mForget = false;
                	break;
                case EVENT_EMPTY_PASSWD:
                	Toast.makeText(getApplicationContext(), "密码不可为空", Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_EMPTY_RE_PASSWD:
                	Toast.makeText(getApplicationContext(), "确认密码不可为空", Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_INCONSISTENT_PASSWD:
                	Toast.makeText(getApplicationContext(), "请确保输入一致", Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_UPDATE_TIME:
                	mApplyVerificationCodeBT.setText(msg.obj + "s");
                    break;
                case EVENT_LOGIN_SUCCESS:
    				String name = mTeleNumberET.getText().toString();
    		        String password = mPasswordET.getText().toString();
    		        writeData(name, password, mKeepUserinfo.isChecked(), mKeepPassword.isChecked());
    				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
    				Bundle bundle = new Bundle();
    				bundle.putString("telenumber", name);
    				intent.putExtras(bundle);
    				startActivity(intent);
    				finish();
                	break;
                default:
                    break;
            }
        }
    };
    
    public void showRegisterDialog(final boolean forget){
    	mForget = forget;
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_verification_code, null); // 加载自定义的布局文件
		final EditText teleNumberET = (EditText)view.findViewById(R.id.et_input_tele_number);
		final EditText verificationCodeET = (EditText)view.findViewById(R.id.et_input_verification_code);
		mApplyVerificationCodeBT = (Button)view.findViewById(R.id.bt_apply_verification_code);
		mApplyVerificationCodeBT.setText("验证码");
		final Button nextRegisterStepBT=(Button)view.findViewById(R.id.bt_next_register_step);
		nextRegisterStepBT.setEnabled(false);
		final CheckBox agreeCB = (CheckBox)view.findViewById(R.id.cb_agree);
		agreeCB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            	nextRegisterStepBT.setEnabled(agreeCB.isChecked());
            }
        });
		if(forget){
			agreeCB.setVisibility(View.GONE);
			nextRegisterStepBT.setEnabled(true);
		}else{
			agreeCB.setVisibility(View.VISIBLE);
		}
		final AlertDialog.Builder VCdialogBuilder = new AlertDialog.Builder(LoginActivity.this);
		VCdialogBuilder.setView(view); 
		//VCdialogBuilder.setCancelable(false);//点击对话框外面的区域无效
		nextRegisterStepBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
            	if(teleNumberET.getText().toString().equals("")){
            		Message msg = new Message();
            		msg.what = EVENT_EMPTY_TELE_NUMBER;
            		mHandler.sendMessage(msg);
            	}else if(verificationCodeET.getText().toString().equals("")){
            		Message msg = new Message();
            		msg.what = EVENT_EMPTY_VERIFICATION_CODE;
            		mHandler.sendMessage(msg);
            	}else{
            		if(forget){
    					mAnalysisTask = new UserAnalysisTask("forget",teleNumberET.getText().toString(),verificationCodeET.getText().toString());
    					mAnalysisTask.execute((Void) null);
            		}else{
    					mAnalysisTask = new UserAnalysisTask("register",teleNumberET.getText().toString(),verificationCodeET.getText().toString());
    					mAnalysisTask.execute((Void) null);
            		}
            	}
			}
		});
		class TimeThread extends Thread {
	        @Override
	        public void run () {
	        	synchronized(mLock) {
		                try {
		                	while(mThreadTime>=0 && mUpdateTime){
			                    Thread.sleep(1000);
			                    Message msg = new Message();
			                    msg.what = EVENT_UPDATE_TIME;
			                    msg.obj = mThreadTime--;
								mHandler.sendMessage(msg);
								Log.e("TimeThread","update time");
		                	}
		                }
		                catch (InterruptedException e) {
		                    e.printStackTrace();
		                }
	        	}
	        }
	    }
		mApplyVerificationCodeBT.setOnClickListener(new OnClickListener() {
			@Override
		    public void onClick(View v) {
				SMSSDK.getVerificationCode(mCountryCode, teleNumberET.getText().toString());//获取验证码
				mApplyVerificationCodeBT.setEnabled(false);
				mUpdateTime=true;
				mTimeThread=new TimeThread();
				mTimeThread.start();
		    }
	     });
		mDialog = VCdialogBuilder.create();
		mDialog.show();
    }
    
    public void showSetPasswdDialog(final boolean forget){
    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_passwd_set, null); // 加载自定义的布局文件
		final EditText setPasswdET = (EditText)view.findViewById(R.id.et_set_passwd);
		final EditText reInputPasswdET = (EditText)view.findViewById(R.id.et_re_input_passwd);
		final Button finishRegisterButton=(Button)view.findViewById(R.id.bt_finish_register);
		final AlertDialog.Builder VCdialogBuilder = new AlertDialog.Builder(LoginActivity.this);
		VCdialogBuilder.setView(view); // 自定义dialog
		VCdialogBuilder.setCancelable(false);//点击对话框外面的区域无效
		finishRegisterButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
            	if(setPasswdET.getText().toString().equals("")){
            		Message msg = new Message();
            		msg.what = EVENT_EMPTY_PASSWD;
            		mHandler.sendMessage(msg);
            	}else if(reInputPasswdET.getText().toString().equals("")){
            		Message msg = new Message();
            		msg.what = EVENT_EMPTY_RE_PASSWD;
            		mHandler.sendMessage(msg);
            	}else if(!(setPasswdET.getText().toString()).equals(reInputPasswdET.getText().toString())){
            		Message msg = new Message();
            		msg.what = EVENT_INCONSISTENT_PASSWD;
            		mHandler.sendMessage(msg);
            	}else{
            		if(forget){
    					mRegisterTask = new UserRegisterTask("forget",mTeleNumber,setPasswdET.getText().toString()); 
    					mRegisterTask.execute((Void) null);
            		}else{
    					mRegisterTask = new UserRegisterTask("register",mTeleNumber,setPasswdET.getText().toString());
    					mRegisterTask.execute((Void) null);
            		}
            	}
			}
		});
		mDialog = VCdialogBuilder.create();
		mDialog.show();
    }
    
	/**
	 * Attempts to login
	 */
	public void attemptLogin() {
		if (mLoginTask != null) {
			return;
		}

		// Reset errors.
		mTeleNumberET.setError(null);
		mPasswordET.setError(null);

		// Store values at the time of the login attempt.
		mTeleNumber = mTeleNumberET.getText().toString();
		mPassword = mPasswordET.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mTeleNumber)) {
			mTeleNumberET.setError(getString(R.string.error_tele_number_field_required));
			focusView = mTeleNumberET;
			cancel = true;
		}else if (mTeleNumberET.length() != 11) {
			mTeleNumberET.setError(getString(R.string.error_invalid_tele_number));
			focusView = mTeleNumberET;
			cancel = true;
		}else if (TextUtils.isEmpty(mPassword)) {
			mPasswordET.setError(getString(R.string.error_passwd_field_required));
			focusView = mPasswordET;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordET.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordET;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mLoginTask = new UserLoginTask();
			mLoginTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Add for request login's state
	 * */
	public boolean clientLogin(String account, String pwd)throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置,"0"代表永不超时
		  String strurl = "http://" + 	this.getString(R.string.ip) + "/itspark/owner/login/login";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  //request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  LoginInfo info = new LoginInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_LOGIN_CODE, account, readToken());
		  info.setHeader(header);
		  info.setVersion(getVersion());
		  info.setPassword(getMD5Code(pwd));
		  String androidID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		  info.setAndroidID(androidID);
		  mPublicKey = generateRSAKeyPair(1024).getPublic().toString(); 
		  mPrivateKey = generateRSAKeyPair(1024).getPrivate().toString();
		  //param.put("publicKey", mPublicKey);
		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientLogin-> param is " + JacksonJsonUtil.beanToJson(info));
		  request.setEntity(se);//发送数据
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientLogin->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  String resCode = res.getResCode();
				  if(resCode.equals("100")){
					  writeToken((String)res.getPropertyMap().get("token"));
					  mErrorType = ERROR_TYPE_NO_ERROR;
					  return true;
				  }else if(resCode.equals("201")){
					  mErrorType = ERROR_TYPE_TELE;
					  return false;
				  }else if(resCode.equals("202")){
					  mErrorType = ERROR_TYPE_PASSWD;
					  return false;
				  }else{
					  return false;
				  }
			  }else{
				  Log.e(LOG_TAG, "clientLogin->error code is " + Integer.toString(code));
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
	 * 用户注册task
	 * 
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				String name = mTeleNumberET.getText().toString();
				String passwd = mPasswordET.getText().toString();
				return clientLogin(name,passwd);
			}catch(Exception e){
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mLoginTask = null;
			showProgress(false);

			if (success) {
				Message msg = new Message();
				msg.what = EVENT_LOGIN_SUCCESS;
				mHandler.sendMessage(msg);
			} else {
               if(mErrorType == ERROR_TYPE_TELE){
					mTeleNumberET.setError(getString(R.string.error_incorrect_tele));
					mTeleNumberET.requestFocus();
			   }else if(mErrorType == ERROR_TYPE_PASSWD){
					mPasswordET.setError(getString(R.string.error_incorrect_password));
					mPasswordET.requestFocus();
				}
			}
			mErrorType=ERROR_TYPE_NO_ERROR;
		}

		@Override
		protected void onCancelled() {
			mLoginTask = null;
			showProgress(false);
		}
	}
	
	
	/**
	 * Add for request account's  state
	 * */
	public boolean clientAnalysis(String type,String account,String verificationCode) throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
		  String strurl = "http://" + 	this.getString(R.string.ip) + "/itspark/owner/login/analysis";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  //request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  AnalysisInfo info = new AnalysisInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_ANALYSIS_ACCOUNT_CODE, account, readToken());
		  info.setHeader(header);
		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientAnalysis-> param is " + JacksonJsonUtil.beanToJson(info));
		  request.setEntity(se);//发送数据
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientAnalysis->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  String resCode = res.getResCode();
				  String resMsg = res.getResMsg();
				  toastWrapper(resMsg);
				  if(resCode.equals("100")){
					  mTeleNumber = account;
					  SMSSDK.submitVerificationCode( mCountryCode, account, verificationCode);
					  /**Add for test without verification code **/
            		  Message msg = new Message();
            		  msg.what = EVENT_VERIFY_SUCCESS;
            		  mHandler.sendMessage(msg);
            		  mDialog.dismiss();
            		  toastWrapper("验证成功");
					  return true;
				  }else if(resCode.equals("201")){
					  return false;
				  }
			  }else{
				  Log.e(LOG_TAG, "clientAnalysis->error code is " + Integer.toString(code));
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
	 * 判断账号是否存在Task
	 * 
	 */
	public class UserAnalysisTask extends AsyncTask<Void, Void, Boolean> {
		private String type;
		private String teleNumber;
		private String verificationCode;
		public UserAnalysisTask(String type,String teleNumber,String verificationCode){
			this.type = type;
			this.teleNumber = teleNumber;
			this.verificationCode = verificationCode;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				return clientAnalysis(type,teleNumber,verificationCode);
			}catch(Exception e){
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAnalysisTask = null;
		}

		@Override
		protected void onCancelled() {
			mAnalysisTask = null;
		}
		
	}
	
	/**
	 * Add for request register's state
	 * */
	public boolean clientRegister(String type,String account, String passwd) throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
		  String strurl = "http://" + 	this.getString(R.string.ip) + "/itspark/owner/login/register";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  RegisterInfo info = new RegisterInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_REGISTER_CODE, account, readToken());
		  info.setHeader(header);
		  info.setPassword(getMD5Code(passwd));
		  info.setRegisterType(type);
		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientRegister-> param is " + JacksonJsonUtil.beanToJson(info));
		  request.setEntity(se);//发送数据
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientRegister->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  String resCode = res.getResCode();
				  String resMsg = res.getResMsg();
				  toastWrapper(resMsg);
				  if(resCode.equals("100")){
					  return true;
				  }else if(resCode.equals("201")){
					  return false;
				  }
			  }else{
				  Log.e(LOG_TAG, "clienRegister->error code is " + Integer.toString(code));
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
	 * 用户注册Task
	 * 
	 */
	public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
		private String type;
		private String teleNumber;
		private String password; 
		public UserRegisterTask(String type,String teleNumber,String password){
			this.type = type;
			this.teleNumber = teleNumber;
			this.password = password;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				return clientRegister(type,teleNumber,password);
			}catch(Exception e){
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mRegisterTask = null;
			mDialog.dismiss();
		}

		@Override
		protected void onCancelled() {
			mRegisterTask = null;
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
     * 随机生成RSA密钥对 
     *  
     * @param keyLength 
     *            密钥长度，范围：512～2048<br> 
     *            一般1024 
     * @return 
     */  
    public static KeyPair generateRSAKeyPair(int keyLength)  
    {  
        KeyPair keyPair = RSAUtils.generateRSAKeyPair(keyLength);
		return keyPair;  
    }  
    
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.app_name) + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.can_not_find_version_name);
        }
    }
    
    public String readToken() {
        SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
        String str = pref.getString("token", "");
        return str;
    }
}
