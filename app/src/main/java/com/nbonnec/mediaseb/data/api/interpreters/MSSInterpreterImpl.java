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

import android.util.Log;

import com.nbonnec.mediaseb.BuildConfig;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


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
        final String COVER_LOAD_ELEMENT ="div.couverture input[name=\"ntc_url\"]";

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
            Element coverLoadUrl = e.select(COVER_LOAD_ELEMENT).first();
            Element imageUrl = e.select("img").first();

            Media currentMedia = DefaultFactory.Media.constructDefaultInstance();

            if (title != null) {
                currentMedia.setTitle(title.text());
                currentMedia.setNoticeUrl(endpoints.baseUrl() + title.select("a").attr("href"));
            }
            if (author != null) {
                currentMedia.setAuthor(author.text());
            }
            if (editor != null) {
                currentMedia.setEditor(editor.text());
            }
            if (collection != null) {
                currentMedia.setCollection(collection.text());
            }
            if (year != null) {
                currentMedia.setYear(year.text());
            }
            if (coverLoadUrl != null) {
                currentMedia.setImageUrl(endpoints.imageUrl("/" + coverLoadUrl.val()));
            } else if (imageUrl != null) {
                currentMedia.setImageUrl(endpoints.imageUrl(imageUrl.attr("src")));
            }

            medias.add(currentMedia);
        }

        mediaResults.setMedias(medias);

        nextPageUrl = parseHtml.select(NEXT_URL_ELEMENT).first();
        if (nextPageUrl != null)
            mediaResults.setNextPageUrl(endpoints.nextUrl(nextPageUrl.attr("href")));

        return mediaResults;
    }

    public Media interpretNoticeFromHtml(String html) {
        final String DETAILS_ELEMENT = "div#detail-ntc";
        final String COPY_DETAILS_ELEMENT = "div.detail-ntc-exemplaire tr td";
        final String SUMMURAY_ELEMENT = "p:contains(Résumé)";
        final String TEXT_ELEMENT = "span.aff-public-texte";
        final int TYPE_INDEX = 0;
        final int SECTION_INDEX = 1;
        final int LOCATION_INDEX = 2;
        final int RATING_INDEX = 3;
        final int AVAILABLE_INDEX = 4;
        final int RETURN_DATE_INDEX = 5;

        final Media media = DefaultFactory.Media.constructDefaultInstance();

        Document parseHtml = Jsoup.parse(html);
        Element details = parseHtml.select(DETAILS_ELEMENT).first();

        if (details != null) {
            Element summary = details.select(SUMMURAY_ELEMENT).select(TEXT_ELEMENT).first();
            Element type = details.select(COPY_DETAILS_ELEMENT).get(TYPE_INDEX);
            Element section = details.select(COPY_DETAILS_ELEMENT).get(SECTION_INDEX);
            Element location = details.select(COPY_DETAILS_ELEMENT).get(LOCATION_INDEX);
            Element rating = details.select(COPY_DETAILS_ELEMENT).get(RATING_INDEX);
            Element available = details.select(COPY_DETAILS_ELEMENT).get(AVAILABLE_INDEX);
            Element returnDate = details.select(COPY_DETAILS_ELEMENT).get(RETURN_DATE_INDEX);

            if (summary != null) {
                media.setSummary(summary.text());
            }
            if (type != null) {
                media.setType(type.text());
            }
            if (section != null) {
                media.setSection(section.text());
            }
            if (location != null) {
                media.setLocation(location.text());
            }
            if (rating != null) {
                media.setRating(rating.text());
            }
            if (available != null) {
                media.setAvailable(available.text().equals("En rayon"));
                if (!media.isAvailable() && returnDate != null) {
                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                    try {
                        media.setReturnDate(fmt.parse(returnDate.text()));
                    } catch (ParseException e) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, " Exception : can not parse return date !");
                        }
                    }
                }
            }
        }

        return media;
    }

    @Override
    public String interpretImageUrlFromHtml(String html) {
        Document parseHtml = Jsoup.parse(html);
        Element imageUrl = parseHtml.select("img").first();

        if (imageUrl != null) {
            return endpoints.imageUrl(imageUrl.attr("src"));
        } else {
            return endpoints.baseUrl().concat("/templates/c3rb_alpha_25/html/" +
                    "com_opac/assets/images/icones_support/ico_sup_03.png");
        }
    }
}
