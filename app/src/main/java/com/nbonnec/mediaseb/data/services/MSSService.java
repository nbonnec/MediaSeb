package com.nbonnec.mediaseb.data.services;/*
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

import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;

import rx.Observable;

public interface MSSService {
    Observable<MediaList> getMediaList(String url);
    Observable<Media> getMediaDetails(String url);
    Observable<String> getMediaLoadedImageUrl(String url);
    Observable<MediaList> getResults(String pattern);
    Observable<MediaList> getNews();
    Observable<Boolean> login(String name, String cardNumber);
    Observable<String> getHtml(final String url);
}
