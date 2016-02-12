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

public class MSSEndpointsImpl implements MSSEndpoints {
    public static final String TAG = MSSEndpointsImpl.class.getSimpleName();

    private static final String LAYOUT = "ligne";
    private static final int SEARCH_SIZE = 10;

    private static final String API_URL = "http://mediatheque.saintsebastien.fr";
    private static final String NEWS_URL =
            "{api_url}/catalogue/nouveautes?layout={layout}&scrit=&task=rechnouveautes&limit={limit}";
    private static final String SIMPLE_SEARCH_URL =
            "{api_url}/recherche/facettes/{search}/{layout}?limit={limit}";

    @Override
    public String baseUrl() {
        return API_URL;
    }

    @Override
    public String simpleSearchUrl(String search) {
        return Phrase.from(SIMPLE_SEARCH_URL)
                .put("api_url", API_URL)
                .put("search", search)
                .put("layout", LAYOUT)
                .put("limit", String.valueOf(SEARCH_SIZE))
                .format()
                .toString();
    }

    @Override
    public String imageUrl(String href) {
        if (href.contains("/com_opac/assets/images/"))
            return API_URL + href;
        else
            return href;
    }

    @Override
    public String newsUrl() {
        return Phrase.from(NEWS_URL)
                .put("api_url", API_URL)
                .put("layout", LAYOUT)
                .put("limit", SEARCH_SIZE)
                .format()
                .toString();
    }
}
