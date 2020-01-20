package com.welo.pagerecycleview.pagerecycleview;

import com.welo.pagerecycleview.R;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amy on 2019-11-14
 */

public class PageRecycleViewAdapter<P extends PageRecycleviewBaseAdapter<D, VH>, D, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<PageRecycleViewAdapter.ViewHolder> {

    private final Context context;
    private final P adapter;
    private final int colCount;
    private List<D> dataList = new ArrayList<>();
    private int rvHeight = -1, rvWidth = -1;
    private List<P> pageAdapterList = new ArrayList<>();
    private int pageCount;
    private int pageItemCount;
    private int DEFULT_ROW = 5;
    private boolean isAlreadyAutoSet = false;
    private MutableLiveData<Integer> itemHeight = new MutableLiveData<>();
    private MutableLiveData<Integer> itemWidth = new MutableLiveData<>();
    private OnPageStatueChangedListener onPageStatueChangedListener;

    public interface OnPageStatueChangedListener {
        void onPageCountChanged(int pageCount);

        void onPageRowChanged(int pageRow);
    }

    private OnDataChangeListener<D> onDataChangeListener;

    public PageRecycleViewAdapter(Context context, P adapter, int colCount, int rowCount) {
        this.context = context;
        this.adapter = adapter;
        this.colCount = colCount;
        dataList = adapter.getDataList();
        initPageView(rowCount);
    }

    public PageRecycleViewAdapter(Context context, P adapter, int colCount, int height, int width, int defaultRow) {
        this.context = context;
        this.adapter = adapter;
        this.colCount = colCount;
        dataList = adapter.getDataList();
        rvHeight = height;
        rvWidth = width;
        itemHeight.observeForever(this::autoHeight);
        if (defaultRow > 0) {
            DEFULT_ROW = defaultRow;
        }
        initPageView(DEFULT_ROW);
    }


    private void autoHeight(int itemHeight) {
        Handler handler = new Handler();
        handler.post(() -> {
            if (rvHeight > 0 && rvWidth > 0 && itemHeight > 0 && rvHeight / itemHeight != DEFULT_ROW) {
                initPageView((rvHeight / itemHeight));
                if (null != onPageStatueChangedListener) {
                    onPageStatueChangedListener.onPageRowChanged((rvHeight / itemHeight));
                }
            }
            if (onDataChangeListener != null) {
                setOnDataChangeListener(onDataChangeListener);
            }
        });
    }

    private void initPageView(int row) {
        pageItemCount = colCount * row;
        pageCount = dataList.size() / pageItemCount;
        if (dataList.size() % pageItemCount != 0) {
            pageCount++;
        }
        if (null != onPageStatueChangedListener) {
            onPageStatueChangedListener.onPageCountChanged(pageCount);
        }
        setPageData();
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setOnDataChangeListener(OnDataChangeListener<D> onDataChangeListener) {
        this.onDataChangeListener = onDataChangeListener;
        for (P adapter : pageAdapterList) {
            adapter.setOnDataChangeListener(onDataChangeListener);
        }
    }

    public void setOnPageStatueChangedListener(OnPageStatueChangedListener onPageStatueChangedListener) {
        this.onPageStatueChangedListener = onPageStatueChangedListener;
    }

    private void setPageData() {
        pageAdapterList.clear();
        for (int i = 0; i < pageCount; i++) {
            P newAdapter = adapter.getNewAdapter();
            List<D> pageDataList = new ArrayList<>();
            pageDataList.clear();
            if ((i + 1) * pageItemCount > dataList.size()) {
                pageDataList.addAll(dataList.subList(i * pageItemCount, dataList.size()));
            } else {
                pageDataList.addAll(dataList.subList(i * pageItemCount, (i + 1) * pageItemCount));
            }
            newAdapter.setDataList(pageDataList);
            pageAdapterList.add(newAdapter);
        }
        notifyDataSetChanged();
    }

    public GridLayoutManager newGridLayoutManager() {
        return new GridLayoutManager(context, colCount) {
            @Override
            public void onMeasure(@androidx.annotation.NonNull RecyclerView.Recycler recycler, @androidx.annotation.NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
                int count = state.getItemCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        View view = recycler.getViewForPosition(0);
                        if (view != null) {
                            measureChild(view, widthSpec, heightSpec);
                            int measuredWidth = View.MeasureSpec.getSize(widthSpec);
                            int measuredHeight = view.getMeasuredHeight();
                            if (!isAlreadyAutoSet) {
                                itemHeight.setValue(measuredHeight);
                                itemWidth.setValue(measuredWidth);
                                isAlreadyAutoSet = true;
                            }
                        }
                    }
                }
                super.onMeasure(recycler, state, widthSpec, heightSpec);
            }
        };

    }

    public List<D> getDataList() {
        return dataList;
    }

    @androidx.annotation.NonNull
    @Override
    public PageRecycleViewAdapter.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycleview_page, parent, false);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull PageRecycleViewAdapter.ViewHolder holder, int position) {
        holder.bind(pageAdapterList.get(position));
    }

    @Override
    public int getItemCount() {
        return pageCount;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rvPage)
        RecyclerView rvPage;

        public ViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rvPage.setLayoutManager(newGridLayoutManager());

        }

        public void bind(P adapter) {
            rvPage.setAdapter(adapter);
        }
    }
}
