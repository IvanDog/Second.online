package com.example.driver.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapException;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.Photo;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.example.driver.R;
import com.example.driver.R.color;
import com.example.driver.R.drawable;
import com.example.driver.R.id;
import com.example.driver.R.layout;
import com.example.driver.R.string;
import com.example.driver.common.JacksonJsonUtil;
import com.example.driver.common.UserDbAdapter;
import com.example.driver.info.CommonRequestHeader;
import com.example.driver.info.CommonResponse;
import com.example.driver.info.LogoutInfo;
import com.example.driver.info.QueryRecordInfo;
import com.example.driver.info.QueryUserInfo;
import com.example.driver.info.SearchParkInfo;


import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements  LocationSource, AMapLocationListener, AMap.OnMapClickListener,
AMap.OnInfoWindowClickListener,AMap.InfoWindowAdapter,AMap.OnMarkerClickListener,PoiSearch.OnPoiSearchListener,Inputtips.InputtipsListener,OnGeocodeSearchListener,RouteSearch.OnRouteSearchListener{
	private Context mContext;
	private String mTeleNumber;
	private TextView mCityTV;
	//private Button mCloseRouteBT;
	private View mUserCenter;
	private TextView mUserCenterTV;
	private TextView mAccountBalanceTV;
	private TextView mParkingCouponTV;
	private View mRelativeParkingsListView;
	private ListView mParkingsListView=null; 
	private TextView mAllParkingTypeParkingListTV;
	private TextView mOutsideParkingTypeParkingListTV;
	private TextView mInsideParkingTypeParkingListTV;
	private TextView mEmptyParkingListNotifyTV;
	private TextView mNotifyInputLocationTV;
	private ListView mSearchList=null;
	private ListView mParkingDetailListView=null;
	private ParkingDetailAdapter mParkingDetailAdapter;
    private ArrayList<HashMap<String, Object>> mParkingDetailList=new ArrayList<HashMap<String,Object>>(); 
    private UserQueryTask mQueryTask = null;
    private UserCenterDisplayTask  mDisplayUserTask= null;
    private UserLogoutTask mLogoutTask = null;
	private TextView mEmptyParkingDetailNotifyTV;
	private ListView mUserCenterList=null;
	private AlertDialog mDialog;
	private AlertDialog mParkingDetailDialog;
    private View mContainer;
    private TextView mPoiNameTV;
    private ImageButton mPayIMBT;
    private ImageButton mFindIMBT;
    private ImageButton mMineIMBT;
	private String mAccountbalance;
	private String mParkingCoupon;
	private String mNickName;
	private Drawable mHeadPortrait = null;
	private String mCurrentCity = "天津";
	
	private TextView mAllParkingTypeTV;
	private TextView mOutsideParkingTypeTV;
	private TextView mInsideParkingTypeTV;
	private String mParkingType = "150903|150904|150905|150906";
    private int mCurrentId;
    private int mCurrentParkingTypeId;
	private MapView mapView;//地图控件
	private AMap mAMAP;//地图控制器对象

    private long mExitTime = 0;
	/**
 	 *      定位需要的声明
 	 */
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mLocationListener = null;//定位监听器
    private boolean isFirstLoc = true;//标识，用于判断是否只显示一次定位信息和用户重新定位
    private AutoCompleteTextView mKey = null;
    private ImageView mParkingIV;
    private ImageView mDeleteIV;
    private PoiSearch mPoiSearch;
    private MyLocationStyle mMyLocationStyle;
    private PoiResult poiResult;//the result of the poi
    private int currentPage = 0;//the page start with 0
    private PoiSearch.Query mQuery;//poi query
    private Marker locationMarker;//选择点
    private Marker mDetailMarker;
    private Marker mlastMarker;
    private myPoiOverlay poiOverlay;//poi图层
    private List<PoiItem> poiItems;//poi数据
    private PoiItem mPoi;
    private RelativeLayout mPoiDetail;
	private LatLonPoint lp = new LatLonPoint(39.1366672021, 117.2100419600);
    private String keyWord = "";
    private int mSearchTag = 0;
    private ArrayList<HashMap<String, Object>> mList=new ArrayList<HashMap<String,Object>>(); 
    private GeocodeSearch mGeocoderSearch;
    private RouteSearch mRouteSearch;
    protected LatLonPoint mStartLatlng ;
    protected LatLonPoint mEndLatlng ;
    private DriveRouteResult mDriveRouteResult;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private boolean mIsZoomByRoute;
	private UserQueryParkingTask mQueryParkingTask = null;
	
	private View mDialogMain;
	private TextView mDialogParkingNameTV;
	private TextView mDialogParkingFreeNumberTV;
	private TextView mDialogParkingFeeTV;
	private TextView mDialogParkingFreeDurationTV;
	private TextView mDialogCertificationTV;
	private TextView mDialogParkingChargeTV;
	private TextView mDialogParkingAutoChargeTV;
	private TextView mDialogParkingNetworkChargeTV;
	private TextView mDialogCashPaymentTV;
	private TextView mDialogCardPaymentTV;
	private TextView mDialogAliPaymentTV;
	private TextView mDialogWechatPaymentTV;
	
	private View mDialogParkingNumberDetails;
	//private TextView mDialogParkingLocationTV;
	//private ImageView mDialogDisplayDetaiIV;
	private TextView mDialogRouteBT;
	private boolean mRouteState;
	private TextView mNavigationBT;
	private Double mCurrentDialogLatitude;
	private Double mCurrentDialogLongtitude;
	private UserDbAdapter mUserDbAdapter; 
    private static final int EVENT_SHOW_DIALOG = 101;
    private static final int EVENT_DISPLAY_USER_INFORMATION = 102;
    private static final int EVENT_SET_UNFINISHED_PARKING_RECORD_ADAPTER = 201;
    private static final int EVENT_NO_UNFINISHED_PARKING_RECORD = 202;
    private static final int EVENT_SET_PARKING_LIST_ADAPTER = 203;
    private static final int EVENT_DISPLAY_MARKER_INFORMATION = 301;
    private static final int EVENT_DISMISS_MARKER = 302;
    private static final String FILE_NAME_TOKEN = "save_pref_token";
    private static final String LOG_TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mUserDbAdapter = new UserDbAdapter(this);
        setContentView(R.layout.activity_main);
        mContext=this;
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	mTeleNumber = bundle.getString("telenumber");
        }
        
     	/**
     	 *      主界面Dialog
     	 */
    	mDialogMain = (View)findViewById(R.id.dialog_main);
    	mDialogMain.setAlpha(0.8f);
    	mDialogParkingNameTV = (TextView)findViewById(R.id.tv_poi_name_dialog);
    	mDialogParkingFreeNumberTV = (TextView)findViewById(R.id.tv_parking_free_dialog);
    	mDialogParkingFeeTV = (TextView)findViewById(R.id.tv_parking_fee_dialog);
    	mDialogParkingFreeDurationTV = (TextView)findViewById(R.id.tv_parking_free_time_duration_dialog);
    	mDialogCertificationTV = (TextView)findViewById(R.id.tv_parking_type_certification_or_not_dialog);
    	mDialogParkingChargeTV = (TextView)findViewById(R.id.tv_parking_type_certification_or_not_dialog);
    	mDialogParkingAutoChargeTV= (TextView)findViewById(R.id.tv_parking_type_manual_or_auto_dialog);
    	mDialogParkingNetworkChargeTV= (TextView)findViewById(R.id.tv_parking_type_network_or_venue_dialog);
    	mDialogCashPaymentTV= (TextView)findViewById(R.id.tv_payment_type_cash_dialog);
    	mDialogCardPaymentTV= (TextView)findViewById(R.id.tv_payment_type_pos_dialog);
    	mDialogAliPaymentTV= (TextView)findViewById(R.id.tv_payment_type_ali_dialog);
    	mDialogWechatPaymentTV= (TextView)findViewById(R.id.tv_payment_type_wechat_dialog);
    	mDialogRouteBT = (TextView)findViewById(R.id.tv_detail_dialog);
    	mDialogRouteBT.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v){
    			if(!mIsZoomByRoute){
    				mEndLatlng = new LatLonPoint(Double.valueOf(mCurrentDialogLatitude), Double.valueOf(mCurrentDialogLongtitude));
    				showProgressDialog();
    		        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
    		                mStartLatlng, mEndLatlng);
    		        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null,
    		                null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
    		        mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
    			}else{
            		mIsZoomByRoute = false;
            		//doSearchQuery(mCurrentCity,true);
            		searchNearbyParking(true);
            		mDialogRouteBT.setText("路线");
    			}
    		}
    	});
    	mNavigationBT = (TextView)findViewById(R.id.tv_navigation_dialog);
    	mNavigationBT.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v){
				if (isAvilible(getApplicationContext(), "com.autonavi.minimap")) {
                    try{  
                         Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=driver&poiname=name&lat="+mCurrentDialogLatitude+"&lon="+mCurrentDialogLongtitude+"&dev=0");  
                         startActivity(intent);   
                    } catch (URISyntaxException e)  {
                    	e.printStackTrace(); 
                    } 
                }else{
                        Toast.makeText(getApplicationContext(), "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                        Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");  
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);   
                        startActivity(intent);
                 }
    		}
    	});
    	
    	
     	/**
     	 *      城市选择控件
     	 */
        mCityTV=(TextView)findViewById(R.id.tv_city);  
        mCityTV.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		showCityDialog();
        	}
        });
        
     	/**
     	 *      查找车位控件
     	 */
        mFindIMBT=(ImageButton)findViewById(R.id.imgbt_find);
        mFindIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_black_36dp)); 
        mFindIMBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		mFindIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_black_36dp)); 
        		mPayIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_balance_wallet_white_36dp)); 
        		mMineIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_box_white_36dp)); 
        		
        		mCityTV.setVisibility(View.VISIBLE);
        		mKey.setVisibility(View.VISIBLE);
          		mDeleteIV.setVisibility(View.VISIBLE);
        		mParkingIV.setVisibility(View.VISIBLE);
           	    mContainer.setVisibility(View.VISIBLE);
           	    mParkingDetailListView.setVisibility(View.GONE); 
           	    mEmptyParkingDetailNotifyTV.setVisibility(View.GONE); 
        	    mUserCenter.setVisibility(View.GONE); 
        	}
        });
        
     	/**
     	 *      付费控件
     	 */
        mPayIMBT=(ImageButton)findViewById(R.id.imgbt_pay);
        mPayIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_balance_wallet_white_36dp)); 
        mPayIMBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		mFindIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_white_36dp)); 
        		mPayIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_balance_wallet_black_36dp)); 
        		mMineIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_box_white_36dp)); 
        		
    			mQueryTask = new UserQueryTask(mTeleNumber);
    			mQueryTask.execute((Void) null);
        		
        		mCityTV.setVisibility(View.GONE);
        		mKey.setVisibility(View.GONE);
        		mDeleteIV.setVisibility(View.GONE);
        		mParkingIV.setVisibility(View.GONE);
           	    mContainer.setVisibility(View.GONE);
           	    mRelativeParkingsListView.setVisibility(View.GONE);
           	    mParkingDetailListView.setVisibility(View.VISIBLE); 
          	    mUserCenter.setVisibility(View.GONE); 
        	}
        });
        
     	/**
     	 *      用户中心控件
     	 */
        mMineIMBT=(ImageButton)findViewById(R.id.imgbt_mine);
        mMineIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_box_white_36dp)); 
        mMineIMBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		mFindIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_white_36dp)); 
        		mPayIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_balance_wallet_white_36dp)); 
        		mMineIMBT.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_box_black_36dp)); 
        		
        		//setParkingDetailList();
        		
        		mCityTV.setVisibility(View.GONE);
        		mKey.setVisibility(View.GONE);
        		mDeleteIV.setVisibility(View.GONE);
        		mParkingIV.setVisibility(View.GONE);
           	    mContainer.setVisibility(View.GONE);
           	    mRelativeParkingsListView.setVisibility(View.GONE);
           	    mParkingDetailListView.setVisibility(View.GONE); 
          	    mEmptyParkingDetailNotifyTV.setVisibility(View.GONE); 
           	    mUserCenter.setVisibility(View.VISIBLE); 
           	    
        	}
        });
        
     	/**
     	 *      关闭路线控件
     	 */
    /*mCloseRouteBT = (Button)findViewById(R.id.bt_close_route);
        mCloseRouteBT.setAlpha(0.8f);
        mCloseRouteBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		mIsZoomByRoute = false;
        		mCloseRouteBT.setVisibility(View.GONE);
        		doSearchQuery(mCurrentCity,true);
        	}
        });*/
        
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        
     	/**
     	 *      切换停车场类别控件
     	 */
    	mAllParkingTypeTV=(TextView)findViewById(R.id.tv_all_parking_type);
		mAllParkingTypeTV.setBackgroundResource(R.color.gray);
    	mAllParkingTypeTV.setOnClickListener(mTabClickListener);
        mOutsideParkingTypeTV=(TextView)findViewById(R.id.tv_outside_parking_type);
		mOutsideParkingTypeTV.setBackgroundResource(R.color.gray);
        mOutsideParkingTypeTV.setOnClickListener(mTabClickListener);
    	mInsideParkingTypeTV=(TextView)findViewById(R.id.tv_inside_parking_type);
		mInsideParkingTypeTV.setBackgroundResource(R.color.gray);
    	mInsideParkingTypeTV.setOnClickListener(mTabClickListener);
    	changeSelect(R.id.tv_all_parking_type,1);
    	
     	/**
     	 *      地图控件
     	 */
        mapView = (MapView)findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mAMAP = mapView.getMap();          //获取地图对象
        mAMAP.setOnMapClickListener(this);
        mAMAP.setOnMarkerClickListener(this);
        mAMAP.setOnInfoWindowClickListener(this);
        mAMAP.setInfoWindowAdapter(this);
        UiSettings settings = mAMAP.getUiSettings();            //设置显示定位按钮 并且可以点击
        mAMAP.setLocationSource((LocationSource) this);            //设置定位监听
        settings.setMyLocationButtonEnabled(true);            // 是否显示定位按钮
        settings.setZoomControlsEnabled(false); //显示zoom按钮
        settings.setZoomGesturesEnabled(true);
        mAMAP.setMyLocationEnabled(true);            // 是否可触发定位并显示定位层
        mMyLocationStyle = new MyLocationStyle();
        mMyLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_48px));
        mMyLocationStyle.radiusFillColor(android.R.color.transparent);
        mMyLocationStyle.strokeColor(android.R.color.transparent);
        mAMAP.setMyLocationStyle(mMyLocationStyle);
        
        initLoc();
        
     	/**
     	 *      设置地图移动时的回调
     	 */
        mAMAP.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            	// TODO Auto-generated method stub
            }
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                    if(!mIsZoomByRoute){
                    	if(lp.getLatitude() != cameraPosition.target.latitude || lp.getLongitude() != cameraPosition.target.longitude){
                        	lp = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
                        	showProgressDialog();
                            //doSearchQuery(mCurrentCity,false);
                        	searchNearbyParking(false);
                            Log.e(LOG_TAG,"onCameraChangeFinish->doSearchQuery");
                    	}
                    }
            }
        });
        
        mContainer=(View)findViewById(R.id.frame_container);
        mRelativeParkingsListView=(View)findViewById(R.id.relative_parking_list);
        mParkingsListView=(ListView)findViewById(R.id.list_parking_list);  
        
    	mAllParkingTypeParkingListTV=(TextView)findViewById(R.id.tv_all_parking_type_parking_list);
		mAllParkingTypeParkingListTV.setBackgroundResource(R.color.gray);
		mAllParkingTypeParkingListTV.setOnClickListener(mParkingTabClickListener);
        mOutsideParkingTypeParkingListTV=(TextView)findViewById(R.id.tv_outside_parking_type_parking_list);
        mOutsideParkingTypeParkingListTV.setBackgroundResource(R.color.gray);
        mOutsideParkingTypeParkingListTV.setOnClickListener(mParkingTabClickListener);
    	mInsideParkingTypeParkingListTV=(TextView)findViewById(R.id.tv_inside_parking_type_parking_list);
		mInsideParkingTypeParkingListTV.setBackgroundResource(R.color.gray);
    	mInsideParkingTypeParkingListTV.setOnClickListener(mParkingTabClickListener);
    	changeSelect(R.id.tv_all_parking_type_parking_list,2);
    	
        mEmptyParkingListNotifyTV=(TextView)findViewById(R.id.tv_empty_list_notify_parking_list_main);  
        mNotifyInputLocationTV=(TextView)findViewById(R.id.tv_notify_input_location_parking_list_main);  
        mParkingsListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
            	//TODO
            }
        });
        
        
     	/**
     	 *      经纬度解析对象，构造 GeocodeSearch 对象，并设置监听
     	 */
        mGeocoderSearch = new GeocodeSearch(this);
        mGeocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
           	     Log.e(LOG_TAG,"onRegeocodeSearched");
            }
 
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int returnCode) {
                //判断请求是否成功(1000为成功，其他为失败)
           	 Log.e(LOG_TAG,"onGeocodeSearched");
                if (returnCode == 1000) {
                    if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                            && geocodeResult.getGeocodeAddressList().size() > 0) {
                        GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                        Log.e("yifan", "经纬度值:" + address.getLatLonPoint() + "位置描述:"
                                + address.getFormatAddress());
                        lp = new LatLonPoint(address.getLatLonPoint().getLatitude(), address.getLatLonPoint().getLongitude());
                        //doSearchQuery(address.getCity(),true);
                        searchNearbyParking(true);
                        Log.e(LOG_TAG,"onGeocodeSearched->doSearchQuery");
                    }
                }
            }
        });
        
        
        mSearchList= (ListView) findViewById(R.id.list_search);
        mSearchList.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
            	Map<String,Object> map=(Map<String,Object>)mSearchList.getItemAtPosition(arg2);
                String name=(String)map.get("name");
            	mKey.setText(name);
                //通过 GeocodeQuery(java.lang.String locationName, java.lang.String city) 设置查询参数，调用 GeocodeSearch 的 getFromLocationNameAsyn(GeocodeQuery geocodeQuery) 方法发起请求
                GeocodeQuery query = new GeocodeQuery(mKey.getText().toString().trim(), mCurrentCity);
                //发起请求
                mGeocoderSearch.getFromLocationNameAsyn(query);
                Log.e(LOG_TAG,"getFromLocationNameAsyn");
            	mSearchList.setVisibility(View.GONE);
            	mSearchTag=1;
            }
        });
        mParkingDetailListView = (ListView) findViewById(R.id.list_parking_detail);
        mEmptyParkingDetailNotifyTV=(TextView)findViewById(R.id.tv_empty_list_notify_parking_detail_main);
        mParkingDetailListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
            	HashMap<String,Object> map=(HashMap<String,Object>)mParkingDetailListView.getItemAtPosition(arg2);
            	Intent intent = new Intent(MainActivity.this,PaymentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("telenumber", mTeleNumber);
				bundle.putString("parkingRecordID", (String)map.get("parkingRecordID"));
				bundle.putString("licensePlateNumber", (String)map.get("licensePlateNumber"));
				bundle.putString("parkNumber", (String)map.get("parkNumber"));
				bundle.putString("parkName", (String)map.get("parkName"));
				bundle.putString("carType", (String)map.get("carType"));
				bundle.putString("startTime", (String)map.get("startTime"));
				bundle.putString("leaveTime", (String)map.get("leaveTime"));
				bundle.putString("parkingLocation", (String)map.get("parkingLocation"));
				//bundle.putString("feeScale", (String)map.get("feeScale"));
				intent.putExtras(bundle);
				startActivity(intent);
            }
        });
        mParkingIV=(ImageView)findViewById(R.id.iv_parking_list);
        mParkingIV.setOnClickListener(new OnClickListener(){
             @Override
             public void onClick(View v){
            	   if(mContainer.getVisibility()==View.VISIBLE){
                	     mContainer.setVisibility(View.GONE);
                	     if(mParkingsListView.getAdapter()!=null){
                    	       mRelativeParkingsListView.setVisibility(View.VISIBLE);
                               if(mCurrentId==R.id.tv_all_parking_type){
                            	   changeSelect(R.id.tv_all_parking_type_parking_list,2);
                                   mCurrentParkingTypeId = R.id.tv_all_parking_type_parking_list;
                               }else if(mCurrentId==R.id.tv_outside_parking_type){
                            	   changeSelect(R.id.tv_outside_parking_type_parking_list,2);
                            	   mCurrentParkingTypeId = R.id.tv_outside_parking_type_parking_list;
                               }else if(mCurrentId==R.id.tv_inside_parking_type){
                            	   changeSelect(R.id.tv_inside_parking_type_parking_list,2);
                            	   mCurrentParkingTypeId = R.id.tv_inside_parking_type_parking_list;
                               }
                    	       mParkingsListView.setVisibility(View.VISIBLE);
                        	   if(mParkingsListView.getAdapter().getCount()!=0){
                            	   mParkingsListView.setVisibility(View.VISIBLE);
                        		   mEmptyParkingListNotifyTV.setVisibility(View.GONE);
                        		   mNotifyInputLocationTV.setVisibility(View.GONE);
                        	   }else{
                        		   mParkingsListView.setVisibility(View.GONE);
                        		   mNotifyInputLocationTV.setVisibility(View.GONE);
                        		   mEmptyParkingListNotifyTV.setVisibility(View.VISIBLE);
                        	   }
                	      }else{
                	    	  mRelativeParkingsListView.setVisibility(View.GONE);
                		     mEmptyParkingListNotifyTV.setVisibility(View.GONE);
                		     mNotifyInputLocationTV.setVisibility(View.VISIBLE);
                	      }
            	 }else if(mContainer.getVisibility()==View.GONE){
                	 mContainer.setVisibility(View.VISIBLE);
                     if(mCurrentParkingTypeId == R.id.tv_all_parking_type_parking_list){
                  	     changeSelect(R.id.tv_all_parking_type,1);
                         mCurrentId=R.id.tv_all_parking_type;
                     }else if(mCurrentParkingTypeId == R.id.tv_outside_parking_type_parking_list){
                  	     changeSelect(R.id.tv_outside_parking_type,1);
                  	     mCurrentId=R.id.tv_outside_parking_type;
                     }else if(mCurrentParkingTypeId == R.id.tv_inside_parking_type_parking_list){
                  	     changeSelect(R.id.tv_inside_parking_type,1);
                  	     mCurrentId=R.id.tv_inside_parking_type;
                     }
                	 mRelativeParkingsListView.setVisibility(View.GONE);
                	 mNotifyInputLocationTV.setVisibility(View.GONE);
            		 mEmptyParkingListNotifyTV.setVisibility(View.GONE);
            	 }
             }
        });

     	/**
     	 *      搜索栏控件与搜索监听
     	 */
        mKey=(AutoCompleteTextView) findViewById(R.id.ac_search_input);
        OnKeyListener onKeyListener = new OnKeyListener() {
            @Override  
            public boolean onKey(View v, int keyCode, KeyEvent event) {  
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){  
                    /*隐藏软键盘*/  
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
                    if(inputMethodManager.isActive()){  
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);  
                    }   
                    showProgressDialog();
                    //通过 GeocodeQuery(java.lang.String locationName, java.lang.String city) 设置查询参数，调用 GeocodeSearch 的 getFromLocationNameAsyn(GeocodeQuery geocodeQuery) 方法发起请求
                    GeocodeQuery query = new GeocodeQuery(mKey.getText().toString().trim(), "天津");
                    //发起请求
                    mGeocoderSearch.getFromLocationNameAsyn(query);
                    return true;  
                }  
                return false;  
            }  
        };  
        mKey.setOnKeyListener(onKeyListener);  
        mDeleteIV=(ImageView)findViewById(R.id.iv_search_delete);
        mDeleteIV.setOnClickListener(new OnClickListener(){
        	@Override
        	public  void onClick(View v){
        		mKey.setText("");
        	}
        });
        
     	/**
     	 *      搜索栏控件联想监听
     	 */
        class InputtipsListener implements Inputtips.InputtipsListener{
			@Override
			public void onGetInputtips(List<Tip> list, int resultCode) {
				Log.e(LOG_TAG,"onGetInputtips->resultCode is " + resultCode);
			      if (resultCode == 1000 && mSearchTag==0) {// 正确返回
			    	  mSearchList.setVisibility(View.VISIBLE);
			    	  mDialogMain.setVisibility(View.GONE);
			            List<Map<String,Object>> searchList=new ArrayList<Map<String, Object>>() ;
			            for (int i=0;i<list.size();i++){
			                Map<String, Object> hashMap=new HashMap<String, Object>();
			                hashMap.put("name",list.get(i).getName());
			                Log.e(LOG_TAG,"name is " + list.get(i).getName());
			                hashMap.put("address",list.get(i).getDistrict());//将地址信息取出放入HashMap中
			                Log.e(LOG_TAG,"address is " + list.get(i).getDistrict());
			                searchList.add(hashMap);//将HashMap放入表中
			            }
			            Log.e(LOG_TAG,"newAdapter");
			            ParkingSearchAdapter searchAdapter=new ParkingSearchAdapter(getApplicationContext(),searchList);//新建一个适配器
			            Log.e(LOG_TAG,"setAdapter");
			            mSearchList.setAdapter(searchAdapter);//为listview适配
			            searchAdapter.notifyDataSetChanged();//动态更新listview
			    }
		    }
        };
         mKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString().trim();
                if(newText.equals("")){
                	mSearchTag=0;
                	mSearchList.setVisibility(View.GONE);
                	mDialogMain.setVisibility(View.VISIBLE);
                }else{
                	if(mSearchTag==0){
                        InputtipsQuery inputquery = new InputtipsQuery(newText, mCurrentCity);
                        inputquery.setCityLimit(true);//将获取到的结果进行城市限制筛选
                        Inputtips inputTips = new Inputtips(MainActivity.this, inputquery);
                        inputTips.setInputtipsListener(new InputtipsListener());
                        inputTips.requestInputtipsAsyn();
                	}
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
                  
         
     	/**
     	 *      用户中心
     	 */
         mUserCenter=(View)findViewById(R.id.view_user_center);
         mUserCenterTV=(TextView)mUserCenter.findViewById(R.id.tv_user_center);
         mUserCenterTV.setOnClickListener(new OnClickListener(){
             @Override
             public void onClick(View v){
         		Intent userIntent = new Intent(MainActivity.this,UserInformationActivity.class);
         		Bundle userBindle = new Bundle();
         		userBindle.putString("telenumber", mTeleNumber);
         		userIntent.putExtras(userBindle);
         		startActivity(userIntent);
             }
         });
         mAccountBalanceTV=(TextView)mUserCenter.findViewById(R.id.tv_account_balance_user_center);
         mAccountBalanceTV.setOnClickListener(new OnClickListener(){
             @Override
             public void onClick(View v){
         		Intent mobileIntent = new Intent(MainActivity.this,RechargeActivity.class);
         		Bundle mobileBundle = new Bundle();
         		mobileBundle.putString("telenumber", mTeleNumber);
         		mobileIntent.putExtras(mobileBundle);
         		startActivity(mobileIntent);
             }
         });
         mParkingCouponTV=(TextView)mUserCenter.findViewById(R.id.tv_coupon_user_center);
         mParkingCouponTV.setOnClickListener(new OnClickListener(){
             @Override
             public void onClick(View v){
         		Intent mobileIntent = new Intent(MainActivity.this,ParkingCouponActivity.class);
         		Bundle mobileBundle = new Bundle();
         		mobileBundle.putString("telenumber", mTeleNumber);
         		mobileIntent.putExtras(mobileBundle);
         		startActivity(mobileIntent);
             }
         });
         mUserCenterList=(ListView)mUserCenter.findViewById(R.id.list_function_user_center);
         List<Map<String, Object>> list=getUserCenterData();  
         mUserCenterList.setAdapter(new UserCenterListAdapter(this, list));
         mUserCenterList.setOnItemClickListener(new OnItemClickListener(){
             @Override
             public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                     long arg3) {
             	Map<String,Object> map=(Map<String,Object>)mUserCenterList.getItemAtPosition(arg2);
                 String userCenterFunction=(String)map.get("userCenterFunction");
                 if(userCenterFunction.equals("车辆管理")){
                    Intent intent = new Intent(MainActivity.this,LicensePlateManagementActivity.class);
    				Bundle bundle = new Bundle();
    				bundle.putString("telenumber", mTeleNumber);
    				intent.putExtras(bundle);
       	        	startActivity(intent); 
                  }else if(userCenterFunction.equals("停车记录")){
                   	Intent intent = new Intent(MainActivity.this,ParkingRecordActivity.class);
    				Bundle bundle = new Bundle();
    				bundle.putString("telenumber", mTeleNumber);
    				intent.putExtras(bundle);
   	        	    startActivity(intent);  
                  }else if(userCenterFunction.equals("我的车位")){
                	 Toast.makeText(getApplicationContext(), "我的车位功能开发中", Toast.LENGTH_SHORT).show();
                   }else if(userCenterFunction.equals("意见反馈")){
                  	Intent intent = new Intent(MainActivity.this,FeedbackActivity.class);
    				Bundle bundle = new Bundle();
    				bundle.putString("telenumber", mTeleNumber);
    				intent.putExtras(bundle);
  	        	    startActivity(intent);
                  }else if(userCenterFunction.equals("消息中心")){
                 	Intent intent = new Intent(MainActivity.this,MessageCenterActivity.class);
    				Bundle bundle = new Bundle();
    				bundle.putString("telenumber", mTeleNumber);
    				intent.putExtras(bundle);
 	        	    startActivity(intent);
                 }else if(userCenterFunction.equals("退出账号")){
                 	showExitDialog();
                 }
             }
         });
         IntentFilter filter = new IntentFilter();  
         filter.addAction("ExitApp");  
         filter.addAction("BackMain");  
         registerReceiver(mReceiver, filter);
    }
    
	/**
	 *      切换停车场类型监听
	 */
	private OnClickListener mTabClickListener = new OnClickListener() {
        @Override  
        public void onClick(View v) {  
			if (v.getId() != mCurrentId) {//如果当前选中跟上次选中的一样,不需要处理  
                changeSelect(v.getId(),1);//改变图标跟文字颜色的选中   
                mCurrentId = v.getId();//设置选中id  
                if(mCurrentId==R.id.tv_all_parking_type){
                	mParkingType = "150903|150904|150905|150906";
                	//doSearchQuery(mCurrentCity,false);
                	searchNearbyParking(false);
                }else if(mCurrentId==R.id.tv_outside_parking_type){
                	mParkingType = "150906";
                	//doSearchQuery(mCurrentCity,false);
                	searchNearbyParking(false);
                }else if(mCurrentId==R.id.tv_inside_parking_type){
                	mParkingType = "150903|150904|150905";
                	//doSearchQuery(mCurrentCity,false);
                	searchNearbyParking(false);
                }
            }  
        }  
    };  
    
	/**
	 *      切换停车场类型监听
	 */
	private OnClickListener mParkingTabClickListener = new OnClickListener() {
        @Override  
        public void onClick(View v) {  
			if (v.getId() != mCurrentParkingTypeId) {//如果当前选中跟上次选中的一样,不需要处理  
                changeSelect(v.getId(),2);//改变图标跟文字颜色的选中   
                mCurrentParkingTypeId = v.getId();//设置选中id  
                if(mCurrentParkingTypeId==R.id.tv_all_parking_type_parking_list){
                	mParkingType = "150903|150904|150905|150906";
                	//doSearchQuery(mCurrentCity,false);
                	searchNearbyParking(false);
                }else if(mCurrentParkingTypeId==R.id.tv_outside_parking_type_parking_list){
                	mParkingType = "150906";
                	//doSearchQuery(mCurrentCity,false);
                	searchNearbyParking(false);
                }else if(mCurrentParkingTypeId==R.id.tv_inside_parking_type_parking_list){
                	mParkingType = "150903|150904|150905";
                	//doSearchQuery(mCurrentCity,false);
                	searchNearbyParking(false);
                }
            }  
        }  
    };  
    
	/**
	 *      切换停车场控件颜色切换
	 */
	private void changeSelect(int resId,int dislplayType) {
		if(dislplayType==1){
			mAllParkingTypeTV.setSelected(false);
			mAllParkingTypeTV.setBackgroundResource(R.color.gray);
			mOutsideParkingTypeTV.setSelected(false);
			mOutsideParkingTypeTV.setBackgroundResource(R.color.gray);
			mInsideParkingTypeTV.setSelected(false);
			mInsideParkingTypeTV.setBackgroundResource(R.color.gray);
	        switch (resId) {  
	            case R.id.tv_all_parking_type:  
	        	    mAllParkingTypeTV.setSelected(true);  
	        	    mAllParkingTypeTV.setBackgroundResource(R.color.orange);
	        	    mIsZoomByRoute = false;
	        	    mDialogRouteBT.setText("路线");
	        	    //mCloseRouteBT.setVisibility(View.GONE);
	                break;  
	            case R.id.tv_outside_parking_type:  
	        	    mOutsideParkingTypeTV.setSelected(true);  
	        	    mOutsideParkingTypeTV.setBackgroundResource(R.color.orange);
	        	    mIsZoomByRoute = false;
	        	    mDialogRouteBT.setText("路线");
	        	    //mCloseRouteBT.setVisibility(View.GONE);
	                break;
	            case R.id.tv_inside_parking_type:  
	        	    mInsideParkingTypeTV.setSelected(true);  
	        	    mInsideParkingTypeTV.setBackgroundResource(R.color.orange);
	        	    mIsZoomByRoute = false;
	        	    mDialogRouteBT.setText("路线");
	        	    //mCloseRouteBT.setVisibility(View.GONE);
	                break;  
	        }  
		}else if(dislplayType==2){
			mAllParkingTypeParkingListTV.setSelected(false);
			mAllParkingTypeParkingListTV.setBackgroundResource(R.color.gray);
			mOutsideParkingTypeParkingListTV.setSelected(false);
			mOutsideParkingTypeParkingListTV.setBackgroundResource(R.color.gray);
			mInsideParkingTypeParkingListTV.setSelected(false);
			mInsideParkingTypeParkingListTV.setBackgroundResource(R.color.gray);
	        switch (resId) {  
	            case R.id.tv_all_parking_type_parking_list:  
	            	mAllParkingTypeParkingListTV.setSelected(true);  
	            	mAllParkingTypeParkingListTV.setBackgroundResource(R.color.orange);
	            	if(mIsZoomByRoute){
	            		//doSearchQuery(mCurrentCity,true);
	            		searchNearbyParking(true);
		        	    mIsZoomByRoute = false;
		        	    mDialogRouteBT.setText("路线");
	            	}
	        	    //mCloseRouteBT.setVisibility(View.GONE);
	                break;  
	            case R.id.tv_outside_parking_type_parking_list:  
	            	mOutsideParkingTypeParkingListTV.setSelected(true);  
	            	mOutsideParkingTypeParkingListTV.setBackgroundResource(R.color.orange);
	            	if(mIsZoomByRoute){
	            		//doSearchQuery(mCurrentCity,true);
	            		searchNearbyParking(false);
		        	    mIsZoomByRoute = false;
		        	    mDialogRouteBT.setText("路线");
	            	}
	        	    //mCloseRouteBT.setVisibility(View.GONE);
	                break;
	            case R.id.tv_inside_parking_type_parking_list:  
	            	mInsideParkingTypeParkingListTV.setSelected(true);  
	            	mInsideParkingTypeParkingListTV.setBackgroundResource(R.color.orange);
	            	if(mIsZoomByRoute){
	            		//doSearchQuery(mCurrentCity,true);
	            		searchNearbyParking(false);
		        	    mIsZoomByRoute = false;
		        	    mDialogRouteBT.setText("路线");
	            	}
	        	    //mCloseRouteBT.setVisibility(View.GONE);
	                break;  
	        }  
		}
    }
	
	
	/**
	 *      搜索停车场(易华录测试数据)
	 */
	protected void searchNearbyParking(boolean isSearchType){
        if(isSearchType){
        	//将地图移动到定位点
            mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
        }
		mQueryParkingTask = new UserQueryParkingTask(lp.getLatitude(),lp.getLongitude(),5000);
		mQueryParkingTask.execute((Void) null);
	}
	/**
	 *      搜索停车场(高德数据)
	 */
    protected void doSearchQuery(String city, final boolean isSearchType){
    	keyWord = mKey.getText().toString().trim();
    	mQuery = new PoiSearch.Query("", mParkingType , city);//150900
        mPoiSearch = new PoiSearch(MainActivity.this, mQuery);
        mPoiSearch.setBound(new PoiSearch.SearchBound(lp,500,true));
        mPoiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int errcode) {
                //判断搜索成功
                if (errcode == 1000) {
                    if (null != poiResult/* && poiResult.getPois().size() > 0*/) {
                    	mList.clear();
                        for (int i = 0; i < poiResult.getPois().size(); i++) {
                        //Log.e("TAG_MAIN", "POI 的行政区划代码和名称=" + poiResult.getPois().get(i).getAdCode()+","+poiResult.getPois().get(i).getAdName());
                        //Log.e("TAG_MAIN", "POI的所在商圈=" + poiResult.getPois().get(i).getBusinessArea());
                        //Log.e("TAG_MAIN", "POI的城市编码与名称=" + poiResult.getPois().get(i).getCityCode()+","+poiResult.getPois().get(i).getCityName());
                        //Log.e("TAG_MAIN", "POI 的经纬度=" + poiResult.getPois().get(i).getLatLonPoint());
                       /*if(i==0){
                        		lp = new LatLonPoint(poiResult.getPois().get(i).getLatLonPoint().getLatitude(), poiResult.getPois().get(i).getLatLonPoint().getLongitude());
                        	}*/
                        	//URL url = URLEncoder.encode(poiResult.getPois().get(i).getPhotos().get(0).getUrl(),"utf8");
                       /*if(!(poiResult.getPois().get(i).getPhotos().isEmpty())){
                            	Log.e("gouyifan", "POI图片=" + poiResult.getPois().get(i).getPhotos().get(0).getUrl());
                        	}*/
                        	if(i==0){
                        		setPoiItemDisplayContent(poiResult.getPois().get(i));
                        	}
                            Log.e(LOG_TAG, "POI的名称=" + poiResult.getPois().get(i).getTitle());
                            Log.e(LOG_TAG, "POI的距离=" + poiResult.getPois().get(i).getDistance());
                            Log.e(LOG_TAG, "POI的地址=" + poiResult.getPois().get(i).getSnippet());
                            Log.e(LOG_TAG, "POI的纬度=" + poiResult.getPois().get(i).getLatLonPoint().getLatitude());
                            Log.e(LOG_TAG, "POI的经度=" + poiResult.getPois().get(i).getLatLonPoint().getLongitude());
                            HashMap<String, Object> map=new HashMap<String, Object>();  
                            map.put("parkingName", poiResult.getPois().get(i).getTitle());
                            map.put("distance", poiResult.getPois().get(i).getDistance());
                            map.put("location", poiResult.getPois().get(i).getAdName() + poiResult.getPois().get(i).getBusinessArea() + poiResult.getPois().get(i).getSnippet());  
                            map.put("parkingNumberTotal", "总车位:50"); 
                            map.put("parkingNumberIdle", "空闲:20"); 
                            map.put("parkingFee", "计费:5元/时"); 
                            map.put("parkingFreeTime", "免费时长:1h"); 
                            //map.put("parkingPhotos", poiResult.getPois().get(i).getPhotos());
                            map.put("fee","计费:5元/时");
                            map.put("latitude", poiResult.getPois().get(i).getLatLonPoint().getLatitude());
                            map.put("longtitude", poiResult.getPois().get(i).getLatLonPoint().getLongitude());
                            mList.add(map);  
                        }
                        mParkingsListView.setAdapter(new ParkingListAdapter(mContext, mList));
                    }
                    if(isSearchType){
                    	//将地图移动到定位点
                        mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
                    }
                    //是否是同一条
                  if(poiResult.getQuery().equals(mQuery)){
                      poiItems = poiResult.getPois();
                      //获取poitem数据
                      List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();
                      if(poiItems !=null && poiItems.size()>0) {
                          //清除POI信息
                          //whetherToShowDetailInfo(false);
                          //并还原点击marker样式
                          if (mlastMarker != null) {
                              resetlastmarker();
                          }
                          //清除之前的结果marker样式
                          if (poiOverlay != null) {
                              poiOverlay.removeFromMap();
                          }
                          //新的marker
                          mAMAP.clear();
                          Log.e("yifan","doSearchQuery->clear");
                          poiOverlay = new myPoiOverlay(mAMAP, poiItems);
                          poiOverlay.addToMap();
                          //poiOverlay.zoomToSpan();

                          mAMAP.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                                  .icon(BitmapDescriptorFactory
                                          .fromBitmap(BitmapFactory.decodeResource(
                                                  getResources(), R.drawable.ic_add_location_48px)))
                                  .position(new LatLng(lp.getLatitude(), lp.getLongitude())));
                          dismissProgressDialog();
                          //在地图上显示搜索范围圈
                          /* aMap.addCircle(new CircleOptions().center(new LatLng(lp.getLatitude(), lp.getLongitude())).radius(5000)
                                  .strokeColor(Color.BLUE)
                                  .fillColor(Color.argb(50, 1, 1, 1))
                                  .strokeWidth(2));*/
                      }else if (suggestionCities !=null && suggestionCities.size()>0){
                    	  Toast.makeText(getApplicationContext(), "showSuggestCity", Toast.LENGTH_LONG).show();
                          showSuggestCity(suggestionCities);
                      } else {
                          //新的marker
                          mAMAP.clear();
                          poiOverlay = new myPoiOverlay(mAMAP, poiItems);
                          poiOverlay.addToMap();
                          //poiOverlay.zoomToSpan();

                          mAMAP.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                                  .icon(BitmapDescriptorFactory
                                          .fromBitmap(BitmapFactory.decodeResource(
                                                  getResources(), R.drawable.ic_add_location_48px)))
                                  .position(new LatLng(lp.getLatitude(), lp.getLongitude())));
                          dismissProgressDialog();
                          setPoiItemDisplayContent(null);
                    	  Toast.makeText(getApplicationContext(), "未发现附近停车场", Toast.LENGTH_SHORT).show();
                      }
                  }else{
                	   Log.e(LOG_TAG,"query not consistent.");
                  }
              
                }
            }
            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {
            	//TODO
            }
        });
        mPoiSearch.searchPOIAsyn();
    }
    
	/**
	 *      初始化定位
	 */
    private void initLoc() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener((AMapLocationListener) this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }
    
	/**
	 *      定位回调函数
	 */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                mStartLatlng = new LatLonPoint(Double.valueOf(amapLocation.getLatitude()), Double.valueOf(amapLocation.getLongitude()));
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getCity();
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                	mCurrentCity=amapLocation.getCity();
                	mCityTV.setText(mCurrentCity.replace("市", ""));
                    //设置缩放级别
                    mAMAP.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    lp = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());
                    //doSearchQuery(amapLocation.getCity(),true);
                    searchNearbyParking(true);
                    Log.e(LOG_TAG,"onLocationChanged->doSearchQuery");
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mLocationListener.onLocationChanged(amapLocation);
                    //添加图钉
                    //aMap.addMarker(getMarkerOptions(amapLocation));
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getCity() + ""  + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_SHORT).show();
                    isFirstLoc = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

	/**
	 *      自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
	 */
    private MarkerOptions getMarkerOptions(AMapLocation amapLocation) {
         //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
       options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_48px));
        //位置
        options.position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
        StringBuffer buffer = new StringBuffer();
        buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() +  "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
        //标题
        options.title(buffer.toString());
        //子标题
        //options.snippet("就是这里");
        //设置多少帧刷新一次图片资源
        options.period(60);
        return options;
    }
    
	/**
	 *      激活定位
	 */
    @Override
    public void activate(OnLocationChangedListener listener) {
    	mLocationListener = listener;
    }

	/**
	 *      停止定位
	 */
    @Override
    public void deactivate() {
    	mLocationListener = null;
    }
    
    /**
     * 重新绘制加载地图
     */
    @Override
    protected void onResume() {
       super.onResume();
       mapView.onResume();
       //new UpdateInformationThread().start();
       mDisplayUserTask = new UserCenterDisplayTask();
       mDisplayUserTask.execute((Void) null);
    }
    
    /**
     * 暂停地图的绘制
     */
    @Override
    protected void onPause() {
       super.onPause();
       mapView.onPause();
    }
    
    /**
     * 保存地图当前的状态方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
       mapView.onSaveInstanceState(outState);
    }
    
    /**
     * 销毁地图、取消注册
     */
    @Override
    protected void onDestroy() {
       super.onDestroy();
       mapView.onDestroy();
       unregisterReceiver(mReceiver);
    }
    
    @Override
    public void onPoiItemSearched(PoiItem arg0,int arg1){
    	// TODO Auto-generated method stub
    }
    
    
    private int[] markers = {
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
            R.drawable.ic_local_parking_32px,
    };
    
    private void whetherToShowDetailInfo(boolean isToShow){
        if(isToShow){
            mPoiDetail.setVisibility(View.VISIBLE);
        }else {
            mPoiDetail.setVisibility(View.GONE);
        }
    }
    
    
    private void showSuggestCity(List<SuggestionCity> cities){
        String infomation = "推荐城市\n";
        for(int i = 0;i<cities.size();i++){
            infomation += "城市名称：" + cities.get(i).getCityName() + "城市区号：" + cities.get(i).getCityCode() + "城市编码：" + cities.get(i).getAdCode() + "\n";
        }
        Toast.makeText(getApplicationContext(), infomation, Toast.LENGTH_LONG).show();
    }
    
    
	/**
	 *      将之前点击的marker还原为原来的状态
	 */
  private void resetlastmarker() {
      Log.e("yifan", "resetlastmarker");
      int index = poiOverlay.getPoiIndex(mlastMarker);
      //20个以内的marker显示图标
      mlastMarker.setIcon(BitmapDescriptorFactory
                  .fromBitmap(BitmapFactory.decodeResource(
                          getResources(),
                          markers[index])));
      mlastMarker = null;
  }
  
  
  @Override
  public void onMapClick(LatLng arg0){
      whetherToShowDetailInfo(false);
      if(mlastMarker!=null){
          resetlastmarker();
      }
  }
  
  
  public boolean onMarkerClick(Marker marker) {
      if (marker.getObject() != null) {
      //显示相关的位置信息
          //whetherToShowDetailInfo(true);
          try {
              PoiItem mCurrentPoi = (PoiItem) marker.getObject();
              if (mlastMarker == null) {
                  mlastMarker = marker;
              } else {
                 //还原原来的marker
                  resetlastmarker();
                  mlastMarker = marker;
              }
              mDetailMarker = marker;
              //按下后的显示图标
              mDetailMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_local_parking_32px)));
              setPoiItemDisplayContent(mCurrentPoi);
          } catch (Exception e) {
        	  e.printStackTrace();
          }
      } else {
          whetherToShowDetailInfo(false);
          resetlastmarker();
      }
      return true;
  }
  
  
  private void setPoiItemDisplayContent(PoiItem mCurrentPoi){
	  if(mCurrentPoi!=null){
		  mDialogMain.setVisibility(View.VISIBLE);
		  mDialogParkingNameTV.setText(mCurrentPoi.getTitle());
    /* if(mParkingType == "150903|150904|150905|150906"){
			  Drawable drawable = getResources().getDrawable(R.drawable.ic_parking_name_24px);
			  drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			  mDialogParkingNameTV.setCompoundDrawables(drawable, null, null, null);//画在左边
		  }else if(mParkingType == "150906"){
			  Drawable drawable = getResources().getDrawable(R.drawable.ic_inside_parking_32px);
			  drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			  mDialogParkingNameTV.setCompoundDrawables(drawable, null, null, null);//画在左边
		  }else{
			  Drawable drawable = getResources().getDrawable(R.drawable.ic_outside_parking_32px);
			  drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			  mDialogParkingNameTV.setCompoundDrawables(drawable, null, null, null);//画在左边
		  }*/
		  //mDialogParkingLocationTV.setText(mCurrentPoi.getAdName() +mCurrentPoi.getBusinessArea() + mCurrentPoi.getSnippet());
		  mCurrentDialogLatitude = mCurrentPoi.getLatLonPoint().getLatitude();
		  mCurrentDialogLongtitude = mCurrentPoi.getLatLonPoint().getLongitude();
	  }else{
		  mDialogMain.setVisibility(View.GONE);
	  }
	  //showParkingDetailDialog(mCurrentPoi.getTitle(), mCurrentPoi.getAdName() +mCurrentPoi.getBusinessArea() + mCurrentPoi.getSnippet(), mCurrentPoi.getLatLonPoint().getLatitude() , mCurrentPoi.getLatLonPoint().getLongitude());
  }
  
  @Override
  public View getInfoContents(Marker arg0){
      return null;
  }
  
  @Override
  public View getInfoWindow(Marker arg0){
      return null;
  }
  
  @Override
  public void onInfoWindowClick(Marker arg0){
		// TODO Auto-generated method stub
  }
  
  
	/**
	 *      myPoiOverlay类，该类下面有多个方法
	 */
  private class myPoiOverlay{
      private AMap mamap;
      private List<PoiItem> mPois;
      private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
      //构造函数，传进来的是amap对象和查询到的结果items  mPois
      public myPoiOverlay(AMap amap,List<PoiItem>pois){
          mamap = amap;
          mPois = pois;
      }

  	/**
  	 *      增加Maker到地图中
  	 */
  public void addToMap(){
	  Log.e("yifan","addtomap");
      for(int i=0;i<mPois.size();i++){
          Marker marker = mamap.addMarker(getMarkerOptions(i));
          PoiItem item = mPois.get(i);
          marker.setObject(item);
          mPoiMarks.add(marker);
      }
  }
  
	/**
	 *      移除所有的marker
	 */
  public void removeFromMap(){
      for(Marker mark: mPoiMarks){
          mark.remove();
      }
  }
  
	/**
	 *      移动镜头到当前的视角
	 */
  public void zoomToSpan(){
      if(mPois !=null && mPois.size()>0){
          if(mamap ==null) return;
          LatLngBounds bounds = getLatLngBounds();
         //瞬间移动到目标位置
          mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100));
      }
  }

  private LatLngBounds getLatLngBounds(){
      LatLngBounds.Builder b = LatLngBounds.builder();
      for(int i=0;i<mPois.size();i++){
          b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),mPois.get(i).getLatLonPoint().getLongitude()));
      }
      return b.build();
  }

  private MarkerOptions getMarkerOptions(int index){
      return new MarkerOptions()
              .position(new LatLng(mPois.get(index).getLatLonPoint()
              .getLatitude(),mPois.get(index)
              .getLatLonPoint().getLongitude()))
              .title(getTitle(index)).snippet(getSnippet(index))
              .icon(getBitmapDescriptor(index));

  }

  protected String getTitle(int index){
      return mPois.get(index).getTitle();
  }
  protected String getSnippet(int index){
      return mPois.get(index).getSnippet();
  }
  
	/**
	 *      获取位置，第几个index就第几个poi
	 */
  public int getPoiIndex(Marker marker){
      for(int i=0;i<mPoiMarks.size();i++){
          if(mPoiMarks.get(i).equals(marker)){
              return i;
          }
      }
      return -1;
  }

  public PoiItem getPoiItem(int index) {
	  if (index < 0 || index >= mPois.size()) {
          return null;
      }
      return mPois.get(index);
  }

  protected BitmapDescriptor getBitmapDescriptor(int arg0){
      if(arg0<10){
          BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                  BitmapFactory.decodeResource(getResources(),markers[arg0]));
          return icon;
      }else {
          BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                  BitmapFactory.decodeResource(getResources(),R.drawable.ic_local_parking_32px));
          return icon;
      }
  }
}

  
  private Handler mHandler = new Handler() {
      @Override
      public void handleMessage (Message msg) {
          super.handleMessage(msg);
          switch (msg.what) {
              case EVENT_SHOW_DIALOG:
                  break;
              case EVENT_DISPLAY_USER_INFORMATION:
            	  mAccountBalanceTV.setText("账户余额: "  + mAccountbalance + "元");
            	  mParkingCouponTV.setText("停车券: "  + mParkingCoupon + "张");
            	  if(mNickName!=null){
                	  mUserCenterTV.setText(mNickName);
            	  }else{
            		  mUserCenterTV.setText(mTeleNumber);
            	  }
            	  if(mHeadPortrait!=null){
            		  mHeadPortrait.setBounds(0, 0, 80, 80);
            		  mUserCenterTV.setCompoundDrawables(mHeadPortrait, null, null, null);
            	  }else{
            			Drawable drawable = getResources().getDrawable(R.drawable.ic_user_center);
            			drawable.setBounds(0, 0, 80, 80);
              		    mUserCenterTV.setCompoundDrawables(drawable, null, null, null);
            	  }
              	 break;
	        case EVENT_SET_UNFINISHED_PARKING_RECORD_ADAPTER:
	             mParkingDetailAdapter = new ParkingDetailAdapter(getApplicationContext(), mParkingDetailList);
	             mParkingDetailListView.setAdapter(mParkingDetailAdapter); 
	    	     mParkingDetailAdapter.notifyDataSetChanged();
	             break;
	        case EVENT_NO_UNFINISHED_PARKING_RECORD:
	        	mParkingDetailListView.setVisibility(View.GONE);
	            mEmptyParkingDetailNotifyTV.setVisibility(View.VISIBLE);
	        	break;
	        case EVENT_SET_PARKING_LIST_ADAPTER:
	        	//if(mList.size()>0){
					 ParkingListAdapter parkingListAdapter= new ParkingListAdapter(getApplicationContext(), mList);
					 mParkingsListView.setAdapter(parkingListAdapter);
					 parkingListAdapter.notifyDataSetChanged();
	        	//}
				 break;
	        case EVENT_DISPLAY_MARKER_INFORMATION:
                //新的marker
                mAMAP.clear();
                mAMAP.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.decodeResource(
                                        getResources(), R.drawable.ic_add_location_48px)))
                        .position(new LatLng(lp.getLatitude(), lp.getLongitude())));
                for(int i=0;i<mList.size();i++){
                	Double latitude = Double.parseDouble(String.valueOf(mList.get(i).get("latitude")));
                	Double longitude = Double.parseDouble(String.valueOf(mList.get(i).get("longitude")));
                	Marker marker = mAMAP.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(BitmapFactory.decodeResource(
                                            getResources(), R.drawable.ic_local_parking_32px)))
                            .position(new LatLng(latitude, longitude)));
                	marker.setSnippet(Integer.toString(i));
                	
                }
                AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
                    // marker 对象被点击时回调的接口
                    // 返回 true 则表示接口已响应事件，否则返回false
                    @Override
                    public boolean onMarkerClick(Marker marker) {
        			    mDialogParkingNameTV.setText(mList.get(Integer.parseInt(marker.getSnippet())).get("parkName") + "");
        		    	mDialogParkingFreeNumberTV.setText("空闲: " + mList.get(Integer.parseInt(marker.getSnippet())).get("idleLocationNumber"));
        		    	mDialogParkingFeeTV.setText("计费: " +mList.get(Integer.parseInt(marker.getSnippet())).get("feeScale"));
        		    	mDialogParkingFreeDurationTV.setText("免费时长: " + mList.get(Integer.parseInt(marker.getSnippet())).get("parkingFreeTime") + "h");
        		    	mDialogCertificationTV = (TextView)findViewById(R.id.tv_parking_type_certification_or_not_dialog);
        		    	mDialogParkingChargeTV = (TextView)findViewById(R.id.tv_parking_type_certification_or_not_dialog);
        		    	mDialogParkingAutoChargeTV= (TextView)findViewById(R.id.tv_parking_type_manual_or_auto_dialog);
        		    	mDialogParkingNetworkChargeTV= (TextView)findViewById(R.id.tv_parking_type_network_or_venue_dialog);
        		    	mDialogCashPaymentTV= (TextView)findViewById(R.id.tv_payment_type_cash_dialog);
        		    	mDialogCardPaymentTV= (TextView)findViewById(R.id.tv_payment_type_pos_dialog);
        		    	mDialogAliPaymentTV= (TextView)findViewById(R.id.tv_payment_type_ali_dialog);
        		    	mDialogWechatPaymentTV= (TextView)findViewById(R.id.tv_payment_type_wechat_dialog);
                        return true;
                    }
                };
                // 绑定 Marker 被点击事件
                mAMAP.setOnMarkerClickListener(markerClickListener);
                dismissProgressDialog();
	  		    mDialogMain.setVisibility(View.VISIBLE);
			    mDialogParkingNameTV.setText(mList.get(0).get("parkName") + "");
			    mDialogParkingFreeNumberTV.setText("空闲: " + mList.get(0).get("idleLocationNumber"));
		    	mDialogParkingFeeTV.setText("计费: " +mList.get(0).get("feeScale"));
		    	mDialogParkingFreeDurationTV.setText("免费时长: " + mList.get(0).get("parkingFreeTime") + "h");
			    mCurrentDialogLatitude = Double.parseDouble(String.valueOf(mList.get(0).get("latitude")));
			    mCurrentDialogLongtitude =  Double.parseDouble(String.valueOf(mList.get(0).get("longitude")));
			    break;
	        case EVENT_DISMISS_MARKER:
                //新的marker
                mAMAP.clear();
                mAMAP.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.decodeResource(
                                        getResources(), R.drawable.ic_add_location_48px)))
                        .position(new LatLng(lp.getLatitude(), lp.getLongitude())));
                dismissProgressDialog();
	  		    mDialogMain.setVisibility(View.GONE);
              default:
                break;
          }
      }
  };
  
	/**
	 * 设置城市
	 */
    public void showCityDialog(){
    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_city, null); // 加载自定义的布局文件
		final Button currentBT=(Button)view.findViewById(R.id.bt_current_city);
		currentBT.setText(mCurrentCity.replace("市", ""));
		Button tianjinBT=(Button)view.findViewById(R.id.bt_city_tj);
		tianjinBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mCurrentCity = "天津";
				currentBT.setText(mCurrentCity.toString());
				mCityTV.setText(mCurrentCity.toString());
				LatLonPoint lp = new LatLonPoint(39.0836500000, 117.2006000000);
				mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
				mDialog.dismiss();
			}
		});
		Button beijingBT=(Button)view.findViewById(R.id.bt_city_bj);
		beijingBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mCurrentCity = "北京";
				mCityTV.setText(mCurrentCity.toString());
				LatLonPoint lp = new LatLonPoint(39.9045200000, 116.4072500000);
				mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
				mDialog.dismiss();
			}
		});
		Button shanghaiBT=(Button)view.findViewById(R.id.bt_city_sh);
		shanghaiBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mCurrentCity = "上海";
				mCityTV.setText(mCurrentCity.toString());
				LatLonPoint lp = new LatLonPoint(31.2303500000, 121.4737200000);
				mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
				mDialog.dismiss();
			}
		});
		Button shenzhenBT=(Button)view.findViewById(R.id.bt_city_sz);
		shenzhenBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mCurrentCity = "深圳";
				mCityTV.setText(mCurrentCity.toString());
				LatLonPoint lp = new LatLonPoint(22.5437500000, 114.0594600000);
				mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
				mDialog.dismiss();
			}
		});
		Button guangzhouBT=(Button)view.findViewById(R.id.bt_city_gz);
		guangzhouBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mCurrentCity = "广州";
				mCityTV.setText(mCurrentCity.toString());
				LatLonPoint lp = new LatLonPoint(23.1290800000, 113.2643600000);
				mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
				mDialog.dismiss();
			}
		});
		Button nanjingBT=(Button)view.findViewById(R.id.bt_city_nj);
		nanjingBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mCurrentCity = "南京";
				mCityTV.setText(mCurrentCity.toString());
				LatLonPoint lp = new LatLonPoint(32.0584400000, 118.7965400000);
				mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
				mDialog.dismiss();
			}
		});
		Button hangzhouBT=(Button)view.findViewById(R.id.bt_city_hz);
		hangzhouBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mCurrentCity = "杭州";
				mCityTV.setText(mCurrentCity.toString());
				LatLonPoint lp = new LatLonPoint(30.2458600000, 120.2101700000);
				mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
				mDialog.dismiss();
			}
		});
		Button xiamenBT=(Button)view.findViewById(R.id.bt_city_xm);
		xiamenBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mCurrentCity = "厦门";
				mCityTV.setText(mCurrentCity.toString());
				LatLonPoint lp = new LatLonPoint(24.4795100000, 118.0894800000);
				mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
				mDialog.dismiss();
			}
		});
		Button zhengzhouBT=(Button)view.findViewById(R.id.bt_city_zz);
		zhengzhouBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mCurrentCity = "郑州";
				mCityTV.setText(mCurrentCity.toString());
				LatLonPoint lp = new LatLonPoint(34.7471900000, 113.6253500000);
				mAMAP.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lp.getLatitude(), lp.getLongitude())));
				mDialog.dismiss();
			}
		});
		final AlertDialog.Builder CitydialogBuilder = new AlertDialog.Builder(MainActivity.this);
		CitydialogBuilder.setView(view); // 自定义dialog
		mDialog = CitydialogBuilder.create();
		mDialog.show();
    }
        
       /* 检查手机上是否安装了指定的软件 
        * @param context 
        * @param packageName：应用包名 
        * @return 
        */  
       public static boolean isAvilible(Context context, String packageName){   
           //获取packagemanager   
           final PackageManager packageManager = context.getPackageManager();  
           //获取所有已安装程序的包信息   
           List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);  
           //用于存储所有已安装程序的包名   
           List<String> packageNames = new ArrayList<String>();  
           //从pinfo中将包名字逐一取出，压入pName list中   
           if(packageInfos != null){   
               for(int i = 0; i < packageInfos.size(); i++){   
                   String packName = packageInfos.get(i).packageName;   
                   packageNames.add(packName);   
               }   
           }   
         //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE   
           return packageNames.contains(packageName);  
        }
       
       public String sendCityInfo(){//将前面定位数据中的city数据传过来
           String info;//前面定位所在城市信息
           Intent intent=this.getIntent();
           info=intent.getStringExtra("city");
           return info;
       }
    
	/**
	 * 驾驶路线规划
	 */
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
    	dismissProgressDialog();
    	mAMAP.clear();
    	Log.e(LOG_TAG,"onDriveRouteSearched->clear");
        if (i == 1000) {
            if (driveRouteResult != null && driveRouteResult.getPaths() != null) {
                if (driveRouteResult.getPaths().size() > 0) {
                    mDriveRouteResult = driveRouteResult;
                    final DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(this, mAMAP, drivePath, driveRouteResult.getStartPos(),
                            driveRouteResult.getTargetPos());
                    drivingRouteOverlay.removeFromMap();
                    //drivingRouteOverlay.setNodeIconVisibility(false);//隐藏转弯的节点
                    drivingRouteOverlay.addToMap();
                    Log.e(LOG_TAG,"onDriveRouteSearched");
                    drivingRouteOverlay.zoomToSpan();
                    mIsZoomByRoute = true;
                    mDialogRouteBT.setText("关闭路线");
                    //mCloseRouteBT.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		    progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    progDialog.setIndeterminate(false);
		    progDialog.setCancelable(true);
		    progDialog.setMessage("正在搜索");
		    progDialog.show();
	    }

	/**
	 * 隐藏进度框
	 */
	private void dismissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}
	
  @Override
  public void onPoiSearched(PoiResult result, int rcode){
	// TODO Auto-generated method stub
  }
  
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
    	// TODO Auto-generated method stub
    }
    
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
    	// TODO Auto-generated method stub
    }
    
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
    	// TODO Auto-generated method stub
    }
    
	@Override
	public void onGetInputtips(List<Tip> arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
    }

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
    }
	
    /**
 	 * Add for request display user center information
 	 * */
 	public boolean clientQueryUser() throws ParseException, IOException, JSONException{
 		Log.e(LOG_TAG,"clientQueryUser->enter clientQueryAccount");  
 		HttpClient httpClient = new DefaultHttpClient();
 		  httpClient.getParams().setIntParameter(  
                   HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
 		  httpClient.getParams().setIntParameter(  
                   HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
 		  String strurl = "http://" + this.getString(R.string.ip) + "/itspark/owner/userCenter/query";
 		  HttpPost request = new HttpPost(strurl);
 		  request.addHeader("Accept","application/json");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  QueryUserInfo info = new QueryUserInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_USER_CENTER_INFORMATION_CODE, mTeleNumber, readToken());
		  info.setHeader(header);
		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientQueryUser-> param is " + JacksonJsonUtil.beanToJson(info));
 		  request.setEntity(se);//发送数据
 		  try{
 			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
 			  int code = httpResponse.getStatusLine().getStatusCode();
 			  if(code==HttpStatus.SC_OK){
 				  String strResult = EntityUtils.toString(httpResponse.getEntity());
 				  Log.e(LOG_TAG,"clientQueryUser->strResult is " + strResult);
 				  CommonResponse res = new CommonResponse(strResult);
 				  if(res.getResCode().equals("100")){
 					mAccountbalance = (String)res.getPropertyMap().get("accountBalance");
         			mParkingCoupon = (String)res.getPropertyMap().get("parkingCoupon");
         			mNickName = (String)res.getPropertyMap().get("nickName");
         			byte[] headPortraitByteArray = new byte[1024];
         			headPortraitByteArray = ((String)res.getPropertyMap().get("headportrait")).getBytes();
         			if(headPortraitByteArray!=null){
         				mHeadPortrait=bytes2Drawable(Base64.decode(headPortraitByteArray, Base64.NO_WRAP));
         			}
 					 return true;
 				  }else{
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
 	 * 显示用户中心信息Task
 	 */
 	public class UserCenterDisplayTask extends AsyncTask<Void, Void, Boolean> {
 		@Override
 		protected Boolean doInBackground(Void... params) {
 			try{
 				Log.e(LOG_TAG,"UserCenterDisplayTask->doInBackground");  
 				return clientQueryUser();
 			}catch(Exception e){
 				Log.e(LOG_TAG,"UserCenterDisplayTask->exists exception ");  
 				e.printStackTrace();
 			}
 			return false;
 		}

 		@Override
 		protected void onPostExecute(final Boolean success) {
 			mDisplayUserTask = null;
 			if(success){
		    	Message msg = new Message();
		    	msg.what = EVENT_DISPLAY_USER_INFORMATION;
		    	mHandler.sendMessage(msg);
 			}
 		}

 		@Override
 		protected void onCancelled() {
 			mDisplayUserTask = null;
 		}
 		
 	}
    
	/**
	 *      byte[]转换成Drawable  
	 */
    public Drawable bytes2Drawable(byte[] b) {  
        Bitmap bitmap = this.bytes2Bitmap(b);  
        return this.bitmap2Drawable(bitmap);  
    }
    
	/**
	 *     byte[]转换成Bitmap
	 */
    public Bitmap bytes2Bitmap(byte[] b) {  
        if (b.length != 0) {  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        }  
        return null;  
    }  
    
	/**
	 *     Bitmap转换成Drawable
	 */
    public Drawable bitmap2Drawable(Bitmap bitmap) {  
        BitmapDrawable bd = new BitmapDrawable(bitmap);  
        Drawable d = (Drawable) bd;  
        return d;  
    } 
    
	/**
	 *     用户中心列表数据填充
	 */
    public List<Map<String, Object>> getUserCenterData(){  
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        for (int i = 1; i <= 6; i++) {  
            Map<String, Object> map=new HashMap<String, Object>();  
            if(i==1){
                map.put("userCenterFunction",  "车辆管理");
                map.put("userCenterFunctionSpreadImage",  drawable.ic_chevron_right_black_24dp);
                map.put("userCenterFunctionImage",  drawable.ic_exposure_black_18dp);
            }else if(i==2){
                map.put("userCenterFunction",  "停车记录");
                map.put("userCenterFunctionSpreadImage",  drawable.ic_chevron_right_black_24dp);
                map.put("userCenterFunctionImage",  drawable.ic_insert_invitation_black_18dp);
            }else if(i==3){
            	map.put("userCenterFunction",  "我的车位");
            	map.put("userCenterFunctionSpreadImage",  drawable.ic_chevron_right_black_24dp);
                map.put("userCenterFunctionImage",  drawable.ic_directions_car_black_18dp);
            }else if(i==4){
            	map.put("userCenterFunction",  "意见反馈");
            	map.put("userCenterFunctionSpreadImage",  drawable.ic_chevron_right_black_24dp);
                map.put("userCenterFunctionImage",  drawable.ic_border_color_black_18dp);
            }else if(i==5){
                map.put("userCenterFunction",  "消息中心");
                map.put("userCenterFunctionSpreadImage",  drawable.ic_chevron_right_black_24dp);
                map.put("userCenterFunctionImage",  drawable.ic_message_black_18dp);
            }else if(i==6){
            	map.put("userCenterFunction",  "退出账号");
            	map.put("userCenterFunctionSpreadImage",  drawable.ic_chevron_right_black_24dp);
                map.put("userCenterFunctionImage",  drawable.ic_power_settings_new_black_18dp);
            }
            list.add(map);  
        }  
        return list;  
      }

	/**
	 *     退出账号对话框
	 */
    private void showExitDialog(){
        final AlertDialog.Builder exitDialog = new AlertDialog.Builder(MainActivity.this);
        exitDialog.setIcon(R.drawable.ic_exit_to_app_black_24dp);
        exitDialog.setTitle("退出账号");
        exitDialog.setMessage("确定退出当前账号？");
        exitDialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
				mLogoutTask = new UserLogoutTask();
				mLogoutTask.execute((Void) null);
            }
        });
        exitDialog.setNegativeButton("关闭",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
        		// TODO Auto-generated method stub
            }
        });
        exitDialog.show();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
            if((System.currentTimeMillis() - mExitTime) > 2000){  
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
                mExitTime = System.currentTimeMillis();   
            } else {
                Intent intentFinsh = new Intent();  
                intentFinsh.setAction("ExitApp");  
                sendBroadcast(intentFinsh); 
                exit();
                System.exit(0);
            }
            return true;   
        }
        return super.onKeyDown(keyCode, event);
    }
    
    public void exit(){
    	Intent startMain = new Intent(Intent.ACTION_MAIN);
    	startMain.addCategory(Intent.CATEGORY_HOME);
    	startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(startMain);
    	android.os.Process.killProcess(android.os.Process.myPid());
    }
    
	 //封装Toast
	 private void toastWrapper(final String str) {
	      runOnUiThread(new Runnable() {
	          @Override
	           public void run() {
	               Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
	           }
	      });
	 }
	 
	    public String readToken() {
	        SharedPreferences pref = getSharedPreferences(FILE_NAME_TOKEN, MODE_MULTI_PROCESS);
	        String str = pref.getString("token", "");
	        return str;
	    }
	    
	    /**
		 * Add for request parking's information related to location
		 * */
		public boolean clientQueryParking(Double latitude,Double longitude,int range) throws ParseException, IOException, JSONException{
			Log.e(LOG_TAG,"clientQueryParking->enter clientQueryParking");  
			HttpClient httpClient = new DefaultHttpClient();
			  httpClient.getParams().setIntParameter(  
	                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
			  httpClient.getParams().setIntParameter(  
	                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
			  String strurl = "http://" + this.getString(R.string.ip) + "/itspark/owner/queryNearbyParking/query";
			  HttpPost request = new HttpPost(strurl);
			  request.addHeader("Accept","application/json");
			  request.setHeader("Content-Type", "application/json; charset=utf-8");
			  SearchParkInfo info = new SearchParkInfo();
			  CommonRequestHeader header = new CommonRequestHeader();
			  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_NEARBY_PARKING_CODE, mTeleNumber, readToken());
			  info.setHeader(header);
			  info.setLongitude(longitude);
			  info.setLatitude(latitude);
			  if(mParkingType== "150903|150904|150905|150906"){//全部
				  info.setParkingType(3);
			  }else if(mParkingType=="150906"){//路外
				  info.setParkingType(2);
			  }else if(mParkingType=="150903|150904|150905"){//路内
				  info.setParkingType(1);
			  }
			  info.setRange(range);
			  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
			  Log.e(LOG_TAG,"clientQueryParking-> param is " + JacksonJsonUtil.beanToJson(info));
			  request.setEntity(se);//发送数据  
			  try{
				  HttpResponse httpResponse = httpClient.execute(request);//获得响应
				  int code = httpResponse.getStatusLine().getStatusCode();
				  if(code==HttpStatus.SC_OK){
					  String strResult = EntityUtils.toString(httpResponse.getEntity());
					  Log.e(LOG_TAG,"clientQueryParking->strResult is " + strResult);
					  CommonResponse res = new CommonResponse(strResult);
					  toastWrapper(res.getResMsg());  
					  if(res.getResCode().equals("100")){
						  mList = res.getDataList();
						  return true;
					  }else{
				          return false;
					  } 
				}else{
				    Log.e(LOG_TAG, "clientQueryParking->error code is " + Integer.toString(code));
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
		 * 附近停车场查询Task
		 */
		public class UserQueryParkingTask extends AsyncTask<Void, Void, Boolean> {
			private Double latitude;
			private Double longitude;
			private int range;
			public UserQueryParkingTask(Double latitude,Double longitude,int range){
				this.latitude = latitude;
				this.longitude = longitude;
				this.range=range;
			}
			@Override
			protected Boolean doInBackground(Void... params) {
				try{
					Log.e(LOG_TAG,"UserQueryParkingTask->doInBackground");  
					return clientQueryParking(latitude,longitude,range);
				}catch(Exception e){
					Log.e(LOG_TAG,"UserQueryParkingTask->exists exception");  
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(final Boolean success) {
				mQueryParkingTask = null;
				if(success){
					Message msg2 = new Message();
					msg2.what=EVENT_SET_PARKING_LIST_ADAPTER;
					mHandler.sendMessage(msg2);
                    if(mList.size()>0) {
						Message msg1 = new Message();
						msg1.what=EVENT_DISPLAY_MARKER_INFORMATION;
						mHandler.sendMessage(msg1);
                    }else {
						Message msg = new Message();
						msg.what=EVENT_DISMISS_MARKER;
						mHandler.sendMessage(msg);
                  	    Toast.makeText(getApplicationContext(), "未发现附近停车场", Toast.LENGTH_SHORT).show();
                    }
				}
			}

			@Override
			protected void onCancelled() {
				mQueryParkingTask = null;
			}
			
		}
		
    /**
	 * Add for request query unfinished parking record
	 * */
	public boolean clientQueryCurrentRecord(String account) throws ParseException, IOException, JSONException{
		Log.e(LOG_TAG,"clientQueryCurrentRecord->enter clientQuery");  
		HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置 
		  String strurl = "http://" + this.getString(R.string.ip) + "/itspark/owner/queryCurrent/query";
		  Log.e(LOG_TAG,"clientQueryCurrentRecord->url is " + strurl);
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  QueryRecordInfo info = new QueryRecordInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_CURRENT_PARKING_RECORD_CODE, mTeleNumber, readToken());
		  info.setHeader(header);
		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientQueryCurrentRecord-> param is " + JacksonJsonUtil.beanToJson(info));
		  request.setEntity(se);//发送数据
		  
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientQueryCurrentRecord->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  toastWrapper(res.getResMsg());  
				  if(res.getResCode().equals("100")){
					  mParkingDetailList = res.getDataList();
					  return true;
				  }else{
			          return false;
				  } 
			}else{
			    Log.e(LOG_TAG, "clientQueryCurrentRecord->error code is " + Integer.toString(code));
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
	 * 当前停车记录查询Task
	 * 
	 */
	public class UserQueryTask extends AsyncTask<Void, Void, Boolean> {
		private String account;
		public UserQueryTask(String account){
			this.account = account;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				Log.e(LOG_TAG,"UserQueryTask->doInBackground");  
				return clientQueryCurrentRecord(account);
			}catch(Exception e){
				Log.e(LOG_TAG,"UserQueryTask->exists exception ");  
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mQueryTask = null;
			if(success){
			    if(mParkingDetailList.size()!=0){
					  Message msg = new Message();
  		              msg.what=EVENT_SET_UNFINISHED_PARKING_RECORD_ADAPTER;
  		              mHandler.sendMessage(msg);
			   }else{
			          Message msg = new Message();
			          msg.what=EVENT_NO_UNFINISHED_PARKING_RECORD;
			          mHandler.sendMessage(msg);
			   }	
			}
		}

		@Override
		protected void onCancelled() {
			mQueryTask = null;
		}
		
	}
	
	/**
	 * Add for logout
	 * */
	public boolean clientLogout()throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.SO_TIMEOUT, 5000); // 请求超时设置,"0"代表永不超时  
		  httpClient.getParams().setIntParameter(  
                  HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时设置,"0"代表永不超时
		  String strurl = "http://" + 	this.getString(R.string.ip) + "/itspark/owner/userCenter/logout";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  LogoutInfo info = new LogoutInfo();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_LOGOUT_CODE, mTeleNumber, readToken());
		  info.setHeader(header);
		  StringEntity se = new StringEntity(JacksonJsonUtil.beanToJson(info), "UTF-8");
		  Log.e(LOG_TAG,"clientLogout-> param is " + JacksonJsonUtil.beanToJson(info));
		  request.setEntity(se);//发送数据
		  try{
			  HttpResponse httpResponse = httpClient.execute(request);//获得响应
			  int code = httpResponse.getStatusLine().getStatusCode();
			  if(code==HttpStatus.SC_OK){
				  String strResult = EntityUtils.toString(httpResponse.getEntity());
				  Log.e(LOG_TAG,"clientLogout->strResult is " + strResult);
				  CommonResponse res = new CommonResponse(strResult);
				  String resCode = res.getResCode();
				  toastWrapper(res.getResMsg());
				  if(resCode.equals("100")){
					  return true;
				  }else{
					  return false;
				  }
			  }else{
				  Log.e(LOG_TAG, "clientLogout->error code is " + Integer.toString(code));
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
	 * 退出账号Task
	 * 
	 */
	public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				return clientLogout();
			}catch(Exception e){
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mLogoutTask = null;
			if(success){
                Intent intentFinsh = new Intent();  
                intentFinsh.setAction("ExitApp");  
                sendBroadcast(intentFinsh); 
				Intent intent = new Intent(MainActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
	
}
