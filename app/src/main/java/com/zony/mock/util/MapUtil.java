package com.zony.mock.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.AlertDialog;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;

import java.util.List;

/**
 * 地图工具类
 *
 * @author zony
 * @time 18-5-7
 */
public class MapUtil {
    private static final String TAG = "MapUtil";

    /**
     * 添加地图标记
     *
     * @param resId    自定义标记图标id
     * @return marker
     * @author zony
     * @time 18-6-14
     */
    public static Marker addMarker(AMap aMap, LatLng latLng, int resId) {
        if (null == aMap) {
            return null;
        }
        MarkerOptions markOptiopns = new MarkerOptions().position(latLng)
            .title("").snippet("longitude:" + latLng.longitude + "\nlatitude:" + latLng.latitude).anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromResource(resId));
        Marker marker = aMap.addMarker(markOptiopns);

        if (null != marker) {
            marker.setObject("Object");
            Animation startAnimation = new AlphaAnimation(0, 1);
            startAnimation.setDuration(600);
            marker.setAnimation(startAnimation);
            marker.startAnimation();
            marker.showInfoWindow();
        }
        return marker;
    }

    /**
     * 移除地图标记
     *
     * @param showMarks     标记集合
     * @param smoothMarkers 平滑移动的标记集合
     * @author zony
     * @time 18-6-14
     */
    public static void removeMarker(List<Marker> showMarks, List<SmoothMoveMarker> smoothMarkers) {
        if (null != showMarks && showMarks.size() > 0) {
            for (Marker showMark : showMarks) {
                if (null != showMark) {
                    showMark.remove();
                }
            }
        }

        if (null != smoothMarkers && smoothMarkers.size() > 0) {
            for (SmoothMoveMarker smoothMoveMarker : smoothMarkers) {
                if (null != smoothMoveMarker) {
                    smoothMoveMarker.destroy();
                }
            }
        }
    }


    /**
     * 移除地图标记
     *
     * @param list 标记集合
     * @author zony
     * @time 18-6-14
     */
    public static void removeMarker(List<Marker> list) {
        if (list != null && list.size() > 0) {
            for (Marker marker : list) {
                if (null != marker) {
                    marker.remove();
                }
            }
        }
    }

    /**
     * 判断手机是否选择了使用当前应用为模拟位置应用
     *
     * @param locationManager
     * @author zony
     * @time 20-1-19 下午7:38
     */
    public static boolean isMockSwithOn(LocationManager locationManager) {
        boolean canMockPosition = false;
        try {
            String providerStr = LocationManager.GPS_PROVIDER;
            try {
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            LocationProvider provider = locationManager.getProvider(providerStr);
            if (provider != null) {
                try {
                    locationManager.addTestProvider(
                        provider.getName(), provider.requiresNetwork(), provider.requiresSatellite(),
                        provider.requiresCell(), provider.hasMonetaryCost(), provider.supportsAltitude(),
                        provider.supportsSpeed(), provider.supportsBearing(), provider.getPowerRequirement(),
                        provider.getAccuracy());
                } catch (SecurityException e) {
                    return false;
                }
            } else {
                locationManager.addTestProvider(
                    providerStr, true, true, false,
                    false, true, true, true,
                    Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
            }
            locationManager.setTestProviderEnabled(providerStr, true);
            locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

            // 模拟位置可用
            canMockPosition = true;
            locationManager.setTestProviderEnabled(providerStr, false);
            locationManager.removeTestProvider(providerStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return canMockPosition;
    }

    /**
     * 判断GPS是否打开
     *
     * @param context
     * @author zony
     * @time 20-1-19 下午7:43
     */
    public static boolean isGpsOpened(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 显示GPS定位dialog
     *
     * @param activity
     * @author zony
     * @time 20-1-19 下午7:43
     */
    public static void showGpsDialog(final Activity activity, String msg, final String action) {
        new AlertDialog.Builder(activity)
            .setTitle("Tips")//这里是表头的内容
            .setMessage(msg)//这里是中间显示的具体信息
            .setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(action);
                        activity.startActivityForResult(intent, 0);
                    }
                }).setNegativeButton("Cancle",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
    }
}
