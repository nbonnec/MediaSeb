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
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.Rx.RxUtils;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.data.factories.InitialFactory;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;
import com.nbonnec.mediaseb.ui.adapter.MediasAdapter;
import com.nbonnec.mediaseb.ui.event.MediasLatestPositionEvent;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import timber.log.Timber;

@FragmentWithArgs
public class MediaListFragment extends BaseFragment implements MediasAdapter.OnItemClickListener {
    // TODO hasFixedSize
    private static final String TAG = MediaListFragment.class.getSimpleName();

    private static final String STATE_MEDIALIST = "state_medialist";
    private static final String STATE_PAGE_LOADED = "page_loaded";
    private static final String STATE_VISIBLE_LAYOUT = "visible_layout";
    private static final String STATE_SAVED_PAGE =  "saved_page";

    private enum VisibleLayout {
        NO_CONTENT_LAYOUT,
        CONTENT_LAYOUT,
        LOADING_LAYOUT,
        ERROR_LAYOUT
    }

    @Arg
    String page;

    @Inject
    MSSService mssService;

    @Bind(R.id.media_list_flipper_view)
    ViewFlipper flipperView;

    @Bind(R.id.media_list_recycler_view)
    RecyclerView recyclerView;

    @Bind(R.id.media_list_content_layout)
    View contentView;

    @Bind(R.id.media_list_progress_bar_layout)
    View loadingView;

    @Bind(R.id.media_list_error_layout)
    View errorView;

    @Bind(R.id.media_list_no_content_layout)
    View noContentView;

    private OnClickedListener listener;

    private boolean isLoading = false;
    private boolean pageLoaded = false;
    private String savedPage;
    private VisibleLayout visibleLayout = VisibleLayout.LOADING_LAYOUT;

    private MediasAdapter mediasAdapter;
    private MediaList mediaList;

    private Observable<MediaList> getMediasObservable;
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
            // TODO if page already loaded -> snackbar
            showErrorView();
        }

        @Override
        public void onNext(MediaList nextList) {
            mediaList.setNextPageUrl(nextList.getNextPageUrl());
            if (mediasAdapter != null) {
                mediasAdapter.addMedias(nextList.getMedias());
                mediaList.setMedias(mediasAdapter.getMedias());
            }

            pageLoaded = true;
            if (mediaList.getMedias().isEmpty()) {
                showNoContentView();
            } else {
                showContentView();
            }
        }
    };

    public interface OnClickedListener {
        void onItemClicked(Media media);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mediaList = savedInstanceState.getParcelable(STATE_MEDIALIST);
            pageLoaded = savedInstanceState.getBoolean(STATE_PAGE_LOADED);
            savedPage = savedInstanceState.getString(STATE_SAVED_PAGE);
            visibleLayout = (VisibleLayout) savedInstanceState.get(STATE_VISIBLE_LAYOUT);
        } else {
            savedPage = page;
        }

        initAdapters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_media_list, container, false);

        ButterKnife.bind(this, rootView);

        recyclerView.setAdapter(mediasAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        switch (visibleLayout) {
            case CONTENT_LAYOUT:
            case LOADING_LAYOUT:
                showContentView();
                break;
            case NO_CONTENT_LAYOUT:
                showNoContentView();
                break;
            case ERROR_LAYOUT:
                showErrorView();
                break;
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;
            try {
                listener = (OnClickedListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnClickedListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        /* when returning to activity, we want to throw MediasLatestPositionEvent if needed */
        isLoading = false;
        mediasAdapter.notifyDataSetChanged();

        if (!pageLoaded) {
            loadPage(savedPage);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        if (mediaList != null) {
            /* TODO
             * fragment is destroyed while loading,
             * we want to make the request again on onResume.
             * I think it's ugly, need to be changed. */
            if (visibleLayout == VisibleLayout.LOADING_LAYOUT) {
                pageLoaded = false;
            }
            saveInstanceState.putParcelable(STATE_MEDIALIST, mediaList);
            saveInstanceState.putBoolean(STATE_PAGE_LOADED, pageLoaded);
            saveInstanceState.putString(STATE_SAVED_PAGE, savedPage);
            saveInstanceState.putSerializable(STATE_VISIBLE_LAYOUT, visibleLayout);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mediasAdapter.clearSubscriptions();
    }

    @Subscribe
    public void onMediasLatestPositionEvent(MediasLatestPositionEvent event) {
        if (canPullNextMedias(event.getPosition()))
            pullNextMedias();
    }

    @OnClick(R.id.error_reload_button)
    public void onClikLoadPage() {
        loadPage(page);
    }


    /**
     * Load a new page.
     * Reset UI, no more list on screen.
     * @param page page to load (URL).
     *
     * TODO refactor. Maybe use the bundle to change the arg of the fragment.
     */
    public void loadPage(String page) {
        savedPage = page;
        isLoading = true;
        resetAdapters();
        showLoadingView();

        getMediasObservable = mssService
                .getMediaList(page)
                .compose(RxUtils.<MediaList>applySchedulers());
        addSubscription(getMediasObservable.subscribe(getMediasObserver));
    }

    private boolean canPullNextMedias(int position) {
        return !isLoading && mediaList != null &&
                !mediaList.getNextPageUrl().equals(DefaultFactory.MediaList.EMPTY_FIELD_NEXT_PAGE_URL) &&
                position > (mediasAdapter.getItemCount() - 10);
    }

    private void pullNextMedias() {
        isLoading = true;

        getMediasObservable = mssService
                .getMediaList(mediaList.getNextPageUrl())
                .compose(RxUtils.<MediaList>applySchedulers());
        addSubscription(getMediasObservable.subscribe(getMediasObserver));
    }

    private void initAdapters() {
        if (mediaList == null) {
            mediaList = InitialFactory.MediaList.constructInitialInstance();
        }
        mediasAdapter = new MediasAdapter(getActivity(), mediaList.getMedias());
        mediasAdapter.setOnItemClickListener(this);
    }

    private void showLoadingView() {
        Timber.d("Showing loading view");
        visibleLayout = VisibleLayout.LOADING_LAYOUT;
        flipperView.setDisplayedChild(flipperView.indexOfChild(loadingView));
    }

    private void showErrorView() {
        Timber.d("Showing error view");
        visibleLayout = VisibleLayout.ERROR_LAYOUT;
        flipperView.setDisplayedChild(flipperView.indexOfChild(errorView));
    }

    private void showContentView() {
        Timber.d("Showing content view");
        visibleLayout = VisibleLayout.CONTENT_LAYOUT;
        flipperView.setDisplayedChild(flipperView.indexOfChild(contentView));
    }

    private void showNoContentView() {
        Timber.d("Showing no content view");
        visibleLayout = VisibleLayout.NO_CONTENT_LAYOUT;
        flipperView.setDisplayedChild(flipperView.indexOfChild(noContentView));
    }

    @Override
    public void onItemClick(Media media) {
        listener.onItemClicked(media);
    }

    private void resetAdapters() {
        mediaList = InitialFactory.MediaList.constructInitialInstance();
        if (mediasAdapter != null) {
            mediasAdapter.clearMedias();
        }
    }
}
