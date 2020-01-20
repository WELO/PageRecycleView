package com.welo.pagerecycleview;

import com.google.gson.reflect.TypeToken;

import com.anupcowkur.reservoir.Reservoir;
import com.rd.PageIndicatorView;
import com.welo.pagerecycleview.app.AppInfo;
import com.welo.pagerecycleview.app.AppInfoAdapter;
import com.welo.pagerecycleview.app.AppUtility;
import com.welo.pagerecycleview.databinding.ActivityMainBinding;
import com.welo.pagerecycleview.pagerecycleview.OnDataChangeListener;
import com.welo.pagerecycleview.pagerecycleview.PageRecycleViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;
    @BindView(R.id.vpApp)
    ViewPager2 vpApp;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView pageIndicatorView;

    AppInfoAdapter appInfoAdapter;
    private PageRecycleViewAdapter<AppInfoAdapter, AppInfo, AppInfoAdapter.ViewHolder>
            pageRecycleViewAdapter;
    private Context context;

    private String SPF_SAVE_ROW = "ROW";
    private String SPF_CATEGORY = "CATEGORY";
    private String CACHE_APP_INFOS = "cacheAppInfos";
    private MutableLiveData<List<AppInfo>> appInfos = new MutableLiveData<>();


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityMainBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this, binding.getRoot());
        binding.setLifecycleOwner(this);

        context = this;
        binding.setIsLoading(!getCache());
        AppUtility.getAppInfoList(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> binding.setIsLoading(false))
                .subscribe(appInfos -> {
                    Reservoir.put(CACHE_APP_INFOS, appInfos);
                    initAdapter(appInfos);
                }, throwable -> {
                    Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_LONG).show();
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCache();
    }

    private boolean getCache() {
        try {
            Type resultType = new TypeToken<List<AppInfo>>() {
            }.getType();
            List<AppInfo> appInfoList = Reservoir.get(CACHE_APP_INFOS, resultType);
            initAdapter(appInfoList);
            Log.d("TEST", "appInfoList = " + appInfoList.size());
            for (AppInfo appInfo : appInfoList) {
                Log.d("TEST", "appInfo = " + appInfo.toString());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initAdapter(List<AppInfo> appInfos) {
        if (appInfos.size() <= 0)
            return;

        int defulatRow = loadSharedPref(context, SPF_SAVE_ROW, 5, SPF_CATEGORY);
        appInfoAdapter = new AppInfoAdapter(this);
        appInfoAdapter.setDataList(appInfos);
        pageRecycleViewAdapter = new PageRecycleViewAdapter<>(context, appInfoAdapter, 4, vpApp.getMeasuredHeight(), vpApp.getMeasuredWidth(), defulatRow);
        pageIndicatorView.setCount(pageRecycleViewAdapter.getPageCount());
        pageRecycleViewAdapter.setOnPageStatueChangedListener(new PageRecycleViewAdapter.OnPageStatueChangedListener() {
            @Override
            public void onPageCountChanged(int pageCount) {
                pageIndicatorView.setCount(pageCount);
            }

            @Override
            public void onPageRowChanged(int pageRow) {
                saveSharedPref(context, SPF_SAVE_ROW, pageRow, SPF_CATEGORY);
            }
        });
        vpApp.setAdapter(pageRecycleViewAdapter);
        vpApp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset > 0.5 && pageIndicatorView.getSelection() != position + 1) {
                    pageIndicatorView.setSelection(position + 1);
                } else if (positionOffset <= 0.5 && pageIndicatorView.getSelection() != position) {
                    pageIndicatorView.setSelection(position);
                }
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });
        pageRecycleViewAdapter.setOnDataChangeListener(new OnDataChangeListener<AppInfo>() {
            @Override
            public void onDataListChanged(List<AppInfo> list) {
                Toast.makeText(context, "Data List Changed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDataSelected(AppInfo data, boolean isSelected) {
                Toast.makeText(context, data.getAppName() + "is Selected !", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDataClicked(AppInfo data) {
                Toast.makeText(context, data.getAppName() + " is Clicked !", Toast.LENGTH_LONG).show();

            }
        });

    }

    public void saveSharedPref(Context context, String key, int value, String category) {
        SharedPreferences.Editor editor = context.getSharedPreferences(category, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int loadSharedPref(Context context, String key, int value, String category) {
        SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, value);
    }

}

