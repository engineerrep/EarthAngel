package com.earth.libbase.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import com.earth.libbase.entity.LocationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationUtil {
    /**
     * 获取经纬度
     * 如果不支持GPS或GPS信号弱，使用网络获取
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    private LocationEntity getLocationByGps(Context context) {
        LocationEntity locationEntity=new LocationEntity();

        double latitude = 0.0;
        double longitude = 0.0;
        List<Double> list=new ArrayList();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  //从gps获取经纬度
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                list.add(latitude);
                longitude = location.getLongitude();
                list.add(longitude);
                locationEntity.setDoublelist(list);
                locationEntity.setAddresseslist(getAddress(context,location));
            } else {
                //GPS信号弱，位置信息可能获取失败
                return getLocationByNet(context);
            }
        } else {
            //网络获取经纬度
            return getLocationByNet(context);
        }
        return locationEntity;
    }

    // 获取地址信息
    public List<Address> getAddress(Context context,Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(context, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }


    //从网络获取经纬度
    @SuppressLint("MissingPermission")
    public  LocationEntity getLocationByNet(Context context) {
        LocationEntity locationEntity=new LocationEntity();
        double latitude = 0.0;
        double longitude = 0.0;
        List<Double> list=new ArrayList();
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            list.add(latitude);
            longitude = location.getLongitude();
            list.add(longitude);
            locationEntity.setDoublelist(list);
            locationEntity.setAddresseslist(getAddress(context,location));
        }else{
            return null;//获取失败
        }
        return locationEntity;
    }

    LocationListener listener = new LocationListener() {

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {

        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {

        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
        }
    };
}
