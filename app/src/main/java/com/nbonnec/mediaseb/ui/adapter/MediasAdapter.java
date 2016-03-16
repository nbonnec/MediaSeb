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
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.event.MediasLatestPositionEvent;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MediasAdapter extends RecyclerView.Adapter<MediasAdapter.ViewHolder> {
    private static final String TAG = MediasAdapter.class.getSimpleName();

    @Inject
    Bus bus;

    private Context context;
    private List<Media> medias;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title)
        public TextView title;
        @Bind(R.id.author)
        public TextView author;
        @Bind(R.id.icon)
        public ImageView icon;

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
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Media media = medias.get(position);

        bus.post(new MediasLatestPositionEvent(position));

        holder.title.setText(media.getTitle());
        holder.author.setText(media.getAuthor());
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });

        Picasso.with(context).load(media.getImageUrl())
                .into(holder.icon);
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

    public void clearMedias() {
        int size = medias.size();
        medias.clear();
        notifyItemRangeRemoved(0, size);
    }
}
