package com.example.driver;

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
import com.example.driver.R.drawable;
import com.example.driver.RechargeActivity.UserAccountDisplayTask;


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
 	 *      定位需要的声明g
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
                            Log.e("yifan","onCameraChangeFinish->doSearchQuery");
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
           	     Log.e("yifan","onRegeocodeSearched");
            }
 
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int returnCode) {
                //判断请求是否成功(1000为成功，其他为失败)
           	 Log.e("yifan","onGeocodeSearched");
                if (returnCode == 1000) {
                    if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                            && geocodeResult.getGeocodeAddressList().size() > 0) {
                        GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                        Log.e("yifan", "经纬度值:" + address.getLatLonPoint() + "位置描述:"
                                + address.getFormatAddress());
                        lp = new LatLonPoint(address.getLatLonPoint().getLatitude(), address.getLatLonPoint().getLongitude());
                        //doSearchQuery(address.getCity(),true);
                        searchNearbyParking(true);
                        Log.e("yifan","onGeocodeSearched->doSearchQuery");
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
                Log.e("yifan","getFromLocationNameAsyn");
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
				bundle.putLong("parkingRecordrID", Long.parseLong(String.valueOf (map.get("parkingRecordrID"))));
				bundle.putString("licensePlateNumber", (String)map.get("licensePlateNumber"));
				bundle.putString("parkNumber", (String)map.get("parkNumber"));
				bundle.putString("parkName", (String)map.get("parkName"));
				bundle.putString("carType", (String)map.get("carType"));
				bundle.putString("startTime", (String)map.get("startTime"));
				bundle.putString("leaveTime", (String)map.get("leaveTime"));
				bundle.putString("parkingLocation", (String)map.get("parkingLocation"));
				bundle.putString("feeScale", (String)map.get("feeScale"));
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
				Log.e("yifan","onGetInputtips->resultCode is " + resultCode);
			      if (resultCode == 1000 && mSearchTag==0) {// 正确返回
			    	  mSearchList.setVisibility(View.VISIBLE);
			    	  mDialogMain.setVisibility(View.GONE);
			            List<Map<String,Object>> searchList=new ArrayList<Map<String, Object>>() ;
			            for (int i=0;i<list.size();i++){
			                Map<String, Object> hashMap=new HashMap<String, Object>();
			                hashMap.put("name",list.get(i).getName());
			                Log.e("yifan","name is " + list.get(i).getName());
			                hashMap.put("address",list.get(i).getDistrict());//将地址信息取出放入HashMap中
			                Log.e("yifan","address is " + list.get(i).getDistrict());
			                searchList.add(hashMap);//将HashMap放入表中
			            }
			            Log.e("yifan","newAdapter");
			            ParkingSearchAdapter searchAdapter=new ParkingSearchAdapter(getApplicationContext(),searchList);//新建一个适配器
			            Log.e("yifan","setAdapter");
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
                            Log.e("gouyifan0621", "POI的名称=" + poiResult.getPois().get(i).getTitle());
                            Log.e("gouyifan0621", "POI的距离=" + poiResult.getPois().get(i).getDistance());
                            Log.e("gouyifan0621", "POI的地址=" + poiResult.getPois().get(i).getSnippet());
                            Log.e("gouyifan0621", "POI的纬度=" + poiResult.getPois().get(i).getLatLonPoint().getLatitude());
                            Log.e("gouyifan0621", "POI的经度=" + poiResult.getPois().get(i).getLatLonPoint().getLongitude());
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
                	   Log.e("yifan","query not consistent.");
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
                    Log.e("yifan","onLocationChanged->doSearchQuery");
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
        			    mDialogParkingNameTV.setText(mList.get(Integer.parseInt(marker.getSnippet())).get("parkingName") + "");
        		    	mDialogParkingFreeNumberTV.setText("空闲: " + mList.get(Integer.parseInt(marker.getSnippet())).get("parkingNumberIdle"));
        		    	mDialogParkingFeeTV.setText("计费: " +mList.get(Integer.parseInt(marker.getSnippet())).get("parkingFee"));
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
			    mDialogParkingNameTV.setText(mList.get(0).get("parkingName") + "");
			    mDialogParkingFreeNumberTV.setText("空闲: " + mList.get(0).get("parkingNumberIdle"));
		    	mDialogParkingFeeTV.setText("计费: " +mList.get(0).get("parkingFee"));
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
    	Log.e("yifan","onDriveRouteSearched->clear");
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
                    Log.e("yifan","onDriveRouteSearched");
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
 		  String strurl = "http://" + this.getString(R.string.ip) + ":8080/itspark/owner/userCenter/query";
 		  HttpPost request = new HttpPost(strurl);
 		  request.addHeader("Accept","application/json");
			//request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
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
         			//byte[]  headPortraitByteArray = new byte[]{-119,80,78,71,13,10,26,10,0,0,0,13,73,72,68,82,0,0,0,-128,0,0,0,-128,8,6,0,0,0,-61,62,97,-53,0,0,0,9,112,72,89,115,0,0,11,19,0,0,11,19,1,0,-102,-100,24,0,0,10,79,105,67,67,80,80,104,111,116,111,115,104,111,112,32,73,67,67,32,112,114,111,102,105,108,101,0,0,120,-38,-99,83,103,84,83,-23,22,61,-9,-34,-12,66,75,-120,-128,-108,75,111,82,21,8,32,82,66,-117,-128,20,-111,38,42,33,9,16,74,-120,33,-95,-39,21,81,-63,17,69,69,4,27,-56,-96,-120,3,-114,-114,-128,-116,21,81,44,12,-118,10,-40,7,-28,33,-94,-114,-125,-93,-120,-118,-54,-5,-31,123,-93,107,-42,-68,-9,-26,-51,-2,-75,-41,62,-25,-84,-13,-99,-77,-49,7,-64,8,12,-106,72,51,81,53,-128,12,-87,66,30,17,-32,-125,-57,-60,-58,-31,-28,46,64,-127,10,36,112,0,16,8,-77,100,33,115,-3,35,1,0,-8,126,60,60,43,34,-64,7,-66,0,1,120,-45,11,8,0,-64,77,-101,-64,48,28,-121,-1,15,-22,66,-103,92,1,-128,-124,1,-64,116,-111,56,75,8,-128,20,0,64,122,-114,66,-90,0,64,70,1,-128,-99,-104,38,83,0,-96,4,0,96,-53,99,98,-29,0,80,45,0,96,39,127,-26,-45,0,-128,-99,-8,-103,123,1,0,91,-108,33,21,1,-96,-111,0,32,19,101,-120,68,0,104,59,0,-84,-49,86,-118,69,0,88,48,0,20,102,75,-60,57,0,-40,45,0,48,73,87,102,72,0,-80,-73,0,-64,-50,16,11,-78,0,8,12,0,48,81,-120,-123,41,0,4,123,0,96,-56,35,35,120,0,-124,-103,0,20,70,-14,87,60,-15,43,-82,16,-25,42,0,0,120,-103,-78,60,-71,36,57,69,-127,91,8,45,113,7,87,87,46,30,40,-50,73,23,43,20,54,97,2,97,-102,64,46,-62,121,-103,25,50,-127,52,15,-32,-13,-52,0,0,-96,-111,21,17,-32,-125,-13,-3,120,-50,14,-82,-50,-50,54,-114,-74,14,95,45,-22,-65,6,-1,34,98,98,-29,-2,-27,-49,-85,112,64,0,0,-31,116,126,-47,-2,44,47,-77,26,-128,59,6,-128,109,-2,-94,37,-18,4,104,94,11,-96,117,-9,-117,102,-78,15,64,-75,0,-96,-23,-38,87,-13,112,-8,126,60,60,69,-95,-112,-71,-39,-39,-27,-28,-28,-40,74,-60,66,91,97,-54,87,125,-2,103,-62,95,-64,87,-3,108,-7,126,60,-4,-9,-11,-32,-66,-30,36,-127,50,93,-127,71,4,-8,-32,-62,-52,-12,76,-91,28,-49,-110,9,-124,98,-36,-26,-113,71,-4,-73,11,-1,-4,29,-45,34,-60,73,98,-71,88,42,20,-29,81,18,113,-114,68,-102,-116,-13,50,-91,34,-119,66,-110,41,-59,37,-46,-1,100,-30,-33,44,-5,3,62,-33,53,0,-80,106,62,1,123,-111,45,-88,93,99,3,-10,75,39,16,88,116,-64,-30,-9,0,0,-14,-69,111,-63,-44,40,8,3,-128,104,-125,-31,-49,119,-1,-17,63,-3,71,-96,37,0,-128,102,73,-110,113,0,0,94,68,36,46,84,-54,-77,63,-57,8,0,0,68,-96,-127,42,-80,65,27,-12,-63,24,44,-64,6,28,-63,5,-36,-63,11,-4,96,54,-124,66,36,-60,-62,66,16,66,10,100,-128,28,114,96,41,-84,-126,66,40,-122,-51,-80,29,42,96,47,-44,64,29,52,-64,81,104,-122,-109,112,14,46,-62,85,-72,14,61,112,15,-6,97,8,-98,-63,40,-68,-127,9,4,65,-56,8,19,97,33,-38,-120,1,98,-118,88,35,-114,8,23,-103,-123,-8,33,-63,72,4,18,-117,36,32,-55,-120,20,81,34,75,-111,53,72,49,82,-118,84,32,85,72,29,-14,61,114,2,57,-121,92,70,-70,-111,59,-56,0,50,-126,-4,-122,-68,71,49,-108,-127,-78,81,61,-44,12,-75,67,-71,-88,55,26,-124,70,-94,11,-48,100,116,49,-102,-113,22,-96,-101,-48,114,-76,26,61,-116,54,-95,-25,-48,-85,104,15,-38,-113,62,67,-57,48,-64,-24,24,7,51,-60,108,48,46,-58,-61,66,-79,56,44,9,-109,99,-53,-79,34,-84,12,-85,-58,26,-80,86,-84,3,-69,-119,-11,99,-49,-79,119,4,18,-127,69,-64,9,54,4,119,66,32,97,30,65,72,88,76,88,78,-40,72,-88,32,28,36,52,17,-38,9,55,9,3,-124,81,-62,39,34,-109,-88,75,-76,38,-70,17,-7,-60,24,98,50,49,-121,88,72,44,35,-42,18,-113,19,47,16,123,-120,67,-60,55,36,18,-119,67,50,39,-71,-112,2,73,-79,-92,84,-46,18,-46,70,-46,110,82,35,-23,44,-87,-101,52,72,26,35,-109,-55,-38,100,107,-78,7,57,-108,44,32,43,-56,-123,-28,-99,-28,-61,-28,51,-28,27,-28,33,-14,91,10,-99,98,64,113,-92,-8,83,-30,40,82,-54,106,74,25,-27,16,-27,52,-27,6,101,-104,50,65,85,-93,-102,82,-35,-88,-95,84,17,53,-113,90,66,-83,-95,-74,82,-81,81,-121,-88,19,52,117,-102,57,-51,-125,22,73,75,-91,-83,-94,-107,-45,26,104,23,104,-9,105,-81,-24,116,-70,17,-35,-107,30,78,-105,-48,87,-46,-53,-23,71,-24,-105,-24,3,-12,119,12,13,-122,21,-125,-57,-120,103,40,25,-101,24,7,24,103,25,119,24,-81,-104,76,-90,25,-45,-117,25,-57,84,48,55,49,-21,-104,-25,-103,15,-103,111,85,88,42,-74,42,124,21,-111,-54,10,-107,74,-107,38,-107,27,42,47,84,-87,-86,-90,-86,-34,-86,11,85,-13,85,-53,84,-113,-87,94,83,125,-82,70,85,51,83,-29,-87,9,-44,-106,-85,85,-86,-99,80,-21,83,27,83,103,-87,59,-88,-121,-86,103,-88,111,84,63,-92,126,89,-3,-119,6,89,-61,76,-61,79,67,-92,81,-96,-79,95,-29,-68,-58,32,11,99,25,-77,120,44,33,107,13,-85,-122,117,-127,53,-60,38,-79,-51,-39,124,118,42,-69,-104,-3,29,-69,-117,61,-86,-87,-95,57,67,51,74,51,87,-77,82,-13,-108,102,63,7,-29,-104,113,-8,-100,116,78,9,-25,40,-89,-105,-13,126,-118,-34,20,-17,41,-30,41,27,-90,52,76,-71,49,101,92,107,-86,-106,-105,-106,88,-85,72,-85,81,-85,71,-21,-67,54,-82,-19,-89,-99,-90,-67,69,-69,89,-5,-127,14,65,-57,74,39,92,39,71,103,-113,-50,5,-99,-25,83,-39,83,-35,-89,10,-89,22,77,61,58,-11,-82,46,-86,107,-91,27,-95,-69,68,119,-65,110,-89,-18,-104,-98,-66,94,-128,-98,76,111,-89,-34,121,-67,-25,-6,28,125,47,-3,84,-3,109,-6,-89,-11,71,12,88,6,-77,12,36,6,-37,12,-50,24,60,-59,53,113,111,60,29,47,-57,-37,-15,81,67,93,-61,64,67,-91,97,-107,97,-105,-31,-124,-111,-71,-47,60,-93,-43,70,-115,70,15,-116,105,-58,92,-29,36,-29,109,-58,109,-58,-93,38,6,38,33,38,75,77,-22,77,-18,-102,82,77,-71,-90,41,-90,59,76,59,76,-57,-51,-52,-51,-94,-51,-42,-103,53,-101,61,49,-41,50,-25,-101,-25,-101,-41,-101,-33,-73,96,90,120,90,44,-74,-88,-74,-72,101,73,-78,-28,90,-90,89,-18,-74,-68,110,-123,90,57,89,-91,88,85,90,93,-77,70,-83,-99,-83,37,-42,-69,-83,-69,-89,17,-89,-71,78,-109,78,-85,-98,-42,103,-61,-80,-15,-74,-55,-74,-87,-73,25,-80,-27,-40,6,-37,-82,-74,109,-74,125,97,103,98,23,103,-73,-59,-82,-61,-18,-109,-67,-109,125,-70,125,-115,-3,61,7,13,-121,-39,14,-85,29,90,29,126,115,-76,114,20,58,86,58,-34,-102,-50,-100,-18,63,125,-59,-12,-106,-23,47,103,88,-49,16,-49,-40,51,-29,-74,19,-53,41,-60,105,-99,83,-101,-45,71,103,23,103,-71,115,-125,-13,-120,-117,-119,75,-126,-53,46,-105,62,46,-101,27,-58,-35,-56,-67,-28,74,116,-11,113,93,-31,122,-46,-11,-99,-101,-77,-101,-62,-19,-88,-37,-81,-18,54,-18,105,-18,-121,-36,-97,-52,52,-97,41,-98,89,51,115,-48,-61,-56,67,-32,81,-27,-47,63,11,-97,-107,48,107,-33,-84,126,79,67,79,-127,103,-75,-25,35,47,99,47,-111,87,-83,-41,-80,-73,-91,119,-86,-9,97,-17,23,62,-10,62,114,-97,-29,62,-29,60,55,-34,50,-34,89,95,-52,55,-64,-73,-56,-73,-53,79,-61,111,-98,95,-123,-33,67,127,35,-1,100,-1,122,-1,-47,0,-89,-128,37,1,103,3,-119,-127,65,-127,91,2,-5,-8,122,124,33,-65,-114,63,58,-37,101,-10,-78,-39,-19,65,-116,-96,-71,65,21,65,-113,-126,-83,-126,-27,-63,-83,33,104,-56,-20,-112,-83,33,-9,-25,-104,-50,-111,-50,105,14,-123,80,126,-24,-42,-48,7,97,-26,97,-117,-61,126,12,39,-123,-121,-123,87,-122,63,-114,112,-120,88,26,-47,49,-105,53,119,-47,-36,67,115,-33,68,-6,68,-106,68,-34,-101,103,49,79,57,-81,45,74,53,42,62,-86,46,106,60,-38,55,-70,52,-70,63,-58,46,102,89,-52,-43,88,-99,88,73,108,75,28,57,46,42,-82,54,110,108,-66,-33,-4,-19,-13,-121,-30,-99,-30,11,-29,123,23,-104,47,-56,93,112,121,-95,-50,-62,-12,-123,-89,22,-87,46,18,44,58,-106,64,76,-120,78,56,-108,-16,65,16,42,-88,22,-116,37,-14,19,119,37,-114,10,121,-62,29,-62,103,34,47,-47,54,-47,-120,-40,67,92,42,30,78,-14,72,42,77,122,-110,-20,-111,-68,53,121,36,-59,51,-91,44,-27,-71,-124,39,-87,-112,-68,76,13,76,-35,-101,58,-98,22,-102,118,32,109,50,61,58,-67,49,-125,-110,-111,-112,113,66,-86,33,77,-109,-74,103,-22,103,-26,102,118,-53,-84,101,-123,-78,-2,-59,110,-117,-73,47,30,-107,7,-55,107,-77,-112,-84,5,89,45,10,-74,66,-90,-24,84,90,40,-41,42,7,-78,103,101,87,102,-65,-51,-119,-54,57,-106,-85,-98,43,-51,-19,-52,-77,-54,-37,-112,55,-100,-17,-97,-1,-19,18,-62,18,-31,-110,-74,-91,-122,75,87,45,29,88,-26,-67,-84,106,57,-78,60,113,121,-37,10,-29,21,5,43,-122,86,6,-84,60,-72,-118,-74,42,109,-43,79,-85,-19,87,-105,-82,126,-67,38,122,77,107,-127,94,-63,-54,-126,-63,-75,1,107,-21,11,85,10,-27,-123,125,-21,-36,-41,-19,93,79,88,47,89,-33,-75,97,-6,-122,-99,27,62,21,-119,-118,-82,20,-37,23,-105,21,127,-40,40,-36,120,-27,27,-121,111,-54,-65,-103,-36,-108,-76,-87,-85,-60,-71,100,-49,102,-46,102,-23,-26,-34,45,-98,91,14,-106,-86,-105,-26,-105,14,110,13,-39,-38,-76,13,-33,86,-76,-19,-11,-10,69,-37,47,-105,-51,40,-37,-69,-125,-74,67,-71,-93,-65,60,-72,-68,101,-89,-55,-50,-51,59,63,84,-92,84,-12,84,-6,84,54,-18,-46,-35,-75,97,-41,-8,110,-47,-18,27,123,-68,-10,52,-20,-43,-37,91,-68,-9,-3,62,-55,-66,-37,85,1,85,77,-43,102,-43,101,-5,73,-5,-77,-9,63,-82,-119,-86,-23,-8,-106,-5,109,93,-83,78,109,113,-19,-57,3,-46,3,-3,7,35,14,-74,-41,-71,-44,-43,29,-46,61,84,82,-113,-42,43,-21,71,14,-57,31,-66,-2,-99,-17,119,45,13,54,13,85,-115,-100,-58,-30,35,112,68,121,-28,-23,-9,9,-33,-9,30,13,58,-38,118,-116,123,-84,-31,7,-45,31,118,29,103,29,47,106,66,-102,-14,-102,70,-101,83,-102,-5,91,98,91,-70,79,-52,62,-47,-42,-22,-34,122,-4,71,-37,31,15,-100,52,60,89,121,74,-13,84,-55,105,-38,-23,-126,-45,-109,103,-14,-49,-116,-99,-107,-99,125,126,46,-7,-36,96,-37,-94,-74,123,-25,99,-50,-33,106,15,111,-17,-70,16,116,-31,-46,69,-1,-117,-25,59,-68,59,-50,92,-14,-72,116,-14,-78,-37,-27,19,87,-72,87,-102,-81,58,95,109,-22,116,-22,60,-2,-109,-45,79,-57,-69,-100,-69,-102,-82,-71,92,107,-71,-18,122,-67,-75,123,102,-9,-23,27,-98,55,-50,-35,-12,-67,121,-15,22,-1,-42,-43,-98,57,61,-35,-67,-13,122,111,-9,-59,-9,-11,-33,22,-35,126,114,39,-3,-50,-53,-69,-39,119,39,-18,-83,-68,79,-68,95,-12,64,-19,65,-39,67,-35,-121,-43,63,91,-2,-36,-40,-17,-36,127,106,-64,119,-96,-13,-47,-36,71,-9,6,-123,-125,-49,-2,-111,-11,-113,15,67,5,-113,-103,-113,-53,-122,13,-122,-21,-98,56,62,57,57,-30,63,114,-3,-23,-4,-89,67,-49,100,-49,38,-98,23,-2,-94,-2,-53,-82,23,22,47,126,-8,-43,-21,-41,-50,-47,-104,-47,-95,-105,-14,-105,-109,-65,109,124,-91,-3,-22,-64,-21,25,-81,-37,-58,-62,-58,30,-66,-55,120,51,49,94,-12,86,-5,-19,-63,119,-36,119,29,-17,-93,-33,15,79,-28,124,32,127,40,-1,104,-7,-79,-11,83,-48,-89,-5,-109,25,-109,-109,-1,4,3,-104,-13,-4,99,51,45,-37,0,0,0,32,99,72,82,77,0,0,122,37,0,0,-128,-125,0,0,-7,-1,0,0,-128,-23,0,0,117,48,0,0,-22,96,0,0,58,-104,0,0,23,111,-110,95,-59,70,0,0,7,-66,73,68,65,84,120,-38,-19,-99,9,108,20,85,24,-57,17,5,43,-120,-120,39,81,36,68,-28,16,-28,-82,-83,70,-124,20,33,-94,104,48,40,-94,68,5,-15,8,24,9,106,-60,32,81,2,42,96,84,12,70,17,19,74,91,6,-124,24,-126,28,22,-62,33,-48,110,-95,88,-108,-37,114,-123,74,-95,-126,-106,-101,-118,22,41,-27,-7,-67,-35,111,-55,118,-35,-99,-99,107,119,118,-10,-3,55,-7,-91,-108,-12,-51,-101,125,-33,111,-34,123,51,-13,-114,122,66,-120,122,64,93,28,57,-120,-74,33,51,-111,-76,38,-102,38,56,79,59,-36,69,92,-105,-56,60,83,89,-128,-2,-60,14,-30,25,-113,4,-65,33,-79,-118,-8,38,-111,-46,-90,-78,0,99,9,65,-108,16,13,60,32,-64,80,62,-33,-125,-60,-115,16,-64,62,77,-120,-35,92,-88,-61,-110,60,-8,87,19,91,-7,92,71,-95,9,112,-114,-105,-72,80,119,18,-115,76,-90,-67,-126,-72,-106,-72,-109,-56,98,-119,94,35,-58,16,-109,-120,9,-4,-5,40,-66,122,-17,39,110,39,-46,18,124,-98,16,32,-58,-107,-75,-123,11,119,-92,-127,-65,111,65,12,38,-90,18,107,-120,-93,68,53,113,-119,-113,-95,-57,69,-30,28,81,70,124,79,-68,-49,-3,-112,-21,13,-44,84,-91,124,-116,23,18,93,-5,-92,-70,0,-95,109,-21,-2,40,-63,104,73,-68,78,-28,19,103,13,4,-38,44,82,34,-115,59,-93,-111,58,119,-93,-7,-17,54,-69,-47,87,81,65,-128,43,-119,98,46,-28,-73,66,-2,-65,39,-111,75,-100,-114,67,-48,-93,113,-120,-104,66,-76,-29,115,-72,-127,107,12,-63,53,79,61,8,16,31,-98,-32,66,62,64,60,78,44,51,88,-83,-57,-117,127,-120,79,-119,-49,-7,-9,66,-18,115,64,-128,56,81,-97,11,89,22,118,-83,-117,-127,-113,-58,-77,110,-35,-127,-88,-46,4,-56,-98,-6,-15,36,12,124,16,-7,-64,-86,31,4,112,-98,-101,-119,37,73,28,-8,80,100,-51,52,57,-47,29,-63,84,22,32,-125,-40,-21,-111,-32,-121,34,-17,70,110,-123,0,-10,120,-104,56,21,-85,-80,-25,108,-56,-16,-109,-56,0,7,-14,-117,-103,-25,54,-94,21,4,-80,-58,64,-94,42,86,32,114,124,93,-4,-28,-6,-70,-47,-49,-50,70,-126,98,47,-16,69,-23,98,118,97,39,-111,87,-44,93,-52,-10,5,126,-58,72,-77,-121,104,11,1,-52,-111,101,36,-8,-71,-66,-82,98,-59,-114,-31,-30,64,101,-66,40,63,-79,86,20,-18,29,47,-14,124,-35,-29,38,-127,12,-2,-68,-30,7,-60,47,-27,95,-120,-61,39,11,68,-23,-47,-7,98,-31,-26,1,-100,-89,110,-38,-35,-15,-82,9,82,73,-128,14,68,69,-20,-32,119,19,75,-73,62,45,-86,47,-100,18,-95,31,-33,-66,-9,68,78,97,-25,56,8,-112,-31,-49,115,-9,-111,-7,117,-14,-85,-84,-38,65,82,-12,-12,-53,17,-29,24,69,-4,-72,24,2,-24,32,31,-15,-2,108,36,32,-39,5,29,-60,-42,-14,25,34,-4,115,-24,-60,58,-86,-106,123,56,-34,39,-56,-93,0,47,-40,-108,69,-62,-99,-84,-109,-33,37,81,43,86,-18,122,-43,-33,12,25,56,-50,76,8,-96,-49,12,-93,1,-55,46,-24,40,74,-54,62,-7,-97,0,101,-57,-106,-5,-81,84,-89,5,-112,87,-8,-73,-59,-67,68,85,-11,-31,58,-7,-43,94,-86,17,-7,-37,-97,-9,55,71,6,-113,53,24,2,68,-26,17,115,87,100,15,-15,93,73,63,113,-68,106,-41,-27,96,-100,-81,57,35,86,-20,28,97,38,24,-90,-112,-57,-35,116,96,-118,-1,-70,15,126,-10,87,46,33,-39,-46,-51,8,119,-112,-33,86,66,-128,-80,-89,124,27,-52,87,-53,-35,-59,-126,-97,-6,-120,-51,101,-97,-119,109,-121,102,-118,37,91,-98,-92,32,117,-119,-29,93,0,-11,3,-118,-70,-7,-85,-4,-99,21,57,-44,-33,24,47,-76,-115,25,70,-38,-1,112,-90,67,-128,-70,-68,104,-89,109,-106,-3,-127,89,68,110,-20,-34,-72,35,-56,-10,126,86,65,123,-1,-19,-96,-59,-90,-26,111,-94,43,4,8,-112,-58,-49,-48,-123,98,-52,-126,0,1,-122,40,24,124,-55,73,30,-60,-94,-68,0,-117,21,21,64,-14,-74,-22,2,-76,52,-14,-60,47,-123,41,-30,49,14,-54,10,48,76,-31,-32,7,71,20,-75,81,89,-128,-81,21,23,-64,-79,-71,14,94,20,64,-34,-5,111,-124,0,-103,-45,84,21,64,14,-83,-82,-124,0,-103,75,85,21,-96,13,-73,-127,-86,11,-80,-99,39,-108,-90,-124,0,-14,-95,78,39,-94,55,-111,30,99,54,77,95,4,-33,-49,-15,40,83,-55,-21,115,57,126,76,44,36,22,17,95,-15,-53,-92,-58,-55,38,64,51,-98,66,37,103,-19,-44,-16,88,125,57,-59,-22,15,34,-101,104,31,-31,11,62,-57,127,87,-51,53,-127,-118,92,32,-2,-116,48,-45,-88,21,-49,121,-120,38,77,41,15,-102,73,10,1,-18,-26,-55,-112,122,-106,-53,113,125,-125,-62,-66,-92,-100,60,-39,-100,7,78,-86,-52,45,97,-109,73,-38,-123,-52,50,-118,117,11,57,-56,109,1,90,-16,-104,55,-93,-9,-68,89,97,-85,126,-68,-62,51,106,71,40,-54,-53,92,19,54,12,-103,4,91,108,-94,-7,56,70,116,116,75,0,57,-27,58,-57,66,-121,-89,-111,-30,-17,0,-62,57,19,-46,4,-116,-80,-112,94,-50,-117,108,-28,-122,0,67,120,26,-75,-43,-23,83,-3,17,-4,-53,2,4,-57,10,-26,91,72,47,111,-91,-5,-72,33,64,-74,-59,47,28,28,23,-9,16,-126,127,89,-128,-58,92,-3,87,88,72,47,59,-37,99,-36,16,-32,71,27,51,102,100,-6,62,8,126,29,1,26,-13,-65,-83,28,99,-110,27,2,-84,-73,120,-78,107,32,-128,-29,2,124,4,1,-44,22,-32,67,8,0,1,32,0,4,-128,0,16,0,2,64,0,8,0,1,32,0,4,-128,0,16,0,2,64,0,8,0,1,32,0,4,-128,0,16,0,2,64,-128,100,17,96,53,4,80,91,-128,-75,16,-64,113,1,38,123,73,-128,-46,-112,-75,0,33,-128,51,2,-52,-15,-110,0,123,32,-128,-29,2,-52,-125,0,106,11,48,23,2,64,0,-49,8,-80,15,2,56,46,-64,124,47,9,80,2,1,28,23,-32,75,47,9,-80,10,-73,-127,24,22,14,1,-16,40,24,2,64,0,4,31,2,0,8,0,1,32,0,4,-128,0,16,0,2,64,0,8,0,1,32,0,4,-128,0,16,0,2,64,0,8,0,1,32,0,4,-128,0,16,0,2,64,0,8,0,1,32,0,4,72,54,1,-42,89,60,-39,-107,16,32,-94,0,-41,104,-42,-74,-48,-111,123,46,-68,-29,-122,0,11,44,126,-31,60,13,107,5,-121,11,16,92,44,-38,103,33,-3,95,-60,64,55,4,24,-87,5,118,-68,48,123,-62,-93,53,108,25,19,46,64,112,-71,-8,-15,22,-46,-53,-75,-105,-101,-69,33,-64,109,92,-99,-101,57,-39,10,78,7,1,34,11,32,119,80,-7,-51,100,-11,47,107,82,-41,118,12,-71,-113,56,107,-30,-124,71,105,-40,52,74,79,0,-55,99,-60,121,-125,105,39,106,73,-80,103,-48,96,110,-121,98,78,97,-42,-80,107,-104,17,1,-126,18,-4,-82,-109,70,110,-76,53,86,75,-94,93,-61,-18,-27,-37,-69,-117,17,78,-10,87,-30,41,13,-37,-58,-103,17,64,34,55,-108,26,-89,5,54,-104,-106,-69,-81,-55,-19,-27,-74,17,-45,-119,123,-76,36,-36,55,80,-18,115,-41,-107,59,-121,19,-120,55,120,-6,87,-102,-122,125,3,-83,8,16,-70,-59,-82,-36,-97,73,-18,45,-40,64,75,-95,-99,67,33,-128,49,1,82,118,-21,88,8,0,1,32,0,4,0,16,0,2,64,0,8,0,1,32,0,4,-128,0,74,9,-48,15,-63,87,91,-128,7,-119,90,8,-112,121,-124,-97,-12,41,39,64,51,-94,28,2,100,46,114,-96,44,61,41,-128,-28,77,-59,-125,47,-33,-22,-11,82,89,0,-55,7,-60,-65,10,6,95,-66,66,31,-22,84,57,122,89,0,73,58,49,79,11,-84,34,122,38,10,103,121,4,-116,-107,-62,62,71,-100,-42,57,118,52,78,-101,24,-100,17,78,77,-124,-13,63,65,108,-25,87,-70,-19,-99,44,67,-81,11,16,36,-115,123,-60,-31,-56,81,-77,45,109,12,-101,-106,-125,86,-82,-118,114,108,61,-28,57,77,-75,-104,-25,122,62,70,-109,-112,-29,-23,-66,-46,-123,0,-6,52,-75,33,-64,-93,54,-14,-99,104,49,-49,-43,-119,44,31,8,-96,-49,0,23,4,88,3,1,32,0,4,-128,0,16,0,2,64,0,8,0,1,32,0,4,-128,0,16,0,2,64,0,8,0,1,32,0,4,-128,0,16,0,2,64,0,8,0,1,32,0,4,-128,0,16,-64,8,77,108,44,-93,-42,-37,70,-66,-29,44,10,-80,24,2,56,75,125,27,-53,-88,-35,97,35,-33,65,22,5,-104,6,1,-100,-57,-22,50,106,118,-14,-68,-119,87,55,51,-101,111,95,8,-32,60,-106,-105,81,-77,-55,-69,38,-125,-65,44,-47,101,-93,-118,0,-106,-105,81,-77,73,67,-30,7,-125,121,-106,17,-83,33,64,-4,37,48,-75,-116,-102,3,-56,17,-67,115,98,4,127,19,-47,-42,-115,50,81,77,0,-45,-53,-88,57,-120,108,-37,-25,18,123,-119,83,60,-67,109,57,49,92,103,69,-76,-44,19,0,120,-105,-1,0,83,114,125,-123,-21,121,84,-45,0,0,0,0,73,69,78,68,-82,66,96,-126};
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
			  String strurl = "http://" + this.getString(R.string.ip) + ":8080/park/owner/queryNearbyParking/query";
			  HttpPost request = new HttpPost(strurl);
			  request.addHeader("Accept","application/json");
			  //request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			  request.setHeader("Content-Type", "application/json; charset=utf-8");
			  JSONObject param = new JSONObject();
			  CommonRequestHeader header = new CommonRequestHeader();
			  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_NEARBY_PARKING_CODE, mTeleNumber, readToken());
			  param.put("header", header);
			  param.put("latitude", latitude);
			  param.put("longitude", longitude);
			  param.put("city", mCurrentCity);
			  param.put("parkingType", mParkingType);
			  param.put("range", range);
			  StringEntity se = new StringEntity(param.toString(), "UTF-8");
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
					  }else if(res.getResCode().equals("201")){
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
		  String strurl = "http://" + this.getString(R.string.ip) + ":8080/itspark/owner/queryCurrent/query";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
			//request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
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
		  String strurl = "http://" + 	this.getString(R.string.ip) + ":8080/park/owner/userCenter/logout";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		//request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		  request.setHeader("Content-Type", "application/json; charset=utf-8");
		  JSONObject param = new JSONObject();
		  CommonRequestHeader header = new CommonRequestHeader();
		  header.addRequestHeader(CommonRequestHeader.REQUEST_OWNER_LOGOUT_CODE, mTeleNumber, readToken());
		  param.put("header", header);
		  StringEntity se = new StringEntity(param.toString(), "UTF-8");
		  request.setEntity(se);
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
				  }else if(resCode.equals("201")){
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
