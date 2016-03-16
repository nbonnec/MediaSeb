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

package com.nbonnec.mediaseb.data.services;

import android.util.Log;

import com.nbonnec.mediaseb.BuildConfig;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.data.api.interpreters.MSSInterpreter;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class MSSServiceImpl implements MSSService {
    public static final String TAG = MSSServiceImpl.class.getSimpleName();

    @Inject MSSEndpoints mssEndpoints;

    @Inject OkHttpClient client;

    @Inject MSSInterpreter interpreter;

    @Override
    public Observable<MediaList> getResults(String search) {
        return getMediaListFromUrl(mssEndpoints.simpleSearchUrl(search));
    }

    @Override
    public Observable<MediaList> getNews() {
        return getMediaListFromUrl(mssEndpoints.newsUrl());
    }

    @Override
    public Observable<MediaList> getMediaListFromUrl(String url) {
        return getHtml(url)
                .map(new Func1<String, MediaList>() {
                    @Override
                    public MediaList call(String s) {
                        return interpreter.interpretMediaResultsFromHtml(s);
                    }
                });
    }

    @Override
    public Observable<Media> getMediaDetailsFromUrl(String url) {
        return getHtml(url)
                .map(new Func1<String, Media>() {
                    @Override
                    public Media call(String s) {
                        return interpreter.interpretNoticeFromHtml(s);
                    }
                });
    }

    private Observable<String> getHtml(final String url) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("Get page : %s", url));
        }

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    subscriber.onNext(response.body().string());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
