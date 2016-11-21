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
import com.nbonnec.mediaseb.models.Account;
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

import javax.inject.Inject;

import timber.log.Timber;

public class MSSInterpreterImpl implements MSSInterpreter {

    private MSSEndpoints endpoints;

    @Inject
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
            Element element;

            Media currentMedia = DefaultFactory.Media.constructDefaultInstance();

            element = e.select(TITLE_ELEMENT).first();
            if (element != null) {
                String t = cleanTitle(element.text());
                currentMedia.setTitle(t);
                currentMedia.setNoticeUrl(endpoints.baseUrl() + element.select("a").attr("href"));
            }

            element = e.select(AUTHOR_ELEMENT).first();
            if (element != null) {
                currentMedia.setAuthor(element.text());
            }

            element = e.select(EDITOR_ELEMENT).first();
            if (element != null) {
                currentMedia.setEditor(element.text());
            }

            element = e.select(COLLECTION_ELEMENT).first();
            if (element != null) {
                currentMedia.setCollection(element.text());
            }

            element = e.select(YEAR_ELEMENT).first();
            if (element != null) {
                currentMedia.setYear(element.text().replaceAll("[^\\d]", ""));
            }

            element = e.select(COVER_LOAD_ELEMENT).first();
            Element imageUrl = e.select("img").first();
            if (element != null) {
                currentMedia.setImageUrl(endpoints.imageUrl("/" + element.val()));
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
            Element element;

            element = details.select(SUMMURAY_ELEMENT).select(TEXT_ELEMENT).first();
            if (element != null) {
                media.setSummary(element.text());
            }

            element = details.select(COPY_DETAILS_ELEMENT).get(TYPE_INDEX);
            if (element != null) {
                media.setType(element.text());
            }

            element = details.select(COPY_DETAILS_ELEMENT).get(SECTION_INDEX);
            if (element != null) {
                media.setSection(element.text());
            }

            element = details.select(COPY_DETAILS_ELEMENT).get(LOCATION_INDEX);
            if (element != null) {
                media.setLocation(element.text());
            }

            element = details.select(COPY_DETAILS_ELEMENT).get(RATING_INDEX);
            if (element != null) {
                media.setRating(element.text());
            }

            element = details.select(COPY_DETAILS_ELEMENT).get(SITUATION_INDEX);
            if (element != null) {
                media.setStatus(getStatus(element.text()));
                Element returnDate = details.select(COPY_DETAILS_ELEMENT).get(RETURN_DATE_INDEX);
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
            Element element;

            element = loan.select(LOAN_ELEMENT).get(TITLE_INDEX);
            if (element != null) {
                currentMedia.setTitle(cleanTitle(element.text()));
            }

            element = loan.select(LOAN_ELEMENT).get(AUTHOR_INDEX);
            if (element != null) {
                currentMedia.setAuthor(element.text());
            }

            element = loan.select(LOAN_ELEMENT).get(RETURN_DATE_INDEX);
            if (element != null) {
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                try {
                    currentMedia.setReturnDate(fmt.parse(element.text()));
                } catch (ParseException e) {
                    Timber.d("Exception : can not parse return date !");
                }
            }

            element = loan.select(LOAN_ELEMENT).get(LOAN_DATE_INDEX);
            if (element != null) {
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                try {
                    currentMedia.setLoanDate(fmt.parse(element.text()));
                } catch (ParseException e) {
                    Timber.d("Exception : can not parse loan date !");
                }
            }
            medias.add(currentMedia);
        }

        return medias;
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
    public Account interpretAccountFromHtml(String html) {
        final String ACCOUNT_DIV = "div.pagect.opac.moncompte";
        final String SURNAME_ELEMENT = "td:matches(Nom\\b) + td";
        final String NAME_ELEMENT = "td:matches(Prénom\\b) + td";
        final String ADDRESS_ELEMENT = "td:matches(Adresse 1) + td";
        final String POSTAL_CODE_ELEMENT = "td:matches(Code postal) + td";
        final String CITY_ELEMENT = "td:matches(Ville\\b) + td";
        final String PHONE_NUMBER_ELEMENT = "td:matches(Tél) + td";
        final String BIRTH_DATE_ELEMENT = "td:matches(Né\\(e\\) le) + td";
        final String MAIL_ELEMENT = "td:matches(Email\\b) + td";
        final String LOANS_NUMBER_ELEMENT = "td:matches(Nombre total de prêts) + td";
        final String RESARVATION_NUMBER_ELEMENT = "td:matches(Réservations en attente) + td";
        final String AVAILABLE_RESERVATION_ELEMENT = "td:matches(Réservations disponibles) + td";
        final String CARD_NUMBER_ELEMENT = "td:matches(Numéro de carte) + td";
        final String RENEW_DATE_ELEMENT = "td:matches(Date de renouvellement) + td";
        final String FARE_ELEMENT = "td:matches(Tarif) + td";
        final String BALANCE_ELEMENT = "td:matches(Solde) + td";

        Account.Builder account = Account.builder();

        Elements parseAccount = Jsoup.parse(html).select(ACCOUNT_DIV);
        Element element;

        // surname
        element = parseAccount.select(SURNAME_ELEMENT).first();
        if (element != null) {
            account.setSurname(cleanAccountDetails(element.text()));
        }

        // name
        element = parseAccount.select(NAME_ELEMENT).first();
        if (element != null) {
            account.setName(cleanAccountDetails(element.text()));
        }

        // address
        element = parseAccount.select(ADDRESS_ELEMENT).first();
        if (element != null) {
            account.setAddress(cleanAccountDetails(element.text()));
        }

        // postal code
        element = parseAccount.select(POSTAL_CODE_ELEMENT).first();
        if (element != null) {
            account.setPostalCode(cleanAccountDetails(element.text()));
        }

        // city
        element = parseAccount.select(CITY_ELEMENT).first();
        if (element != null) {
            account.setCity(cleanAccountDetails(element.text()));
        }

        // phone number
        element = parseAccount.select(PHONE_NUMBER_ELEMENT).first();
        if (element != null) {
            account.setPhoneNumber(cleanAccountDetails(element.text()));
        }

        // birth date
        element = parseAccount.select(BIRTH_DATE_ELEMENT).first();
        if (element != null) {
            account.setBirthDate(cleanAccountDetails(element.text()));
        }
        // mail
        element = parseAccount.select(MAIL_ELEMENT).first();
        if (element != null) {
            account.setMail(cleanAccountDetails(element.text()));
        }
        // loans number
        element = parseAccount.select(LOANS_NUMBER_ELEMENT).first();
        if (element != null) {
            account.setLoanNumber(cleanAccountDetails(element.text()));
        }
        // reservation number
        element = parseAccount.select(RESARVATION_NUMBER_ELEMENT).first();
        if (element != null) {
            account.setReservation(cleanAccountDetails(element.text()));
        }
        // available reservations
        element = parseAccount.select(AVAILABLE_RESERVATION_ELEMENT).first();
        if (element != null) {
            account.setAvailableReservation(cleanAccountDetails(element.text()));
        }
        // card number
        element = parseAccount.select(CARD_NUMBER_ELEMENT).first();
        if (element != null) {
            account.setCardNumber(cleanAccountDetails(element.text()));
        }
        // fare
        element = parseAccount.select(FARE_ELEMENT).first();
        if (element != null) {
            account.setFare(cleanAccountDetails(element.text()));
        }

        // balance
        element = parseAccount.select(BALANCE_ELEMENT).first();
        if (element != null) {
            account.setBalance(cleanAccountDetails(element.text()));
        }

        // date
        element = parseAccount.select(RENEW_DATE_ELEMENT).first();
        if(element != null) {
            // Remove &nbsp;
            final String renewDate = cleanAccountDetails(element.text());

            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);


            try {
                account.setRenewDate(fmt.parse(renewDate));
            } catch (ParseException e) {
                Timber.d("Exception : can not parse renew date !");
            }
        }

        return account.build();
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

    /** Delete brackets. */
    private String cleanTitle(String s) {
       return s.replaceAll("[\\[\\]{}]", "");
    }

    private String cleanAccountDetails(String s) {
        s = s.replaceAll("\\u00a0", "");
        s = s.replaceAll("^\\s+", "");
        return s;
    }
}
