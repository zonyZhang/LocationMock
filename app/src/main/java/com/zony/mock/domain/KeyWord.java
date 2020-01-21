package com.zony.mock.domain;

import android.os.Parcel;
import android.os.Parcelable;

 /**
  * 关键字
  *
  * @author zony
  * @time 20-1-20 下午4:44
  */
public class KeyWord implements Parcelable {

    /**
     * 关键字
     */
    private String keyWord;

    /**
     * 搜索关键字的创建时间
     */
    private long createTime;

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "KeyWord{" +
            "keyWord='" + keyWord + '\'' +
            ", createTime=" + createTime +
            '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.keyWord);
        dest.writeLong(this.createTime);
    }

    public KeyWord() {
    }

    protected KeyWord(Parcel in) {
        this.keyWord = in.readString();
        this.createTime = in.readLong();
    }

    public static final Creator<KeyWord> CREATOR = new Creator<KeyWord>() {
        @Override
        public KeyWord createFromParcel(Parcel source) {
            return new KeyWord(source);
        }

        @Override
        public KeyWord[] newArray(int size) {
            return new KeyWord[size];
        }
    };
}
