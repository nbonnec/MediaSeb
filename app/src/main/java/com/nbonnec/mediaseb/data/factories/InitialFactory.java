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

package com.nbonnec.mediaseb.data.factories;

import com.nbonnec.mediaseb.models.Media;

import java.util.ArrayList;

public class InitialFactory {
    public static final String TAG = InitialFactory.class.getSimpleName();

    private InitialFactory() {
        throw new AssertionError(TAG + ": Cannot be initialized.");
    }

    public static final class MediaList {
        public static final String TAG = MediaList.class.getSimpleName();

        private MediaList() {
            throw new AssertionError(TAG + ": Cannot be initialized.");
        }

        public static com.nbonnec.mediaseb.models.MediaList constructInitialInstance () {
            final com.nbonnec.mediaseb.models.MediaList mediaList = DefaultFactory.MediaList.constructDefaultInstance();

            mediaList.setMedias(new ArrayList<Media>());

            return mediaList;
        }
    }
}
