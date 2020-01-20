package com.welo.pagerecycleview.pagerecycleview;

import java.util.List;

/**
 * Created by Amy on 2019-11-14
 */

public interface OnDataChangeListener<D> {
    void onDataListChanged(List<D> list);

    void onDataSelected(D data, boolean isSelected);

    void onDataClicked(D data);
}
