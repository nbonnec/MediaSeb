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

package com.nbonnec.mediaseb.data.api.endpoints;

import com.squareup.phrase.Phrase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSSEndpointsImpl implements MSSEndpoints {
    public static final String TAG = MSSEndpointsImpl.class.getSimpleName();

    private static final Pattern AUTHORIZED_CHAR = Pattern.compile("[a-zA-Z0-9 ]*");

    private static final String LAYOUT = "ligne";
    private static final int RESULTS_SIZE = 10;
    private static final int SEARCH_STRING_CHAR_MAX = 40;

    private static final String API_URL = "http://mediatheque.saintsebastien.fr";
    private static final String NEWS_URL =
            "{api_url}/catalogue/nouveautes?layout={layout}&scrit=&task=rechnouveautes&limit={limit}";
    private static final String SIMPLE_SEARCH_URL =
            "{api_url}/recherche/facettes/{search}/{layout}?limit={limit}";
    private static final String NEXT_URL = "{api_url}{next_url}";
    private static final String LOAN_URL = "{api_url}/mon-compte/mes-prets?view=Prets&task=ListePrets";
    private static final String ACCOUNT_URL = "{api_url}/mon-compte";
    /* choose an URL fast to load to login quickly */
    private static final String LOGIN_URL = "{api_url}/mon-compte";

    @Override
    public String baseUrl() {
        return API_URL;
    }

    @Override
    public String simpleSearchUrl(String search) {
        StringBuilder s = new StringBuilder(SEARCH_STRING_CHAR_MAX);
        Matcher m = AUTHORIZED_CHAR.matcher(search);

        /* get rid of special character */
        while (m.find())
            s.append(m.group(0));

        search = s.toString().replaceAll("\\s+", "+");

        /* should rarely happen */
        if (search.isEmpty())
            search = "abc";

        return Phrase.from(SIMPLE_SEARCH_URL)
                .put("api_url", API_URL)
                .put("search", search)
                .put("layout", LAYOUT)
                .put("limit", String.valueOf(RESULTS_SIZE))
                .format()
                .toString();
    }

    @Override
    public String imageUrl(String href) {
        if (!href.contains(API_URL) &&
                (href.contains("/com_opac/assets/images/") ||
                        href.contains("couvertureAjax") ||
                        href.contains("cache")))
            return API_URL + href;
        else
            return href;
    }

    @Override
    public String loanUrl() {
        return Phrase.from(LOAN_URL)
                .put("api_url", API_URL)
                .format()
                .toString();
    }

    @Override
    public String newsUrl() {
        return Phrase.from(NEWS_URL)
                .put("api_url", API_URL)
                .put("layout", LAYOUT)
                .put("limit", RESULTS_SIZE)
                .format()
                .toString();
    }

    @Override
    public String nextUrl(String nextUrl) {
        return Phrase.from(NEXT_URL)
                .put("api_url", API_URL)
                .put("next_url", nextUrl)
                .format()
                .toString();
    }

    @Override
    public String loginUrl() {
        return Phrase.from(LOGIN_URL)
                .put("api_url", API_URL)
                .format()
                .toString();
    }

    @Override
    public String accountUrl() {
        return Phrase.from(ACCOUNT_URL)
                .put("api_url", API_URL)
                .format()
                .toString();
    }
}
