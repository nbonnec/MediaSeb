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

    private boolean isLoading;

    @Inject
    MSSService mssService;

    @Bind(R.id.media_list_recycler_view)
    RecyclerView recyclerView;

    private MediasAdapter newsAdapter;

    private MediaList newsList;
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
        public void onNext(MediaList news) {
            newsList.setNextPageUrl(news.getNextPageUrl());

            if (newsAdapter != null) {
                newsAdapter.addMedias(news.getMedias());

                newsList.setMedias(newsAdapter.getMedias());
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newsList = InitialFactory.MediaList.constructInitialInstance();
        newsAdapter = new MediasAdapter(getActivity(), newsList.getMedias());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_media_list, container, false);

        ButterKnife.bind(this, rootView);

        newsAdapter = new MediasAdapter(getActivity(), newsList.getMedias());
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Subscribe
    public void onMediasLatestPositionEvent(MediasLatestPostionEvent event) {
        if (canPullNextMedias(event.getPosition()))
            pullNextMedias();
    }

    /* TODO make it public and use this in the activity. */
    public void loadNews() {
        isLoading = true;

        getMediasObservable = mssService
                .getNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        addSubscription(getMediasObservable.subscribe(getMediasObserver));
    }

    private boolean canPullNextMedias(int position) {
        return !isLoading && newsList != null &&
                !newsList.getNextPageUrl().equals(DefaultFactory.MediaList.EMPTY_FIELD_NEXT_PAGE_URL) &&
                position > (newsAdapter.getItemCount() - 10);
    }

    private void pullNextMedias() {
        isLoading = true;

        getMediasObservable = mssService
                .getMediaListFromUrl(newsList.getNextPageUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        addSubscription(getMediasObservable.subscribe(getMediasObserver));
    }
}
