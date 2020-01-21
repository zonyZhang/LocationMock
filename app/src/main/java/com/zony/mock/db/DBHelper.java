package com.zony.mock.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zony.mock.db.columns.KeyWordsColumns;

/**
 * 数据库操作辅助类
 *
 * @author zony
 * @time 20-1-20 上午11:45
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    private static DBHelper dbHelper = null;

    public static final String DB_LOCK = "db_lock";

    private DBHelper(Context context) {
        super(context, KeyWordsColumns.DB_NAME, null, DB_VERSION);
    }

    public synchronized static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createKeyWordTable(db);
    }

    private void createKeyWordTable(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(KeyWordsColumns.TABLE).append("(");
        sql.append(KeyWordsColumns.COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(KeyWordsColumns.COLUMN_KEYWORD).append(" INTEGER, ");
        sql.append(KeyWordsColumns.COLUMN_CREATETIME).append(" INTEGER ");
        sql.append(")");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == newVersion) {
            return;
        }
        if (oldVersion > newVersion) {
            dropKeyWordTables(db);
            createKeyWordTable(db);
        } else {
            switch (oldVersion) {
                case 1:
//                    upgradeToVersion2(db);
//                case 2:
//                    upgradeToVersion3(db);
                    break;
                default:
                    dropKeyWordTables(db);
                    createKeyWordTable(db);
                    break;
            }
        }
    }

    private void dropKeyWordTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + KeyWordsColumns.TABLE);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropKeyWordTables(db);
        createKeyWordTable(db);
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        db.execSQL("Alter table " + KeyWordsColumns.TABLE + " add column versionCode INTEGER");
    }

    /**
     * 关闭数据库
     *
     * @param db
     * @author zony
     * @time 20-1-20 下午4:24
     */
    public void closeDB(SQLiteDatabase db) {
        if (db != null) {
            db.close();
        }
    }

    /**
     * 关闭cursor
     *
     * @param c
     * @author zony
     * @time 20-1-20 下午4:24
     */
    public void closeCursor(Cursor c) {
        if (c != null) {
            c.close();
        }
    }
}
