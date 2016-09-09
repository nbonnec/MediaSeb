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
import com.nbonnec.mediaseb.data.Rx.RxUtils;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaStatus;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;

@FragmentWithArgs
public class DetailsFragment extends BaseFragment {
    public static final String TAG = DetailsFragment.class.getSimpleName();

    private static final String STATE_PAGE_LOADED = "page_loaded";

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
    @Bind(R.id.details_available)
    TextView availableView;
    @Bind(R.id.details_summary)
    TextView summaryView;

    // Not to reload unnecessarily.
    private boolean pageLoaded = false;

    private Observer<Media> getNoticeObserver = new Observer<Media>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            pageLoaded = false;
        }

        @Override
        public void onNext(Media nextMedia) {
            media.setDetails(nextMedia);
            refreshViews();
            pageLoaded = true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            pageLoaded = savedInstanceState.getBoolean(STATE_PAGE_LOADED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_media_details, container, false);

        ButterKnife.bind(this, rootView);

        titleView.setText(media.getTitle());

        if (media.getAuthor().equals(DefaultFactory.Media.EMPTY_FIELD_AUTHOR)) {
            authorView.setText(media.getAuthor());
        }

        if (!media.getCollection().equals(DefaultFactory.Media.EMPTY_FIELD_COLLECTION)) {
            collectionView.setText(media.getCollection());
        }

        yearView.setText(media.getYear());

        Picasso.with(getContext())
                .load(media.getImageUrl())
                .into(imageViewBack);

        Picasso.with(getContext())
                .load(media.getImageUrl())
                .into(imageView);

        // Necessary on orientation changes.
        // TODO reset position in the scrollview.
        refreshViews();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!pageLoaded) {
            loadNotice(media.getNoticeUrl());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putBoolean(STATE_PAGE_LOADED, pageLoaded);
    }

    private void loadNotice(String page) {

        Observable<Media> getMediasObservable = mssService
                .getMediaDetails(page)
                .compose(RxUtils.<Media>applySchedulers());
        addSubscription(getMediasObservable.subscribe(getNoticeObserver));
    }

    /**
     * We receive some infos after the creation of the view.
     * TODO do something if the image was not loaded by the list.
     */
    private void refreshViews() {
        if (!media.getStatus().equals(MediaStatus.NONE)) {
            if (media.getStatus() == MediaStatus.AVAILABLE) {
                availableView.setText(R.string.available);
            } else {
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                availableView.setText(String.format("Retour le %s", fmt.format(media.getReturnDate())));
            }
        }

        if (!media.getSummary().equals(DefaultFactory.Media.EMPTY_FIELD_SUMMARY)) {
            summaryView.setText(media.getSummary());
        }
    }
}
