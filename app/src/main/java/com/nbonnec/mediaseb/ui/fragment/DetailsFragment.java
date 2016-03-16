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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.models.Media;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@FragmentWithArgs
public class DetailsFragment extends BaseFragment {
    public static final String TAG = DetailsFragment.class.getSimpleName();

    @Arg
    Media media;

    @Inject
    MSSService mssService;

    @Inject
    MSSEndpoints mssEndpoints;

    @Bind(R.id.details_background)
    ImageView imageViewBack;
    @Bind(R.id.details_image)
    ImageView imageView;
    @Bind(R.id.details_title)
    TextView titleView;
    @Bind(R.id.details_author)
    TextView authorView;
    @Bind(R.id.details_collection)
    TextView collectionView;
    @Bind(R.id.details_year)
    TextView yearView;

    private Observer<Media> getNoticeObserver = new Observer<Media>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(Media nextMedia) {
            media.setDetails(nextMedia);
            refreshViews();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_media_details, container, false);

        ButterKnife.bind(this, rootView);

        titleView.setText(media.getTitle());
        authorView.setText(media.getAuthor());
        collectionView.setText(media.getCollection());
        yearView.setText(media.getYear());

        Picasso.with(getContext())
                .load(media.getImageUrl())
                .into(imageViewBack);

        Picasso.with(getContext())
                .load(media.getImageUrl())
                .into(imageView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotice(media.getNoticeUrl());
    }

    private void loadNotice(String page) {

        Observable<Media> getMediasObservable = mssService
                .getMediaDetailsFromUrl(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        addSubscription(getMediasObservable.subscribe(getNoticeObserver));
    }

    private void refreshViews() {
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}