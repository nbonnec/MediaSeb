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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.factories.InitialFactory;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.models.MediaList;
import com.nbonnec.mediaseb.ui.adapter.MediasAdapter;


import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MediaListFragment extends BaseFragment {
    private static final String TAG = MediaListFragment.class.getSimpleName();

    @Inject
    MSSService mssService;

    @Bind(R.id.media_list_recycler_view)
    RecyclerView recyclerView;

    private MediasAdapter mediasAdapter;

    private MediaList mediaList;
    private Subscription resultsSubscription;
    private static Observable<MediaList> resultsObservable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaList = InitialFactory.MediaList.constructInitialInstance();
        mediasAdapter = new MediasAdapter(getActivity(), mediaList.getMedias());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_media_list, container, false);

        ButterKnife.bind(this, rootView);

        mediasAdapter = new MediasAdapter(getActivity(), mediaList.getMedias());
        recyclerView.setAdapter(mediasAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadNews();
    }

    public void loadNews() {
        resultsObservable = mssService
                .getResults("walking")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        resultsSubscription = resultsObservable.subscribe(new Observer<MediaList>() {
            @Override
            public void onCompleted() {
                resultsObservable = null;
            }

            @Override
            public void onError(Throwable e) {
                resultsObservable = null;
            }

            @Override
            public void onNext(MediaList news) {
                Log.d(TAG, "Loading results completed.");

                mediaList.setNextPageUrl(news.getNextPageUrl());

                if (mediasAdapter != null) {
                    mediasAdapter.addMedias(news.getMedias());

                    mediaList.setMedias(mediasAdapter.getMedias());
                }
            }
        });
    }
}
