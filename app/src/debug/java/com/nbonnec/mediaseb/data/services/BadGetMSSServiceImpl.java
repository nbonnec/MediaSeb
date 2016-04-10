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

import android.accounts.NetworkErrorException;

import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;

import rx.Observable;

public class BadGetMSSServiceImpl implements MSSService {
    public static final String TAG = BadGetMSSServiceImpl.class.getSimpleName();

    public BadGetMSSServiceImpl() { }

    @Override
    public Observable<MediaList> getMediaList(String url) {
        return Observable.error(new NetworkErrorException(TAG +
        ": getMedialistFromUrl() - Simulated Bad Network Request"));
    }

    @Override
    public Observable<Media> getMediaDetails(String url) {
        return Observable.error(new NetworkErrorException(TAG +
        ": getMediaDetails - Simulated Bad Network Request"));
    }

    @Override
    public Observable<String> getMediaLoadedImage(String url) {
        return Observable.error(new NetworkErrorException(TAG +
        ": getMediaLoadedImageUrl - Simulated Bad Network Request"));
    }

    @Override
    public Observable<MediaList> getResults(String pattern) {
        return Observable.error(new NetworkErrorException(TAG +
        ": getResults - Simulated Bad Network Request"));
    }

    @Override
    public Observable<MediaList> getNews() {
        return Observable.error(new NetworkErrorException(TAG +
        ": getNews - Simulated Bad Network Request"));
    }
}
