package com.example.driver.view;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.driver.R;
import com.example.driver.R.id;
import com.example.driver.R.layout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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

        private TextView certificatedTV;
        private TextView chargeTV;
        private TextView autoChargeTV;
        private TextView networkChargeTV;
        private TextView cashChargeTV;
        private TextView posChargeTV;
        private TextView alipayChargeTV;
        private TextView wechatpayChargeTV;
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

            zujian.certificatedTV = (TextView)convertView.findViewById(R.id.tv_parking_type_certification_or_not_list);
            zujian.chargeTV = (TextView)convertView.findViewById(R.id.tv_parking_type_free_or_charge_list);
            zujian.autoChargeTV = (TextView)convertView.findViewById(R.id.tv_parking_type_manual_or_auto_list);
            zujian.networkChargeTV = (TextView)convertView.findViewById(R.id.tv_parking_type_network_or_venue_list);
            zujian.cashChargeTV = (TextView)convertView.findViewById(R.id.tv_payment_type_cash_list);
            zujian.posChargeTV = (TextView)convertView.findViewById(R.id.tv_payment_type_pos_list);
            zujian.alipayChargeTV = (TextView)convertView.findViewById(R.id.tv_payment_type_ali_list);
            zujian.wechatpayChargeTV = (TextView)convertView.findViewById(R.id.tv_payment_type_wechat_list);
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
        zujian.parkingFreeTimeTV.setText("免费时长: " + String.valueOf(data.get(position).get("parkingFreeTime")));
        Drawable closeDrawable = context.getResources().getDrawable(R.drawable.ic_close_16px);
        closeDrawable.setBounds(0, 0, closeDrawable.getMinimumWidth(), closeDrawable.getMinimumHeight());
        if(Integer.parseInt(String.valueOf(data.get(position).get("certificated")))==1){
            Drawable certifiDrawable = context.getResources().getDrawable(R.drawable.ic_certification_24px);
            certifiDrawable.setBounds(0, 0, certifiDrawable.getMinimumWidth(), certifiDrawable.getMinimumHeight());
            zujian.certificatedTV.setCompoundDrawables(certifiDrawable,null,closeDrawable,null);
        }
        if(Integer.parseInt(String.valueOf(data.get(position).get("charge")))==1){
            Drawable chargeDrawable = context.getResources().getDrawable(R.drawable.ic_should_pay_24px);
            chargeDrawable.setBounds(0, 0, chargeDrawable.getMinimumWidth(), chargeDrawable.getMinimumHeight());
            zujian.chargeTV.setCompoundDrawables(chargeDrawable,null,closeDrawable,null);
        }
        if(Integer.parseInt(String.valueOf(data.get(position).get("autoCharge")))==1){
            Drawable autoChargeDrawable = context.getResources().getDrawable(R.drawable.ic_pay_auto_24px);
            autoChargeDrawable.setBounds(0, 0, autoChargeDrawable.getMinimumWidth(), autoChargeDrawable.getMinimumHeight());
            zujian.autoChargeTV.setCompoundDrawables(autoChargeDrawable,null,closeDrawable,null);
        }
        if(Integer.parseInt(String.valueOf(data.get(position).get("networkChartge")))==1){
            Drawable netChargeDrawable = context.getResources().getDrawable(R.drawable.ic_network_24px);
            netChargeDrawable.setBounds(0, 0, netChargeDrawable.getMinimumWidth(), netChargeDrawable.getMinimumHeight());
            zujian.networkChargeTV.setCompoundDrawables(netChargeDrawable,null,closeDrawable,null);
        }
        if(Integer.parseInt(String.valueOf(data.get(position).get("cashCharge")))==0){
            Drawable cashChargeDrawable = context.getResources().getDrawable(R.drawable.ic_cash_16px);
            cashChargeDrawable.setBounds(0, 0, cashChargeDrawable.getMinimumWidth(), cashChargeDrawable.getMinimumHeight());
            zujian.cashChargeTV.setCompoundDrawables(cashChargeDrawable,null,closeDrawable,null);
        }
        if(Integer.parseInt(String.valueOf(data.get(position).get("posCharge")))==0){
            Drawable posChargeDrawable = context.getResources().getDrawable(R.drawable.ic_pos_16px);
            posChargeDrawable.setBounds(0, 0, posChargeDrawable.getMinimumWidth(), posChargeDrawable.getMinimumHeight());
            zujian.posChargeTV.setCompoundDrawables(posChargeDrawable,null,closeDrawable,null);
        }
        if(Integer.parseInt(String.valueOf(data.get(position).get("alipayCharge")))==0){
            Drawable aliChargeDrawable = context.getResources().getDrawable(R.drawable.ic_ali_16px);
            aliChargeDrawable.setBounds(0, 0, aliChargeDrawable.getMinimumWidth(), aliChargeDrawable.getMinimumHeight());
            zujian.alipayChargeTV.setCompoundDrawables(aliChargeDrawable,null,closeDrawable,null);
        }
        if(Integer.parseInt(String.valueOf(data.get(position).get("wechatCharge")))==0){
            Drawable wechatChargeDrawable = context.getResources().getDrawable(R.drawable.ic_wechat_16px);
            wechatChargeDrawable.setBounds(0, 0, wechatChargeDrawable.getMinimumWidth(), wechatChargeDrawable.getMinimumHeight());
            zujian.wechatpayChargeTV.setCompoundDrawables(wechatChargeDrawable,null,closeDrawable,null);
        }
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