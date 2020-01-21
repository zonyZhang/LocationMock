package com.zony.mock.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.zony.mock.R;
import com.zony.mock.adapter.InputTipsAdapter;
import com.zony.mock.constant.Constants;
import com.zony.mock.db.KeyWordDao;
import com.zony.mock.util.LogUtil;
import com.zony.mock.util.ToastUtil;

import java.util.List;

public class InputTipsActivity extends BaseActivity implements SearchView.OnQueryTextListener,
    Inputtips.InputtipsListener, OnItemClickListener, View.OnClickListener, SwipeMenuCreator, OnItemMenuClickListener {
    private static final String TAG = "InputTipsActivity";

    private SearchView mSearchView;// 输入搜索关键字

    private SwipeRecyclerView mRecyclerView;

    private List<Tip> mCurrentTipList;

    private InputTipsAdapter mIntipAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_tips);
        initSearchView();
        initRecyclerView();
        findViewById(R.id.back).setOnClickListener(this);
        initData(true);
    }

    /**
     * 初始化recyclerview
     *
     * @author zony
     * @time 20-1-21 上午9:23
     */
    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.inputtip_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemViewSwipeEnabled(false); // 侧滑删除，默认关闭
        mRecyclerView.setOnItemClickListener(this);
        // 设置监听器
        mRecyclerView.setSwipeMenuCreator(this);

        // 菜单点击监听
        mRecyclerView.setOnItemMenuClickListener(this);
    }

    private void initSearchView() {
        mSearchView = findViewById(R.id.keyWord);
        mSearchView.setOnQueryTextListener(this);
        //设置SearchView默认为展开显示
        mSearchView.setIconified(false);
        mSearchView.onActionViewExpanded();
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setSubmitButtonEnabled(false);
    }

    /**
     * 初始化数据
     *
     * @author zony
     * @time 20-1-21 上午11:04
     */
    private void initData(boolean isSearchHistor) {
        if (isSearchHistor) {
            mCurrentTipList = KeyWordDao.getInstance(this).getTips();
            mRecyclerView.setSwipeItemMenuEnabled(true);
        } else {
            mRecyclerView.setSwipeItemMenuEnabled(false);
        }
        if (mIntipAdapter != null) {
            mIntipAdapter.setmListTips(mCurrentTipList);
        } else {
            mIntipAdapter = new InputTipsAdapter(getApplicationContext(), mCurrentTipList);
            mRecyclerView.setAdapter(mIntipAdapter);
        }
        mIntipAdapter.notifyDataSetChanged();
    }

    /**
     * 输入提示回调
     *
     * @param tipList
     * @param rCode
     */
    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        LogUtil.i(TAG, "onGetInputtips: ");
        if (rCode == 1000) {// 正确返回
            mCurrentTipList = tipList;
            initData(false);
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    @Override
    public void onItemClick(View view, int adapterPosition) {
        if (mCurrentTipList != null) {
            Tip tip = mCurrentTipList.get(adapterPosition);
            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_TIP, tip);
            setResult(Constants.RESULT_CODE_INPUTTIPS, intent);
            this.finish();
        }
    }

    /**
     * 按下确认键触发，本例为键盘回车或搜索键
     *
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        LogUtil.i(TAG, "onQueryTextSubmit query: " + query);
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_WORDS_NAME, query);
        setResult(Constants.RESULT_CODE_KEYWORDS, intent);
        KeyWordDao.getInstance(this).saveKeyWord(query);
        this.finish();
        return false;
    }

    /**
     * 输入字符变化时触发
     *
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        LogUtil.i(TAG, "onQueryTextChange newText: " + newText);
        if (!TextUtils.isEmpty(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, Constants.DEFAULT_CITY);
            Inputtips inputTips = new Inputtips(InputTipsActivity.this.getApplicationContext(), inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        } else {
            if (mIntipAdapter != null && mCurrentTipList != null) {
                mCurrentTipList = KeyWordDao.getInstance(this).getTips();
                initData(true);
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            this.finish();
        }
    }

    @Override
    public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        // 添加右侧的，如果不添加，则右侧不会出现菜单。
        {
            SwipeMenuItem deleteItem = new SwipeMenuItem(InputTipsActivity.this).setBackground(
                R.drawable.selector_red)
                .setImage(R.mipmap.ic_action_delete)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
            rightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

            SwipeMenuItem closeItem = new SwipeMenuItem(InputTipsActivity.this).setBackground(
                R.drawable.selector_purple).setImage(R.mipmap.ic_close).setWidth(width).setHeight(height);
            rightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。
        }
    }

    @Override
    public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
        // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
        menuBridge.closeMenu();

        // 左侧还是右侧菜单：
        int direction = menuBridge.getDirection();
        // 菜单在Item中的Position：
        int menuPosition = menuBridge.getPosition();
        if (menuPosition == 0) {
            KeyWordDao.getInstance(this).deleteKeyWord(mCurrentTipList.get(adapterPosition).getName());
            initData(true);
        }
    }
}