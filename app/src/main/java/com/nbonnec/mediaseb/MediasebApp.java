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

package com.nbonnec.mediaseb;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.nbonnec.mediaseb.di.modules.Modules;
import com.nbonnec.mediaseb.log.CrashlyticsTree;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class MediasebApp extends Application {
    protected ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashlyticsCore core = new CrashlyticsCore.Builder()
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        // Error logs are sent to the cloud with Crashlytics
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        else {
            Timber.plant(new CrashlyticsTree());
        }

        objectGraph = ObjectGraph.create(Modules.get(this));
    }

    public void setObjectGraph(Object... modules) {
        objectGraph = ObjectGraph.create(modules);
    }

    public static MediasebApp get(Context context) {
        return (MediasebApp) context.getApplicationContext();
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }
}
