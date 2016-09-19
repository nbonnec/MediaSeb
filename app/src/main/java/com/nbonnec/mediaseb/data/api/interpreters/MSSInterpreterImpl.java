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
import com.nbonnec.mediaseb.models.MediaStatus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class MSSInterpreterImpl implements MSSInterpreter {

    MSSEndpoints endpoints;

    public MSSInterpreterImpl(MSSEndpoints mssEndpoints) {
        this.endpoints = mssEndpoints;
    }

    @Override
    public MediaList interpretMediaResultsFromHtml(String html) {
        final String LINE_ELEMENT = "div.result-ntc.media";
        final String TITLE_ELEMENT = "span.titre_complet a";
        final String AUTHOR_ELEMENT = "span.auteur a";
        final String EDITOR_ELEMENT = "span.editeur a";
        final String COLLECTION_ELEMENT = "span.collection a";
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
                /* Delete brackets. */
                String t = title.text().replaceAll("\\[(.*)\\]", "$1");
                currentMedia.setTitle(t);
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
                currentMedia.setYear(year.text().replaceAll("[^\\d]", ""));
            }
            if (coverLoadUrl != null) {
                currentMedia.setImageUrl(endpoints.imageUrl("/" + coverLoadUrl.val()));
                if (imageUrl != null) {
                    currentMedia.setLoadingImageUrl(endpoints.imageUrl(imageUrl.attr("src")));
                }
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
        final int SITUATION_INDEX = 4;
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
            Element situation = details.select(COPY_DETAILS_ELEMENT).get(SITUATION_INDEX);
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
            if (situation != null) {
                media.setStatus(getStatus(situation.text()));
                if (media.getStatus() == MediaStatus.LOANED && returnDate != null) {
                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                    try {
                        media.setReturnDate(fmt.parse(returnDate.text()));
                    } catch (ParseException e) {
                        Timber.d("Exception : can not parse return date (%s)! %s ", returnDate.text(), e.toString());
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

    @Override
    public List<Media> interpretLoansFromHtml(String html) {
        final String LOANS_LIST = "div#div_result tr[class]";
        final String LOAN_ELEMENT = "td";
        final int EXTEND_LOAN_INDEX = 0;
        final int TYPE_INDEX = 1;
        final int TITLE_INDEX = 2;
        final int BOOKLET_INDEX = 3;
        final int AUTHOR_INDEX = 4;
        final int RETURN_DATE_INDEX = 5;
        final int LOAN_DATE_INDEX = 6;

        Document parseHtml = Jsoup.parse(html);
        Elements loans = parseHtml.select(LOANS_LIST);

        List<Media> medias = new ArrayList<>();

        for (Element loan : loans) {
            Media currentMedia = DefaultFactory.Media.constructDefaultInstance();

            Element title = loan.select(LOAN_ELEMENT).get(TITLE_INDEX);
            Element author = loan.select(LOAN_ELEMENT).get(AUTHOR_INDEX);
            Element returnDate = loan.select(LOAN_ELEMENT).get(RETURN_DATE_INDEX);
            Element loanDate = loan.select(LOAN_ELEMENT).get(LOAN_DATE_INDEX);

            if (title != null) {
                currentMedia.setTitle(title.text());
            }
            if (author != null) {
                currentMedia.setAuthor(author.text());
            }
            if (returnDate != null) {
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                try {
                    currentMedia.setReturnDate(fmt.parse(returnDate.text()));
                } catch (ParseException e) {
                    Timber.d("Exception : can not parse return date !");
                }
            }
            if (loanDate != null) {
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                try {
                    currentMedia.setLoanDate(fmt.parse(loanDate.text()));
                } catch (ParseException e) {
                    Timber.d("Exception : can not parse loan date !");
                }
            }
            medias.add(currentMedia);
        }

        return medias;
    }

    /**
     * Get the status of the media.
     * @param situation from html.
     * @return enum.
     */
    private MediaStatus getStatus(String situation) {
        MediaStatus mediaStatus;

        switch (situation) {
            case "En rayon":
                mediaStatus = MediaStatus.AVAILABLE;
                break;
            case "Réservé":
                mediaStatus = MediaStatus.RESERVED;
                break;
            case "Sorti":
                mediaStatus = MediaStatus.LOANED;
                break;
            default:
                mediaStatus = MediaStatus.NONE;
        }

        return mediaStatus;
    }

    @Override
    public Hashtable<String, String> interpretTokenFromHtml(String html) {
        final String INPUT_ELEMENT = "form#form-login102 input[type=hidden]";

        Document parseHtml = Jsoup.parse(html);
        Elements inputs = parseHtml.select(INPUT_ELEMENT);

        Hashtable<String, String> table = new Hashtable<>();

        for (Element e : inputs) {
            table.put(e.attr("name"), e.attr("value"));
        }

        return table;
    }

    @Override
    public boolean interpretLoginFromHtml(String html) {
        final String ERROR_ELEMENT = "div.alert.alert-danger";

        Document parseHtml = Jsoup.parse(html);
        Elements errors = parseHtml.select(ERROR_ELEMENT);

        for (Element e : errors) {
            if (e.text().contains("Erreur : Un mot de passe vide n'est pas autorisé") ||
                    e.text().contains("Erreur : Veuillez d'abord vous identifier")) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String interpretAccountFromHtml(String html) {
        final String DATE_ELEMENT = "tr:contains(date) td:eq(3)";

        Document parseHtml = Jsoup.parse(html);
        Element date = parseHtml.select(DATE_ELEMENT).first();

        if(date != null) {
            // Remove &nbsp;
            return date.text().replaceAll("\\u00a0", "");
        } else {
            return "";
        }
    }
}
