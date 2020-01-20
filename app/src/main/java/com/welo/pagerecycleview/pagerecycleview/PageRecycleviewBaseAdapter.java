package com.welo.pagerecycleview.pagerecycleview;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Amy on 2019-11-14
 */

public abstract class PageRecycleviewBaseAdapter<D, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected OnDataChangeListener<D> onDataChangeListener;

    public abstract void setDataList(List<D> list);

    public abstract List<D> getDataList();

    public abstract<P extends PageRecycleviewBaseAdapter> P getNewAdapter();

    public void setOnDataChangeListener(OnDataChangeListener<D> onDataChangeListener) {
        this.onDataChangeListener = onDataChangeListener;
    }
}
