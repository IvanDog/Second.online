package com.example.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.driver.R.color;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParkingCouponAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, Object>> data;  
    private LayoutInflater layoutInflater;
    private Context context;  
    public int clickPosition = -1;
    private static final String LOG_TAG = "ParkingCouponAdapter";

    public ParkingCouponAdapter(Context context,ArrayList<HashMap<String,Object>> data) {
        this.context=context;   
        this.data=data;  
        this.layoutInflater=LayoutInflater.from(context);  
    }
    
    public class ViewHolder{
    	CouponsView couponView;
    	TextView titleTV;
    	TextView startTimeTV;
    	TextView endTimeTV;
    	TextView notifyTV;
    	TextView denominationTV;
        ImageView enterCouponDetailIV;
    	LinearLayout couponHideDetail;
    	TextView couponDetailHideTV;
    }

    @Override
    public int getCount() {
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_parking_coupon, null);
            vh.couponView=(CouponsView)convertView.findViewById(R.id.cv_coupon);
            vh.titleTV = (TextView) convertView.findViewById(R.id.tv_coupon_title);
            vh.startTimeTV = (TextView) convertView.findViewById(R.id.tv_start_time_coupon);
            vh.endTimeTV = (TextView) convertView.findViewById(R.id.tv_end_time_coupon);
            vh.notifyTV = (TextView) convertView.findViewById(R.id.tv_notify_coupon);
            vh.denominationTV = (TextView) convertView.findViewById(R.id.tv_denomination_coupon);
            vh.enterCouponDetailIV=(ImageView)convertView.findViewById(R.id.iv_enter_coupon_detail);
            vh.couponHideDetail=(LinearLayout)convertView.findViewById(R.id.list_parking_coupon_hide);
            vh.couponDetailHideTV=(TextView)convertView.findViewById(R.id.tv_coupon_detail_hide);  
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.titleTV.setText((String)(data.get(position).get("couponTitle")));
        vh.startTimeTV.setText((String)(data.get(position).get("couponStartTime")));
        vh.endTimeTV.setText((String)(data.get(position).get("couponEndTime")));
        vh.notifyTV.setText((String)(data.get(position).get("couponNotify")));
        vh.denominationTV.setText((String)(data.get(position).get("couponDenomination")));
        vh.couponDetailHideTV.setText((String)(data.get(position).get("couponDetail")));
		if (clickPosition == position) {
			if(vh.couponView.isSelected()){
				vh.couponView.setSelected(false);
				vh.couponView.setBackgroundResource(R.color.gray);
			}else{
				vh.couponView.setSelected(true);
				vh.couponView.setBackgroundResource(R.color.orange);
			}
			/*if (vh.enterCouponDetailIV.isSelected()) {
		    	    vh.enterCouponDetailIV.setSelected(false);
		    	    vh.enterCouponDetailIV.setImageResource(R.drawable.ic_chevron_right_black_24dp);
                    vh.couponHideDetail.setVisibility(View.GONE);
                    clickPosition=-1;
		    }else{       
		    	    vh.enterCouponDetailIV.setSelected(true);
		    	    vh.enterCouponDetailIV.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    vh.couponHideDetail.setVisibility(View.VISIBLE);
             }*/
        } else {
        	vh.couponView.setBackgroundColor(color.gray);
            /*vh.couponHideDetail.setVisibility(View.GONE);
            vh.enterCouponDetailIV.setSelected(false);
        	vh.enterCouponDetailIV.setImageResource(R.drawable.ic_chevron_right_black_24dp);*/
        }
		vh.couponView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				clickPosition = position;
		    	notifyDataSetChanged();
			}
		});
        /*vh.enterCouponDetailIV.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
		    	clickPosition = position;
		    	notifyDataSetChanged();
		    }
		});*/
        Log.e(LOG_TAG,"getview");
        return convertView;
    }
}
