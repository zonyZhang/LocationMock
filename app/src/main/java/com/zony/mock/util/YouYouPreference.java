package com.zony.mock.util;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences
 *
 * @author zony
 * @time 18-6-20
 */
public class YouYouPreference {

    private static final String DOWNLOAD_PREF_NAME = "user_prefs";

    private static YouYouPreference m_stInstance;

    private Context mContext;

    private SharedPreferences mPrefs;

    private SharedPreferences.Editor mEditor;

    private YouYouPreference(Context context) {
        mContext = context.getApplicationContext();
        mPrefs = mContext.getSharedPreferences(DOWNLOAD_PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    public static synchronized YouYouPreference getInstance(Context context) {
        if (m_stInstance == null) {
            m_stInstance = new YouYouPreference(context);
        }
        return m_stInstance;
    }


    /**
     * 存储数据
     *
     * @param key
     * @param object
     * @author zony
     * @time 18-6-15
     */
    public void putData(String key, Object object) {
        if (object instanceof String) {
            mEditor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            mEditor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            mEditor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            mEditor.putLong(key, (Long) object);
        } else {
            mEditor.putString(key, object.toString());
        }
        mEditor.commit();
    }

    /**
     * 获取保存的数据
     *
     * @param key
     * @param defaultObject
     * @author zony
     * @time 18-6-15
     */
    public Object getData(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return mPrefs.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return mPrefs.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return mPrefs.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return mPrefs.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return mPrefs.getLong(key, (Long) defaultObject);
        } else {
            return mPrefs.getString(key, null);
        }
    }
}
