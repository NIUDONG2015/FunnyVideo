package com.simaben.funnyvideo.presenter;

import android.widget.Toast;

import com.simaben.funnyvideo.bean.QiubaiVideo;
import com.simaben.funnyvideo.common.Constants;
import com.simaben.funnyvideo.retrofit.ShowService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by simaben on 13/4/16.
 */
public class HotPresenterImpl implements IHotPresenter {

    private int currentPage = 1;

    private IHotFragmentView iHotFragmentView;

    public HotPresenterImpl(IHotFragmentView iHotFragmentView) {
        this.iHotFragmentView = iHotFragmentView;
    }

    @Override
    public void refresh() {
        currentPage = 1;
        loadVideo(currentPage, "");
    }

    @Override
    public void loadMore() {
        currentPage += 1;
        loadVideo(currentPage, "");
    }


    private void loadVideo(final int page, String keyword) {
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        String timestamp = df.format(new Date());
        rx.Observable<QiubaiVideo> videoObservable = ShowService.createService().video(page + "", "249", timestamp, keyword, "41", "39d5945c3c3248cc91e20b73fb9d619c");
        videoObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<QiubaiVideo, List<QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean>>() {
                    @Override
                    public List<QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean> call(QiubaiVideo qiubaiVideo) {
                        return qiubaiVideo.getShowapi_res_body().getPagebean().getContentlist();
                    }
                })
                .subscribe(new Subscriber<List<QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        iHotFragmentView.setRefreshing(true);
                    }

                    @Override
                    public void onCompleted() {
                        iHotFragmentView.setRefreshing(false);
                        if (currentPage != 1) {
                            iHotFragmentView.notifyMoreLoaded();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        iHotFragmentView.setRefreshing(false);
                        if (currentPage != 1) {
                            iHotFragmentView.notifyMoreLoaded();
                        }
                        iHotFragmentView.toast(e.getMessage()+e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentlistBeans) {
                        if (page == 1) {
                            iHotFragmentView.setRefreshViewData(contentlistBeans);
                        } else {
                            iHotFragmentView.setRefreshViewMoreData(contentlistBeans);
                        }
                    }
                });
    }
}
