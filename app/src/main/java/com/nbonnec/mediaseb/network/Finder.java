package com.nbonnec.mediaseb.network;

import com.nbonnec.mediaseb.models.MediaList;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright 2015 Nicolas Bonnec
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
public class Finder {
    private OkHttpClient client;
    private final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36";

    public Finder() {
        client = new OkHttpClient();
        client.networkInterceptors().add(new UserAgentInterceptor(USER_AGENT));
        client.setProxy(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("192.168.95.6", 8080)));
    }

    public List<MediaList> easySearch(String search) throws Exception {
        Document doc;
        Elements titles;
        List<MediaList> list_title = new ArrayList<MediaList>();

        RequestBody formBody = new FormEncodingBuilder()
                .add("SMMaster", "SMMaster|BtRechSimple")
                .add("__EVENTTARGET", "")
                .add("__EVENTARGUMENT", "")
                .add("__VIEWSTATE", "")
                .add("TBRechLibre", search)
                .add("CtrlOpacCompte$TBCompteNom", "")
                .add("CtrlOpacCompte$TBCompteCarte", "")
                .add("TBrechLivreMobile", "")
                .add("CtrLOpacCompteMobile$TBCompteNom", "")
                .add("CtrlOpacCompteMobile$TBCompteCarte", "")
                .add("TBRechTitre", "")
                .add("TBRechAuteur", "")
                .add("TBRechSujet", "")
                .add("TBRechEditeur", "")
                .add("TBRechColl", "")
                .add("__ASYNCPOST", "true")
                .add("BtRechSimple", "")
                .add("tri", "-1")
                .add("ordre", "1")
                .build();
        Request request = new Request.Builder()
                .url("http://www.mediatheque.saintsebastien.fr/")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

        doc = Jsoup.parse(response.body().string());
        titles = doc.select("div[class=\"fll span8\"] > a[title]");
        
        return list_title;
    }
}
