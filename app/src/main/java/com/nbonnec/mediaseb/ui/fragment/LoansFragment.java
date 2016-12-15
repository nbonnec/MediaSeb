/*
 * Copyright 2016 nbonnec
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nbonnec.mediaseb.ui.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.Rx.RxUtils;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.adapter.LoansAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import timber.log.Timber;

public class LoansFragment extends BaseFragment {

    private static final String STATE_PAGE_LOADED = "state_page_loaded";
    private static final String STATE_LOANS_LIST = "state_loans_list";

    @Inject
    MSSService mssService;

    @Inject
    MSSEndpoints mssEndpoints;

    @Inject
    RxUtils rxUtils;

    @Bind(R.id.media_list_recycler_view)
    RecyclerView recyclerView;

    @Bind(R.id.media_list_flipper_view)
    ViewFlipper viewFlipper;

    @Bind(R.id.media_list_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.media_list_content_layout)
    View contentView;

    @Bind(R.id.media_list_no_content_layout)
    View noContentView;

    @Bind(R.id.media_list_error_layout)
    View errorView;

    @Bind(R.id.media_list_progress_bar_layout)
    View loadingView;

    private LoansAdapter loansAdapter;
    private List<Media> loans = Collections.emptyList();
    private boolean pageLoaded;

    private Observable<List<Media>> getLoansObservable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            loans = savedInstanceState.getParcelableArrayList(STATE_LOANS_LIST);
            pageLoaded = savedInstanceState.getBoolean(STATE_PAGE_LOADED);
        } else {
            pageLoaded = false;
        }

        initAdapters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                LayoutInflater.from(getActivity())
                        .inflate(R.layout.fragment_media_list, container, false);

        ButterKnife.bind(this, rootView);

        recyclerView.setAdapter(loansAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadLoans();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (pageLoaded) {
            showContentView();
        } else if (errorView.getVisibility() == View.GONE) {
            showLoadingView();
            loadLoans();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        saveInstanceState.putParcelableArrayList(STATE_LOANS_LIST,
                (ArrayList<? extends Parcelable>) loans);
        saveInstanceState.putBoolean(STATE_PAGE_LOADED, pageLoaded);
    }

    public void loadLoans() {
        getLoansObservable = mssService
                .getLoans(mssEndpoints.loanUrl())
                .compose(rxUtils.<List<Media>>applySchedulers());
        addSubscription(getLoansObservable.subscribe(new Observer<List<Media>>() {
            @Override
            public void onCompleted() {
                getLoansObservable = null;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                getLoansObservable = null;
                showErrorView();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNext(List<Media> medias) {
                pageLoaded = true;
                loansAdapter.setMedias(medias);
                loans = medias;

                if (loans.isEmpty()) {
                    showNoContentView();
                } else {
                    showContentView();
                }
            }
        }));
    }

    private void initAdapters() {
        loansAdapter = new LoansAdapter(getActivity(), loans);
    }

    private void showLoadingView() {
        Timber.d("Showing loading view '%s'", this.toString());
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(loadingView));
    }

    private void showErrorView() {
        Timber.d("Showing error view '%s'", this.toString());
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(errorView));
    }

    private void showContentView() {
        Timber.d("Showing content view '%s'", this.toString());
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(contentView));
    }

    private void showNoContentView() {
        Timber.d("Showing no content view '%s'", this.toString());
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(noContentView));
    }
}
