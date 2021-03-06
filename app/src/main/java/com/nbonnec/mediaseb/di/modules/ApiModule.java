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

package com.nbonnec.mediaseb.di.modules;

import android.app.Application;
import android.util.Log;

import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpointsImpl;
import com.nbonnec.mediaseb.data.api.interpreters.MSSInterpreter;
import com.nbonnec.mediaseb.data.api.interpreters.MSSInterpreterImpl;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.data.services.MSSServiceImpl;
import com.nbonnec.mediaseb.network.UserAgentInterceptor;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class ApiModule {
    public static final String TAG = ApiModule.class.getSimpleName();

    private final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36";

    public static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;
    public static final int PULL_TOLERANCE = 10;

    @Provides
    @Singleton
    public MSSService provideMSSService(MSSServiceImpl mssService) {
        return mssService;
    }

    @Provides
    @Singleton
    public MSSEndpoints provideMSSEndpoints() {
        return new MSSEndpointsImpl();
    }

    @Provides
    @Singleton
    public MSSInterpreter provideMSSInterpreter(MSSEndpoints mssEndpoints) {
        return new MSSInterpreterImpl(mssEndpoints);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    private static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        CookieManager cookieManager = new CookieManager();

        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
        client.networkInterceptors().add(new UserAgentInterceptor(USER_AGENT));

        try {
            File cacheDir = new File(app.getCacheDir(), "http");
            Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
            client.setCache(cache);
        } catch (NullPointerException e) {
            Log.e(TAG, "Unable to initialize OkHttpclient with disk cache", e);
        }

        return client;
    }

}
