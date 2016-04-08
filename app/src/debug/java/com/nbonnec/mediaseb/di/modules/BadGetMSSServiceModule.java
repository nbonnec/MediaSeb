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

import com.nbonnec.mediaseb.data.services.BadGetMSSServiceImpl;
import com.nbonnec.mediaseb.data.services.MSSService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = MediasebModule.class,
        overrides = true
)
public class BadGetMSSServiceModule {
    public static final String TAG = BadGetMSSServiceModule.class.getSimpleName();

    public BadGetMSSServiceModule() {}

    @Provides
    @Singleton
    public MSSService provideMSSService() {
        return new BadGetMSSServiceImpl();
    }
}
