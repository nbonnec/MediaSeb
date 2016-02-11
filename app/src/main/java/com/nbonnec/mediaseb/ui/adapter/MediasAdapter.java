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

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.models.Media;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MediasAdapter extends RecyclerView.Adapter<MediasAdapter.ViewHolder> {
    private static final String LOG_TAG = MediasAdapter.class.getSimpleName();

    private Context context;
    private List<Media> medias;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title)
        public TextView title;
        @Bind(R.id.year)
        public TextView year;
        @Bind(R.id.icon)
        public ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public MediasAdapter(Context context, List<Media> medias) {
        this.context = context;
        this.medias = medias;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_results, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Media media = medias.get(position);

        holder.title.setText(media.getTitle());
        holder.year.setText(media.getYear());

        Picasso.with(context)
                .load(media.getImageUrl())
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
}
