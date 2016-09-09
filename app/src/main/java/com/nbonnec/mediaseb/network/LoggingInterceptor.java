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

package com.nbonnec.mediaseb.network;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;

import timber.log.Timber;

public class LoggingInterceptor implements Interceptor {
    private CookieManager cookieManager;

    public LoggingInterceptor(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Timber.d("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers());
        for (HttpCookie c : cookieManager.getCookieStore().getCookies()) {
            Timber.d("'%s'", c);
        }

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Timber.d("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers());
        for (HttpCookie c : cookieManager.getCookieStore().getCookies()) {
            Timber.d("'%s'", c);
        }
        return response;
    }
}
