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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.data.factories.InitialFactory;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.models.MediaList;
import com.nbonnec.mediaseb.ui.adapter.MediasAdapter;
import com.nbonnec.mediaseb.ui.event.MediasLatestPostionEvent;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MediaListFragment extends BaseFragment {
    private static final String TAG = MediaListFragment.class.getSimpleName();

    private static final String STATE_MEDIALIST = "state_medialist";

    @Inject
    MSSService mssService;

    @Bind(R.id.media_list_recycler_view)
    RecyclerView recyclerView;

    private OnFragmentStartedListener onFragmentStartedListener;

    private boolean isLoading;

    private MediasAdapter newsAdapter;

    private MediaList mediaList;

    private static Observable<MediaList> getMediasObservable;

    private Observer<MediaList> getMediasObserver = new Observer<MediaList>() {
        @Override
        public void onCompleted() {
            getMediasObservable = null;
            isLoading = false;
        }

        @Override
        public void onError(Throwable e) {
            getMediasObservable = null;
            isLoading = false;
        }

        @Override
        public void onNext(MediaList nextList) {
            mediaList.setNextPageUrl(nextList.getNextPageUrl());

            if (newsAdapter != null) {
                newsAdapter.addMedias(nextList.getMedias());
                mediaList.setMedias(newsAdapter.getMedias());
            }
        }
    };

    public interface OnFragmentStartedListener {
        void onFragmentStarted();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mediaList = savedInstanceState.getParcelable(STATE_MEDIALIST);
        }

        initAdapters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_media_list, container, false);

        ButterKnife.bind(this, rootView);

        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnFragmentStartedListener) {
            onFragmentStartedListener = null;
            onFragmentStartedListener = (OnFragmentStartedListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentStartedListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (onFragmentStartedListener != null) {
            onFragmentStartedListener.onFragmentStarted();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        if (mediaList != null) {
            saveInstanceState.putParcelable(STATE_MEDIALIST, mediaList);
        }
    }

    @Subscribe
    public void onMediasLatestPositionEvent(MediasLatestPostionEvent event) {
        if (canPullNextMedias(event.getPosition()))
            pullNextMedias();
    }

    public void loadNews() {
        isLoading = true;

        resetAdapters();

        getMediasObservable = mssService
                .getNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        addSubscription(getMediasObservable.subscribe(getMediasObserver));
    }

    public void loadResults(String search) {
        isLoading = true;

        resetAdapters();

        getMediasObservable = mssService
                .getResults(search)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        addSubscription(getMediasObservable.subscribe(getMediasObserver));
    }

    private boolean canPullNextMedias(int position) {
        return !isLoading && mediaList != null &&
                !mediaList.getNextPageUrl().equals(DefaultFactory.MediaList.EMPTY_FIELD_NEXT_PAGE_URL) &&
                position > (newsAdapter.getItemCount() - 10);
    }

    private void pullNextMedias() {
        isLoading = true;

        getMediasObservable = mssService
                .getMediaListFromUrl(mediaList.getNextPageUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        addSubscription(getMediasObservable.subscribe(getMediasObserver));
    }

    private void initAdapters() {
        if (mediaList == null) {
            mediaList = InitialFactory.MediaList.constructInitialInstance();
        }
        newsAdapter = new MediasAdapter(getActivity(), mediaList.getMedias());
    }
    private void resetAdapters() {
        mediaList = InitialFactory.MediaList.constructInitialInstance();
        if (newsAdapter != null) {
            newsAdapter.clearMedias();
        }
    }
}
