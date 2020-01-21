package com.zony.mock.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.amap.api.services.help.Tip;
import com.zony.mock.db.columns.KeyWordsColumns;
import com.zony.mock.domain.KeyWord;
import com.zony.mock.util.LogUtil;

import java.util.ArrayList;


/**
 * 搜索关键字数据库操作
 *
 * @author zony
 * @time 20-1-20 下午4:31
 */
public class KeyWordDao {
    private static final String TAG = "KeyWordDao";

    private static KeyWordDao instance;

    private DBHelper dbHelper;

    private KeyWordDao(Context context) {
        this.dbHelper = DBHelper.getInstance(context);
    }

    public static synchronized KeyWordDao getInstance(Context context) {
        if (instance == null) {
            instance = new KeyWordDao(context);
        }
        return instance;
    }

    /**
     * 保存搜索关键字
     *
     * @param keyWord 关键字
     * @author zony
     * @time 20-1-20 下午4:52
     */
    public void saveKeyWord(String keyWord) {
        synchronized (DBHelper.DB_LOCK) {
            if (keyWord == null) {
                return;
            }
            String where = KeyWordsColumns.COLUMN_KEYWORD + " = ?";
            String[] whereAgs = {keyWord};
            SQLiteDatabase db = null;
            try {
                db = dbHelper.getWritableDatabase();
                ContentValues values = DBUtils.keyWordToContentValues(keyWord);
                int rows = db.update(KeyWordsColumns.TABLE, values, where, whereAgs);
                if (rows == 0) {
                    LogUtil.i(TAG, "saveKeyWord insert keyWord: " + keyWord);
                    db.insert(KeyWordsColumns.TABLE, null, values);
                } else {
                    LogUtil.i(TAG, "saveKeyWord update keyWord: " + keyWord);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dbHelper.closeDB(db);
            }
        }
    }

    /**
     * 获取关键字
     *
     * @author zony
     * @time 20-1-20 下午5:05
     */
    public ArrayList<Tip> getTips() {
        synchronized (DBHelper.DB_LOCK) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            ArrayList<Tip> tips = new ArrayList<>();
            try {
                db = dbHelper.getWritableDatabase();

                String selection = "select * from " + KeyWordsColumns.TABLE + " limit 0,10";
                String oberby = KeyWordsColumns.COLUMN_CREATETIME + " DESC";
                cursor = db.query(KeyWordsColumns.TABLE, null, null, null,
                    null, null, oberby, " 0,10");
                if (cursor == null) {
                    return null;
                }

                while (cursor.moveToNext()) {
                    KeyWord keyWord = DBUtils.cursorToKeyWord(cursor);
                    LogUtil.i(TAG, "getTips keyWord: " + keyWord);
                    tips.add(DBUtils.keyWordToTip(keyWord));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dbHelper.closeCursor(cursor);
                dbHelper.closeDB(db);
            }
            return tips;
        }
    }

    /**
     * 删除关键字
     *
     * @param keyWord
     * @author zony
     * @time 20-1-20 下午5:12
     */
    public void deleteKeyWord(String keyWord) {
        synchronized (DBHelper.DB_LOCK) {
            SQLiteDatabase db = null;
            try {
                db = dbHelper.getWritableDatabase();
                // 删除条件
                String whereClause = KeyWordsColumns.COLUMN_KEYWORD + "=?";
                // 删除条件参数
                String[] whereArgs = {keyWord};
                db.delete(KeyWordsColumns.TABLE, whereClause, whereArgs);
                LogUtil.d(TAG, "deleteKeyWord success keyWord: " + keyWord);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                dbHelper.closeDB(db);
            }
        }
    }
}
