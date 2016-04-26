package com.simaben.funnyvideo.presenter;

import java.util.List;

/**
 * Created by simaben on 13/4/16.
 */
public interface IHotFragmentView {
    void setRefreshing(boolean refreshing);
    void notifyMoreLoaded();
    void setRefreshViewData(List data);
    void setRefreshViewMoreData(List data);

    void toast(String msg);
}
