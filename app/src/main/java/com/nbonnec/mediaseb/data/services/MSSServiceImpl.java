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

import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.data.api.interpreters.MSSInterpreter;
import com.nbonnec.mediaseb.models.Account;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Hashtable;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

public class MSSServiceImpl implements MSSService {
    public static final String TAG = MSSServiceImpl.class.getSimpleName();

    @Inject
    MSSEndpoints mssEndpoints;

    @Inject
    OkHttpClient client;

    @Inject
    MSSInterpreter interpreter;

    @Override
    public Observable<MediaList> getResults(String search) {
        return getMediaList(mssEndpoints.simpleSearchUrl(search));
    }

    @Override
    public Observable<MediaList> getNews() {
        return getMediaList(mssEndpoints.newsUrl());
    }

    @Override
    public Observable<MediaList> getMediaList(String url) {
        return getHtml(url)
                .map(new Func1<String, MediaList>() {
                    @Override
                    public MediaList call(String s) {
                        return interpreter.interpretMediaResultsFromHtml(s);
                    }
                });
    }

    @Override
    public Observable<Media> getMediaDetails(String url) {
        return getHtml(url)
                .map(new Func1<String, Media>() {
                    @Override
                    public Media call(String s) {
                        return interpreter.interpretNoticeFromHtml(s);
                    }
                });
    }

    @Override
    public Observable<String> getMediaLoadedImageUrl(String url) {
        return getHtml(url)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return interpreter.interpretImageUrlFromHtml(s);
                    }
                });
    }

    @Override
    public Observable<Account> getAccountDetails() {
        return getHtml(mssEndpoints.accountUrl())
                .map(new Func1<String, Account>() {
                    @Override
                    public Account call(String s) {
                        return interpreter.interpretAccountFromHtml(s);
                    }
                });
    }

    @Override
    public Observable<String> getHtml(final String url) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Timber.d("Get page : %s", url);

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    subscriber.onNext(response.body().string());
                    response.body().close();
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Boolean> login(final String name, final String cardNumber) {
        return getHtml(mssEndpoints.loginUrl())
                .map(new Func1<String, Hashtable<String, String>>() {
                    @Override
                    public Hashtable<String, String> call(String s) {
                        return interpreter.interpretTokenFromHtml(s);
                    }
                })
                .flatMap(new Func1<Hashtable<String, String>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Hashtable<String, String> table) {
                        return loginWithToken(name, cardNumber, table);
                    }
                });
    }

    @Override
    public Observable<Boolean> logout() {
        return getHtml(mssEndpoints.loginUrl())
                .map(new Func1<String, Hashtable<String, String>>() {
                    @Override
                    public Hashtable<String, String> call(String s) {
                        return interpreter.interpretTokenFromHtml(s);
                    }
                })
                .flatMap(new Func1<Hashtable<String, String>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Hashtable<String, String> table) {
                        return logoutWithToken(table);
                    }
                });
    }

    /**
     * Try to login.
     *
     * @param name       name of the user.
     * @param cardNumber card number of the user.
     * @param inputs     HTML inputs to post (including token).
     * @return true if login succeeded.
     */
    private Observable<Boolean> loginWithToken(final String name, final String cardNumber, final Hashtable<String, String> inputs) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                Timber.d("Logging in user '%s', card '%s'", name, cardNumber);

                /*
                 * Early return.
                 */
                if (inputs.get("task").equals("logout")) {
                    Timber.d("Already logged in!");
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                    return;
                }

                FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

                formEncodingBuilder.add("nom", name).add("carte", cardNumber);

                for (Hashtable.Entry<String, String> entry : inputs.entrySet()) {
                    formEncodingBuilder.add(entry.getKey(), entry.getValue());

                    Timber.d("name : '%s', value : '%s'", entry.getKey(), entry.getValue());
                }

                RequestBody formBody = formEncodingBuilder.build();


                Request request = new Request.Builder()
                        .url(mssEndpoints.loginUrl())
                        .post(formBody)
                        .build();
                try {
                    Timber.d("Request login !");
                    Response response = client.newCall(request).execute();
                    boolean loginSuccess = interpreter.interpretLoginFromHtml(response.body().string());
                    response.body().close();

                    Timber.d("Finish request !");
                    if (response.isSuccessful() && loginSuccess) {
                        Timber.d("User '%s' was successfully logged in", name);
                        subscriber.onNext(true);
                    } else {
                        Timber.e("Failed to log in user '%s'", name);
                        subscriber.onNext(false);
                    }
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
    /**
     * Try to logout.
     *
     * @param inputs     HTML inputs to post (including token).
     * @return true if logout succeeded.
     */
    private Observable<Boolean> logoutWithToken(final Hashtable<String, String> inputs) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                /*
                 * Early return.
                 */
                if (inputs.get("task").equals("login")) {
                    Timber.d("Already logged out!");
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                    return;
                }

                FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

                for (Hashtable.Entry<String, String> entry : inputs.entrySet()) {
                    formEncodingBuilder.add(entry.getKey(), entry.getValue());

                    Timber.d("name : '%s', value : '%s'", entry.getKey(), entry.getValue());
                }

                RequestBody formBody = formEncodingBuilder.build();

                Request request = new Request.Builder()
                        .url(mssEndpoints.loginUrl())
                        .post(formBody)
                        .build();

                try {
                    Timber.d("Request logout !");
                    Response response = client.newCall(request).execute();
                    response.body().close();

                    Timber.d("Finish request !");
                    if (response.isSuccessful()) {
                        Timber.d("User was successfully logged out.");
                        subscriber.onNext(true);
                    } else {
                        Timber.e("Failed to log out.");
                        subscriber.onNext(false);
                    }
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}