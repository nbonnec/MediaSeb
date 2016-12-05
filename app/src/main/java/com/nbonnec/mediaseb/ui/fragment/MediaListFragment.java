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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.Rx.RxUtils;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.data.factories.InitialFactory;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.di.modules.ApiModule;
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

    @Inject
    RxUtils rxUtils;

    @Bind(R.id.media_list_flipper_view)
    ViewFlipper flipperView;

    @Bind(R.id.media_list_recycler_view)
    RecyclerView recyclerView;

    @Bind(R.id.media_list_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

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

    public interface OnClickedListener {
        void onItemClicked(View view, Media media);
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
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.cast_grid_view_columns)));
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage(savedPage);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);

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

        /* when returning to activity, we want to throw MediasLatestPositionEvent if needed */
        isLoading = false;
        mediasAdapter.notifyDataSetChanged();

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

        if (!isLoading && !pageLoaded) {
            showLoadingView();
            loadPage(savedPage);
        } else {
            getContextBack();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

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
    public void onClickLoadPage() {
        showLoadingView();
        loadPage(page);
    }

    /**
     * Load a new page.
     *
     * @param page page to load (URL).
     *
     * TODO refactor. Maybe use the bundle to change the arg of the fragment.
     */
    public void loadPage(String page) {
        savedPage = page;
        isLoading = true;

        getMediasObservable = mssService
                .getMediaList(savedPage)
                .compose(rxUtils.<MediaList>applySchedulers());
        addSubscription(getMediasObservable.subscribe(new Observer<MediaList>() {
            @Override
            public void onCompleted() {
                getMediasObservable = null;
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                getMediasObservable = null;
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                if (mediasAdapter.getMedias().isEmpty()) {
                    showErrorView();
                } else {
                    Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNext(MediaList nextList) {
                mediasAdapter.setMedias(nextList.getMedias());
                mediaList.setMedias(mediasAdapter.getMedias());
                mediaList.setNextPageUrl(nextList.getNextPageUrl());

                pageLoaded = true;

                if (mediaList.getMedias().isEmpty()) {
                    showNoContentView();
                } else {
                    showContentView();
                }
            }
        }));
    }

    /**
     * Load a next page.
     *
     * @param page page to load (URL).
     */
    public void loadNextPage(String page) {
        isLoading = true;

        getMediasObservable = mssService
                .getMediaList(page)
                .compose(rxUtils.<MediaList>applySchedulers());
        addSubscription(getMediasObservable.subscribe(new Observer<MediaList>() {
            @Override
            public void onCompleted() {
                getMediasObservable = null;
                isLoading = false;
            }

            @Override
            public void onError(Throwable e) {
                if (mediasAdapter.getMedias().isEmpty()) {
                    showErrorView();
                } else {
                    Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNext(MediaList nextList) {
                mediasAdapter.addMedias(nextList.getMedias());
                mediaList.setMedias(mediasAdapter.getMedias());
                mediaList.setNextPageUrl(nextList.getNextPageUrl());

                pageLoaded = true;

                if (mediaList.getMedias().isEmpty()) {
                    showNoContentView();
                } else {
                    showContentView();
                }
            }
        }));
    }

    /**
     * In order for pullNextMedias to work correctly when we come from another activity (search
     * for example), we need ask the fisrt page.
     */
    private void getContextBack() {
        Timber.d("Getting context back");
        addSubscription(mssService.getHtml(savedPage).compose(rxUtils.<String>applySchedulers())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("Can not load page context !");
                    }

                    @Override
                    public void onNext(String s) {

                    }
                }));
    }

    private boolean canPullNextMedias(int position) {
        return !isLoading && mediaList != null &&
                !mediaList.getNextPageUrl().equals(DefaultFactory.MediaList.EMPTY_FIELD_NEXT_PAGE_URL) &&
                position > (mediasAdapter.getItemCount() - ApiModule.PULL_TOLERANCE);
    }

    private void pullNextMedias() {
        isLoading = true;
        loadNextPage(mediaList.getNextPageUrl());
    }

    private void initAdapters() {
        if (mediaList == null) {
            mediaList = InitialFactory.MediaList.constructInitialInstance();
        }
        mediasAdapter = new MediasAdapter(getActivity(), mediaList.getMedias());
        mediasAdapter.setOnItemClickListener(this);
    }

    private void showLoadingView() {
        Timber.d("Showing loading view '%s'", this.toString());
        visibleLayout = VisibleLayout.LOADING_LAYOUT;
        flipperView.setDisplayedChild(flipperView.indexOfChild(loadingView));
    }

    private void showErrorView() {
        Timber.d("Showing error view '%s'", this.toString());
        visibleLayout = VisibleLayout.ERROR_LAYOUT;
        flipperView.setDisplayedChild(flipperView.indexOfChild(errorView));
    }

    private void showContentView() {
        Timber.d("Showing content view '%s'", this.toString());
        visibleLayout = VisibleLayout.CONTENT_LAYOUT;
        flipperView.setDisplayedChild(flipperView.indexOfChild(contentView));
    }

    private void showNoContentView() {
        Timber.d("Showing no content view '%s'", this.toString());
        visibleLayout = VisibleLayout.NO_CONTENT_LAYOUT;
        flipperView.setDisplayedChild(flipperView.indexOfChild(noContentView));
    }

    @Override
    public void onItemClick(View itemView, Media media) {
        listener.onItemClicked(itemView, media);
    }
}
