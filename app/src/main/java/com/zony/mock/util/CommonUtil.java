package com.zony.mock.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.zony.mock.domain.KeyWord;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用util工具类
 *
 * @author zony
 * @time 18-5-7
 */
public class CommonUtil {
    private static final String TAG = "CommonUtil";

    /**
     * 启动Activity
     *
     * @param context  上下文环境
     * @param intent   Intent实例
     * @param isFinish 是否关闭当前页面
     * @author zony
     * @time 18-6-19
     */
    public static void startActivity(Context context, Intent intent, boolean isFinish) {
        if (intent == null || context == null) {
            return;
        }
        try {
            context.startActivity(intent);
            if (isFinish) {
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取uuid
     *
     * @param context 上下文环境
     * @author zony
     * @time 18-6-13
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getUUID(Context context) {
        if (context == null) {
            LogUtil.w(TAG, "CommonUtil context is null, return!");
            return "";
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = null;
        String tmSerial = null;
        String androidId;

        if (tm != null) {
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
        }

        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    /**
     * 获取签名的sha1
     *
     * @param context
     * @author zony
     * @time 18-6-13
     */
    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 说明：移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     * 验证号码 手机号 固话均可
     *
     * @param phoneNumber
     * @author zony
     * @time 18-6-19
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)" +
                "|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)" +
                "|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}  
