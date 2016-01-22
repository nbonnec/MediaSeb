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

package com.nbonnec.mediaseb.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MediaList implements Parcelable {
    public static final String TAG = MediaList.class.getSimpleName();
    private List<Media> medias;
    private String nextPageUrl;

    public MediaList() {}

    protected MediaList(Parcel in) {
        medias = in.createTypedArrayList(Media.CREATOR);
        nextPageUrl = in.readString();
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(medias);
        dest.writeString(nextPageUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaList> CREATOR = new Creator<MediaList>() {
        @Override
        public MediaList createFromParcel(Parcel in) {
            return new MediaList(in);
        }

        @Override
        public MediaList[] newArray(int size) {
            return new MediaList[size];
        }
    };
}
