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

package com.nbonnec.mediaseb.data.api.interpreters;

import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;

public final class MSSInterpreterImpl implements MSSInterpreter {
    public static final String TAG = MSSInterpreterImpl.class.getSimpleName();

    MSSEndpoints endpoints;

    public MSSInterpreterImpl(MSSEndpoints mssEndpoints) {
        this.endpoints = mssEndpoints;
    }

    @Override
    public MediaList interpretMediaResultsFromHtml(String html) {
        final String LINE_ELEMENT = "div.result-ntc.media";
        final String TITLE_ELEMENT = "span.titre_complet";
        final String AUTHOR_ELEMENT = "span.auteur";
        final String EDITOR_ELEMENT = "span.editeur";
        final String COLLECTION_ELEMENT = "span.collection";
        final String YEAR_ELEMENT = "span.date_edi";
        final String NEXT_URL_ELEMENT = "a[title=Page:Suivant]";

        final MediaList mediaResults = DefaultFactory.MediaList.constructDefaultInstance();
        List<Media> medias = new ArrayList<>();
        Element nextPageUrl;

        Document parseHtml = Jsoup.parse(html);
        Elements lines = parseHtml.select(LINE_ELEMENT);

        for (Element e : lines) {
            Element title = e.select(TITLE_ELEMENT).first();
            Element author = e.select(AUTHOR_ELEMENT).first();
            Element editor = e.select(EDITOR_ELEMENT).first();
            Element year = e.select(YEAR_ELEMENT).first();
            Element collection = e.select(COLLECTION_ELEMENT).first();
            Element imageUrl = e.select("img").first();

            Media currentMedia = DefaultFactory.Media.constructDefaultInstance();

            if (title != null)
                currentMedia.setTitle(title.text());
            if (author != null)
                currentMedia.setAuthor(author.text());
            if (editor != null)
                currentMedia.setEditor(editor.text());
            if (collection != null)
                currentMedia.setCollection(collection.text());
            if (year != null)
                currentMedia.setYear(year.text());
            if (imageUrl != null)
                currentMedia.setImageUrl(endpoints.imageUrl(imageUrl.attr("src")));

            medias.add(currentMedia);
        }

        mediaResults.setMedias(medias);

        nextPageUrl = parseHtml.select(NEXT_URL_ELEMENT).first();
        if (nextPageUrl != null)
            mediaResults.setNextPageUrl(endpoints.nextUrl(nextPageUrl.attr("href")));

        return mediaResults;
    }
}
