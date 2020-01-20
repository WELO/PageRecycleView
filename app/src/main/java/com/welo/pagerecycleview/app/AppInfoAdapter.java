package com.welo.pagerecycleview.app;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.welo.pagerecycleview.R;
import com.welo.pagerecycleview.databinding.ItemAppBinding;
import com.welo.pagerecycleview.pagerecycleview.PageRecycleviewBaseAdapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;

/**
 * Created by Amy on 2019-12-24
 */

public class AppInfoAdapter extends PageRecycleviewBaseAdapter<AppInfo, AppInfoAdapter.ViewHolder> {

    private final Context context;
    private List<AppInfo> appInfos = new ArrayList<>();

    public AppInfoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void setDataList(List<AppInfo> list) {
        this.appInfos.clear();
        this.appInfos.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public List<AppInfo> getDataList() {
        return appInfos;
    }

    @Override
    public <P extends PageRecycleviewBaseAdapter> P getNewAdapter() {
        return (P) new AppInfoAdapter(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_app,
                parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setAppInfo(appInfos.get(position));
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivAppIcon)
        ImageView ivAppIcon;

        private ItemAppBinding binding;

        public ViewHolder(@NonNull ItemAppBinding binding) {
            super(binding.getRoot());
            ButterKnife.bind(this, binding.getRoot());
            this.binding = binding;
        }

        public void setAppInfo(AppInfo appInfo) {
            binding.setAppInfo(appInfo);
            setIcon(appInfo).subscribe();
            //ivAppIcon.setImageResource(R.drawable.ic_cancel);
            binding.btnAppInfo.setOnClickListener(v -> {
                onDataChangeListener.onDataClicked(appInfo);
            });
        }

        private Completable setIcon(AppInfo appInfo) {
            return Completable.fromAction(() -> {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo app = packageManager.getApplicationInfo(appInfo.getPackageName(), 0);
                Drawable drawable = packageManager.getApplicationIcon(app);
                Glide.with(context)
                        .load(drawable)
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .apply(RequestOptions.errorOf(R.drawable.ic_cancel))
                        .into(ivAppIcon);
            });

        }
    }
}
