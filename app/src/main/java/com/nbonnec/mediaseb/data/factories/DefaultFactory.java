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

import com.nbonnec.mediaseb.R;

import java.util.ArrayList;

public class DefaultFactory {
    public static final String TAG = DefaultFactory.class.getSimpleName();

    private DefaultFactory() {
        throw new AssertionError(TAG + ": Cannot be initialized.");
    }

    public static final class MediaList {
        public static final String TAG = com.nbonnec.mediaseb.models.MediaList.class.getSimpleName();

        public static final String EMPTY_FIELD_NEXT_PAGE_URL = "No Next Page URL";

        private MediaList() {
            throw new AssertionError(TAG + ": Cannot be initialized.");
        }

        public static com.nbonnec.mediaseb.models.MediaList constructDefaultInstance() {
            final com.nbonnec.mediaseb.models.MediaList temporaryInstance = new com.nbonnec.mediaseb.models.MediaList();

            temporaryInstance.setMedias(new ArrayList<com.nbonnec.mediaseb.models.Media>());

            temporaryInstance.setNextPageUrl(EMPTY_FIELD_NEXT_PAGE_URL);

            return temporaryInstance;
        }
    }

    public static final class Media {
        public static final String TAG = com.nbonnec.mediaseb.models.Media.class.getSimpleName();

        public static final String EMPTY_FIELD_TITLE = "No Title";
        public static final String EMPTY_FIELD_AUTHOR = "No Author";
        public static final String EMPTY_FIELD_EDITOR = "No Editor";
        public static final String EMPTY_FIELD_COLLECTION = "No Collection";
        public static final String EMPTY_FIELD_YEAR = "No Year";
        public static final String EMPTY_FIELD_SUMMARY = "No Summary";

        private Media() {
            throw new AssertionError(TAG + ": Cannot be initialized.");
        }

        public static com.nbonnec.mediaseb.models.Media constructDefaultInstance() {
            final com.nbonnec.mediaseb.models.Media temporaryInstance = new com.nbonnec.mediaseb.models.Media();

            temporaryInstance.setTitle(EMPTY_FIELD_TITLE);
            temporaryInstance.setAuthor(EMPTY_FIELD_AUTHOR);
            temporaryInstance.setEditor(EMPTY_FIELD_EDITOR);
            temporaryInstance.setCollection(EMPTY_FIELD_COLLECTION);
            temporaryInstance.setYear(EMPTY_FIELD_YEAR);
            temporaryInstance.setSummary(EMPTY_FIELD_SUMMARY);

            return temporaryInstance;
        }
    }
}
