package com.zony.mock.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.amap.api.services.help.Tip;
import com.zony.mock.db.columns.KeyWordsColumns;
import com.zony.mock.domain.KeyWord;

import java.text.SimpleDateFormat;

/**
 * 数据库工具类
 *
 * @author zony
 * @time 20-1-20 下午5:13
 */
public class DBUtils {

    /**
     * cursor转为KeyWord对象
     *
     * @param c Cursor对象
     * @author zony
     * @time 20-1-20 下午5:14
     */
    public static KeyWord cursorToKeyWord(Cursor c) {
        KeyWord item = new KeyWord();
        item.setKeyWord(c.getString(c.getColumnIndex(KeyWordsColumns.COLUMN_KEYWORD)));
        item.setCreateTime(c.getLong(c.getColumnIndex(KeyWordsColumns.COLUMN_CREATETIME)));
        return item;
    }

    /**
     * KeyWord转为ContentValues对象
     *
     * @param keyWord keyWord关键字
     * @author zony
     * @time 20-1-20 下午5:14
     */
    public static ContentValues keyWordToContentValues(String keyWord) {
        ContentValues cv = new ContentValues();
        cv.put(KeyWordsColumns.COLUMN_KEYWORD, keyWord);
        cv.put(KeyWordsColumns.COLUMN_CREATETIME, System.currentTimeMillis());
        return cv;
    }

    /**
     * KeyWord转为Tip对象
     *
     * @param keyWord keyWord对象
     * @author zony
     * @time 20-1-20 下午5:14
     */
    public static Tip keyWordToTip(KeyWord keyWord) {
        Tip tip = new Tip();
        tip.setName(keyWord.getKeyWord());
        tip.setAddress(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(keyWord.getCreateTime()) + "");
        return tip;
    }
}
