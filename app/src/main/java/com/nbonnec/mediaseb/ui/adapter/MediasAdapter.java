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

package com.nbonnec.mediaseb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nbonnec.mediaseb.MediasebApp;
import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.Rx.RxUtils;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.event.MediasLatestPositionEvent;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MediasAdapter extends RecyclerView.Adapter<MediasAdapter.ViewHolder> {
    private static final String TAG = MediasAdapter.class.getSimpleName();

    @Inject
    Bus bus;

    @Inject
    MSSService mssService;

    @Inject
    RxUtils rxUtils;

    private Context context;
    private List<Media> medias;
    private CompositeSubscription subscriptions;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, Media media);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.list_item_image)
        public ImageView icon;
        @Bind(R.id.list_item_title)
        public TextView title;
        @Bind(R.id.list_item_author)
        public TextView author;
        @Bind(R.id.list_item_year)
        public TextView year;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }
    }

    public MediasAdapter(Context context, List<Media> medias) {
        this.context = context;
        this.medias = medias;

        MediasebApp app = MediasebApp.get(context);
        app.inject(this);
        subscriptions = new CompositeSubscription();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_media, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Media media = medias.get(position);

        bus.post(new MediasLatestPositionEvent(position));

        if (!media.getTitle().equals(DefaultFactory.Media.EMPTY_FIELD_TITLE)) {
            holder.title.setText(media.getTitle());
        }
        if (!media.getAuthor().equals(DefaultFactory.Media.EMPTY_FIELD_AUTHOR)) {
            holder.author.setText(media.getAuthor());
        }
        if (!media.getYear().equals(DefaultFactory.Media.EMPTY_FIELD_YEAR)) {
            holder.year.setText(media.getYear());
        }

        // TODO use retro lambda
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.itemView, media);
            }
        });

        if (media.needImagePreload()) {
            if (subscriptions == null) {
                subscriptions = new CompositeSubscription();
            }
            subscriptions.add(mssService
                    .getMediaLoadedImageUrl(media.getImageUrl())
                    .compose(rxUtils.<String>applySchedulers())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            media.setImageUrl(s);
                            Picasso.with(context)
                                    .load(media.getImageUrl())
                                    .noFade()
                                    .into(holder.icon);
                        }
                    })
            );
            Picasso.with(context)
                    .load(media.getLoadingImageUrl())
                    .into(holder.icon);

        } else {
            Picasso.with(context)
                    .load(media.getImageUrl())
                    .into(holder.icon);
        }
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void addMedias(List<Media> medias) {
        int currentSize = this.medias.size();
        int amountInserted = medias.size();

        this.medias.addAll(medias);

        notifyItemRangeInserted(currentSize, amountInserted);
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
        notifyDataSetChanged();
    }

    public void clearMedias() {
        int size = medias.size();
        medias.clear();
        notifyItemRangeRemoved(0, size);
        notifyDataSetChanged();
    }

    public void clearSubscriptions() {
        subscriptions = null;
    }
}
