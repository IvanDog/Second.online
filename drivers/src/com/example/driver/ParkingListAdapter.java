package com.example.driver;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ParkingListAdapter extends BaseAdapter {  
	  
    private ArrayList<HashMap<String, Object>> data;  
    private LayoutInflater layoutInflater;   
    private Context context;  
    public int clickPosition = -1;
    public ParkingListAdapter(Context context,ArrayList<HashMap<String, Object>> mList){  
        this.context=context;  
        this.data=mList;  
        this.layoutInflater=LayoutInflater.from(context);  
    }
    
    /** 
     * 组件集合，对应list.xml中的控件 
     * @author Administrator 
     */  
    public final class Zujian{  
    	private TextView parkingNameTV; 
        private TextView distanceTV;  
        private TextView locationTV;  
        private TextView parkingNumberTV; 
        private TextView feeTV;
        private TextView parkingDetailTV;
        public View navigation;  
        private LinearLayout parkingInformationDetail;
    	private LinearLayout parkingInformationHideDetail;
    	private TextView parkingNameHideTV; 
        private TextView locationHideTV;  
        private TextView parkingNumberHideTV; 
    	private TextView parkingNumberTotalTV;
    	private TextView parkingNumberIdleTV;
    	private TextView parkingFeeTV;
    	private TextView parkingFreeTimeTV;
    }  
    @Override  
    public int getCount() {  
        return data.size();  
    }  
    /** 
     * 获得某一位置的数据 
     */  
    @Override  
    public Object getItem(int position) {  
        return data.get(position);  
    }  
    /** 
     * 获得唯一标识 
     */  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  
        Zujian zujian=null;  
        if(convertView==null){  
            zujian=new Zujian();  
            //获得组件，实例化组件  
            convertView=layoutInflater.inflate(R.layout.list_parking_list, null);  
            zujian.parkingNameTV=(TextView)convertView.findViewById(R.id.tv_parkingname);  
            zujian.distanceTV=(TextView)convertView.findViewById(R.id.tv_distance);  
            zujian.locationTV=(TextView)convertView.findViewById(R.id.tv_location);  
            zujian.parkingNumberTV=(TextView)convertView.findViewById(R.id.tv_parkingnumber);  
            zujian.feeTV = (TextView)convertView.findViewById(R.id.tv_fee); 
            zujian.parkingDetailTV = (TextView)convertView.findViewById(R.id.tv_parking_details); 
            zujian.navigation=(View)convertView.findViewById(R.id.linear_navigation);
            zujian.parkingInformationDetail = (LinearLayout)convertView.findViewById(R.id.list_parking_list); 
            zujian.parkingInformationHideDetail = (LinearLayout)convertView.findViewById(R.id.list_parking_list_hide); 
            zujian.parkingNameHideTV=(TextView)convertView.findViewById(R.id.tv_parkingname_hide);  
            zujian.locationHideTV=(TextView)convertView.findViewById(R.id.tv_location_hide);  
            zujian.parkingNumberTotalTV=(TextView)convertView.findViewById(R.id.tv_parking_number_total_hide);  
            zujian.parkingNumberIdleTV=(TextView)convertView.findViewById(R.id.tv_parking_number_idle_hide);  
            zujian.parkingFeeTV=(TextView)convertView.findViewById(R.id.tv_parking_fee_hide);  
            zujian.parkingFreeTimeTV=(TextView)convertView.findViewById(R.id.tv_parking_free_time_duration_hide);  
            convertView.setTag(zujian);  
        }else{  
            zujian=(Zujian)convertView.getTag();  
        }  
        //绑定数据  
        zujian.parkingNameTV.setText((String)data.get(position).get("parkName"));
        zujian.distanceTV.setText(Integer.parseInt(String.valueOf(data.get(position).get("distance"))) + "米");
        zujian.locationTV.setText((String)data.get(position).get("location"));  
        zujian.parkingNumberTV.setText("空闲: " + Integer.parseInt(String.valueOf(data.get(position).get("idleLocationNumber"))));  
        zujian.feeTV.setText("计费: " + (String)data.get(position).get("feeScale"));  
        zujian.parkingNameHideTV.setText((String)data.get(position).get("parkingName"));
        zujian.locationHideTV.setText("地址：" + (String)data.get(position).get("location"));  
        zujian.parkingNumberTotalTV.setText("总车位: " + Integer.parseInt(String.valueOf(data.get(position).get("totalLocationNumber")))); 
        zujian.parkingNumberIdleTV.setText("空闲: " + Integer.parseInt(String.valueOf(data.get(position).get("idleLocationNumber")))); 
        zujian.parkingFeeTV.setText("计费: " + ((String)data.get(position).get("feeScale"))); 
        zujian.parkingFreeTimeTV.setText("免费时长: " + Integer.parseInt(String.valueOf(data.get(position).get("parkingFreeTime"))) + "h"); 
        zujian.navigation.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){ 
				if (isAvilible(context, "com.autonavi.minimap")) {
                    try{  
                         Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=driver&poiname=parkingName&lat="+data.get(position).get("latitude")+"&lon="+data.get(position).get("longitude")+"&dev=0");  
                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         context.startActivity(intent);   
                    } catch (URISyntaxException e)  {
                    	e.printStackTrace(); 
                    } 
                }else{
                        Toast.makeText(context, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                        Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");  
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);   
                        context.startActivity(intent);
                    }
			}
            });
        //zujian.navigationTV.setText((String)data.get(position).get("navigation"));  
		if (clickPosition == position) {
		    if (zujian.parkingDetailTV.isSelected()) {
		    	    zujian.parkingDetailTV.setSelected(false);
		        	zujian.parkingDetailTV.setText("详情");
                    zujian.parkingInformationHideDetail.setVisibility(View.GONE);
                    clickPosition=-1;
		    }else{      
		    	    zujian.parkingDetailTV.setSelected(true);
		    	    zujian.parkingDetailTV.setText("收起");
                    zujian.parkingInformationHideDetail.setVisibility(View.VISIBLE);
              }
        } else {
        	zujian.parkingInformationHideDetail.setVisibility(View.GONE);
        	zujian.parkingDetailTV.setSelected(false);
        	zujian.parkingDetailTV.setText("详情");
        }
		zujian.parkingDetailTV.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
		    	clickPosition = position;
		    	notifyDataSetChanged();
		    }
		});
        return convertView;  
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
} 