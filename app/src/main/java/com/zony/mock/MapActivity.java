package com.zony.mock;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.zony.mock.activity.BaseActivity;
import com.zony.mock.activity.InputTipsActivity;
import com.zony.mock.constant.Constants;
import com.zony.mock.db.KeyWordDao;
import com.zony.mock.overlay.PoiOverlay;
import com.zony.mock.service.MockLocationService;
import com.zony.mock.util.LocationUtil;
import com.zony.mock.util.LogUtil;
import com.zony.mock.util.MapUtil;
import com.zony.mock.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static com.zony.mock.util.MapUtil.isMockSwithOn;

/**
 * 地图界面
 *
 * @author zony
 * @time 18-6-19
 */
public class MapActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
    LocationSource, AMapLocationListener, AMap.OnInfoWindowClickListener, AMap.OnMapClickListener,
    AMap.OnMarkerClickListener, AMap.OnMapTouchListener, PoiSearch.OnPoiSearchListener {
    private static final String TAG = "MapActivity";

    /**
     * 地图ｖｉｅｗ
     */
    private MapView mapView;

    /**
     * 设置定位的默认状态
     */

    private MyLocationStyle myLocationStyle;

    /**
     * 模拟位置开关
     */
    private ToggleButton toggleButton;

    /**
     * 打开开发者模式按钮
     */
    private Button developSettingButton;

    /**
     * 存放共享位置的list
     */
    private List<Marker> showMarks;

    private AMap aMap;

    /**
     * 地图中UI相关设置
     */
    private UiSettings mUiSettings;

    private OnLocationChangedListener mListener;

    private AMapLocationClient mlocationClient;

    /**
     * 高德相关设置
     */
    private AMapLocationClientOption mLocationOption;

    /**
     * 接收的marker
     */
    private Marker markerTourist;

    /**
     * 实时位置
     */
    private LatLng realLatLng;

    private String mKeyWords = "";// 要输入的poi搜索关键字

    private ProgressDialog progDialog = null;// 搜索时进度条

    private PoiResult poiResult; // poi返回的结果

    private int currentPage = 1;

    private PoiSearch.Query query;// Poi查询条件类

    private PoiSearch poiSearch;// POI搜索

    private TextView mKeywordsTextView;

    private Marker mPoiMarker;

    private ImageView mCleanKeyWords;

    private boolean IS_MOCK_SERVICE_START = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initPermission();
        init(savedInstanceState);
        initMap();
    }

    /**
     * 初始化view及其他初始化
     *
     * @param savedInstanceState 保存实例状态
     * @author zony
     * @time 18-6-13
     */
    private void init(Bundle savedInstanceState) {
        toggleButton = findViewById(R.id.activity_map_tb);
        developSettingButton = findViewById(R.id.activity_map_develop_setting);
        mapView = findViewById(R.id.map_view);
        mKeywordsTextView = findViewById(R.id.main_keywords);
        mCleanKeyWords = findViewById(R.id.clean_keywords);
        // 此方法必须重写
        mapView.onCreate(savedInstanceState);

        toggleButton.setOnCheckedChangeListener(this);
        developSettingButton.setOnClickListener(this);
        mKeywordsTextView.setOnClickListener(this);
        mCleanKeyWords.setOnClickListener(this);
        showMarks = new ArrayList<>();
    }

    /**
     * 初始化map
     *
     * @author zony
     * @time 18-6-13
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();

            // 如果要设置定位的默认状态，可以在此处进行设置
            myLocationStyle = new MyLocationStyle();
        }

        // 设置小蓝点的图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_myself));
        aMap.setMyLocationStyle(myLocationStyle);

        // 设置定位监听
        aMap.setLocationSource(this);

        aMap.setOnMapTouchListener(this);

        // 实时交通
        aMap.setTrafficEnabled(false);

        // 有普通，卫星，夜间模式
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);

        // 设置定位的类型为 跟随模式
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW));

        // 设置地图默认的比例尺是否显示
        mUiSettings.setScaleControlsEnabled(true);

        // 是否显示默认的定位按钮
        mUiSettings.setMyLocationButtonEnabled(true);

        // 设置地图缩放比例
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        // marker 点击事件
        aMap.setOnMarkerClickListener(this);

        // map 点击事件
        aMap.setOnMapClickListener(this);

        // InfoWindow 点击事件
        aMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        // 设置定位的类型为 跟随模式
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW));

        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //是指定位间隔
            mLocationOption.setInterval(2000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                // 显示系统小蓝点
                mListener.onLocationChanged(aMapLocation);
//                double realLat = aMapLocation.getLatitude();
//                double realLng = aMapLocation.getLongitude();
//                realLatLng = new LatLng(realLat, realLng);
                LogUtil.i(TAG, "onLocationChanged realLatLng: " + realLatLng.toString());
            } else {
                if (aMapLocation != null) {
                    LogUtil.i(TAG, "errorCode: " + aMapLocation.getErrorCode()
                        + ",errorInfo: " + aMapLocation.getErrorInfo());
                }
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
        realLatLng = marker.getPosition();
        return true;
    }

    /**
     * 用marker展示输入提示list选中数据
     *
     * @param tip
     */
    private void addTipMarker(Tip tip) {
        if (tip == null) {
            return;
        }
        mPoiMarker = aMap.addMarker(new MarkerOptions());
        LatLonPoint point = tip.getPoint();
        if (point != null) {
            LatLng markerPosition = new LatLng(point.getLatitude(), point.getLongitude());
            mPoiMarker.setPosition(markerPosition);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15));
        }
        mPoiMarker.setTitle(tip.getName());
        mPoiMarker.setSnippet(tip.getAddress());
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        // 只定位，不进行其他操作
        if (aMap != null && myLocationStyle != null) {
            aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW));
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mapView) {
            mapView.onDestroy();
            mapView = null;
        }
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
            mlocationClient = null;
        }

        if (null != showMarks) {
            showMarks.clear();
            showMarks = null;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (!MapUtil.isGpsOpened(this)) {
                MapUtil.showGpsDialog(this, this.getString(R.string.gps_open), Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                toggleButton.setChecked(false);
            } else {
                if (!isMockSwithOn(MockLocationService.LOCATION_MANAGER)) {
                    MapUtil.showGpsDialog(this, this.getString(R.string.setting_mock_app), Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                    toggleButton.setChecked(false);
                } else {
                    if (realLatLng == null) {
                        ToastUtil.show(this, getString(R.string.mock_location_select));
                        toggleButton.setChecked(false);
                    } else {
                        ToastUtil.show(this, getString(R.string.mock_location_start));
                        Intent startMockServiceIntent = new Intent(this, MockLocationService.class);
                        Bundle bundle = new Bundle();
                        double[] loc = LocationUtil.gcj02_To_Gps84(realLatLng.latitude, realLatLng.longitude);
                        bundle.putDouble("LATITUDE", loc[0]);
                        bundle.putDouble("LONGITUDE", loc[1]);
                        startMockServiceIntent.putExtra(MockLocationService.INPUT_KEY, bundle);
                        startService(startMockServiceIntent);
                        IS_MOCK_SERVICE_START = true;
                        ToastUtil.show(this, getString(R.string.mock_location_start_success));
                    }
                }
            }
        } else {
            if (IS_MOCK_SERVICE_START) {
                Intent stopMockServiceIntent = new Intent(this, MockLocationService.class);
                stopService(stopMockServiceIntent);
                ToastUtil.show(this, getString(R.string.mock_location_stop));
                IS_MOCK_SERVICE_START = false;
                aMap.clear();
                realLatLng = null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_keywords:
                Intent intent = new Intent(this, InputTipsActivity.class);
                startActivityForResult(intent, Constants.REQUEST_CODE);
                break;
            case R.id.clean_keywords:
                mKeywordsTextView.setText("");
                aMap.clear();
                mCleanKeyWords.setVisibility(View.GONE);
                break;
            case R.id.activity_map_develop_setting:
                Intent intentDevelop = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                startActivity(intentDevelop);
                break;
            default:
                break;
        }
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        MockLocationService.LOCATION_MANAGER = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "请同意使用所有权限,否则无法正常使用...", Toast.LENGTH_LONG).show();
                finish();
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
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + mKeyWords);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String keywords) {
        showProgressDialog();// 显示进度框
        currentPage = 1;
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keywords, "", Constants.DEFAULT_CITY);
        // 设置每页最多返回多少条poiitem
        query.setPageSize(10);
        // 设置查第一页
        query.setPageNum(currentPage);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**
     * 输入提示activity选择结果后的处理逻辑
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_CODE_INPUTTIPS && data
            != null) {
            aMap.clear();
            Tip tip = data.getParcelableExtra(Constants.EXTRA_TIP);
            if (tip.getPoiID() == null || tip.getPoiID().equals("")) {
                doSearchQuery(tip.getName());
            } else {
                addTipMarker(tip);
            }
            String keyWord = tip.getName();
            mKeywordsTextView.setText(keyWord);
            KeyWordDao.getInstance(this).saveKeyWord(keyWord);
            if (!tip.getName().equals("")) {
                mCleanKeyWords.setVisibility(View.VISIBLE);
            }
        } else if (resultCode == Constants.RESULT_CODE_KEYWORDS && data != null) {
            aMap.clear();
            String keywords = data.getStringExtra(Constants.KEY_WORDS_NAME);
            if (keywords != null && !keywords.equals("")) {
                doSearchQuery(keywords);
            }
            mKeywordsTextView.setText(keywords);
            if (!keywords.equals("")) {
                mCleanKeyWords.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MapUtil.removeMarker(showMarks);
        markerTourist = MapUtil.addMarker(aMap, latLng, R.mipmap.ic_launcher_round);
        showMarks.add(markerTourist);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        realLatLng = latLng;
        LogUtil.i(TAG, "onMapClick realLatLng: " + realLatLng.toString());
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                + cities.get(i).getCityCode() + "城市编码:"
                + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(this, infomation);
    }

    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                        .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                        && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(this,
                            R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(this,
                    R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
