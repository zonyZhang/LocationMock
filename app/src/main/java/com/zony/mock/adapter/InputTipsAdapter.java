package com.zony.mock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.zony.mock.R;

import java.util.List;

/**
 * 输入提示adapter，展示item名称和地址
 */
public class InputTipsAdapter extends RecyclerView.Adapter<InputTipsAdapter.ViewHolder> {
    private Context mContext;

    private List<Tip> mListTips;

    public InputTipsAdapter(Context context, List<Tip> tipList) {
        mContext = context;
        mListTips = tipList;
    }

    public List<Tip> getmListTips() {
        return mListTips;
    }

    public void setmListTips(List<Tip> mListTips) {
        this.mListTips = mListTips;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = View.inflate(mContext, R.layout.adapter_inputtips, null);
        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mListTips == null) {
            return;
        }

        holder.mName.setText(mListTips.get(position).getName());
        String address = mListTips.get(position).getAddress();
        holder.mAddress.setText(address);
    }

    @Override
    public int getItemCount() {
        return mListTips == null ? 0 : mListTips.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName;

        TextView mAddress;

        ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mAddress = itemView.findViewById(R.id.adress);
        }
    }
}
