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

import com.nbonnec.mediaseb.di.modules.ApiModule;
import com.nbonnec.mediaseb.ui.activity.MainActivity;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                ApiModule.class
        },
        injects = {
                MediasebApp.class,
                MainActivity.class,
                MediaListFragment.class
        }
)
public class MediasebModule {
    private MediasebApp app;

    public MediasebModule(MediasebApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApp() {
        return app;
    }
}