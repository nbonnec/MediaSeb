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

import java.util.Date;

public class Media implements Parcelable{
    public static final String TAG = Media.class.getSimpleName();
    private String title;
    private String author;
    private String editor;
    private String collection;
    private String year;
    private String summary;
    private String type;
    private String section;
    private String location;
    private String rating;
    private String imageUrl;
    private String noticeUrl;
    private Date returnDate;
    private boolean available;

    public Media() {}

    protected Media(Parcel in) {
        title = in.readString();
        author = in.readString();
        editor = in.readString();
        collection = in.readString();
        year = in.readString();
        summary = in.readString();
        type = in.readString();
        section = in.readString();
        location = in.readString();
        rating = in.readString();
        imageUrl = in.readString();
        noticeUrl = in.readString();
        available = in.readByte() != 0;
        returnDate = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(editor);
        dest.writeString(collection);
        dest.writeString(year);
        dest.writeString(summary);
        dest.writeString(type);
        dest.writeString(section);
        dest.writeString(location);
        dest.writeString(rating);
        dest.writeString(imageUrl);
        dest.writeString(noticeUrl);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeLong(returnDate.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public String toString() {
        return String.format(
                TAG + ": Title: %s... \n" +
                        "Editor: %s... \n" +
                        "Collection: %s...\n" +
                        "Year: %s...",
               title, editor, collection, year
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public void setNoticeUrl(String noticeUrl) {
        this.noticeUrl = noticeUrl;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void getDetails(Media media) {
        summary = media.summary;
        available = media.available;
    }
}
