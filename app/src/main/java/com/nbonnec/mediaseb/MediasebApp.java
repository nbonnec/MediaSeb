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
import com.nbonnec.mediaseb.di.modules.ContextModule;
import com.nbonnec.mediaseb.di.modules.MediasebModule;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.internal.Modules;
import io.fabric.sdk.android.Fabric;

public class MediasebApp extends Application {
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        buildObjectGraphAndInject();
    }

    public void buildObjectGraphAndInject() {
        buildInitialObjectGraph();
    }

    public void buildInitialObjectGraph() {
        objectGraph = ObjectGraph.create(
                new MediasebModule(this)
        );
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
