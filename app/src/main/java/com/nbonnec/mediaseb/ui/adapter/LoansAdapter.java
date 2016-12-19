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
import android.widget.TextView;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.models.Media;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoansAdapter extends RecyclerView.Adapter<LoansAdapter.ViewHolder> {
    private Context context;
    private List<Media> medias;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.loans_author)
        public TextView author;
        @Bind(R.id.loans_title)
        public TextView title;
        @Bind(R.id.loans_return_date)
        public TextView returnDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public LoansAdapter(Context context, List<Media> medias) {
        this.context = context;
        this.medias = medias;
    }

    @Override
    public LoansAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LoansAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_loans, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Media media = medias.get(position);

        if (!media.getAuthor().equals(DefaultFactory.Media.EMPTY_FIELD_AUTHOR)) {
            holder.author.setText(media.getAuthor());
        }
        if (!media.getTitle().equals(DefaultFactory.Media.EMPTY_FIELD_TITLE)) {
            holder.title.setText(media.getTitle());
        }

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        holder.returnDate.setText(fmt.format(media.getReturnDate()));
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
        notifyDataSetChanged();
    }
}
