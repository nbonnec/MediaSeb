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
    private String loadingImageUrl;
    private String noticeUrl;
    private Date returnDate;
    private Date loanDate;
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
        loadingImageUrl = in.readString();
        noticeUrl = in.readString();
        returnDate = new Date(in.readLong());
        loanDate = new Date(in.readLong());
        available = in.readByte() != 0;
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
        dest.writeString(loadingImageUrl);
        dest.writeString(noticeUrl);
        dest.writeLong(returnDate.getTime());
        dest.writeLong(loanDate.getTime());
        dest.writeByte((byte) (available ? 1 : 0));
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
                        "Year: %s...",
               title, editor, year
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
        if (needImagePreload()) {
            imageUrl = imageUrl.replace("taille=grande", "taille=moyenne");
        }
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

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setDetails(Media media) {
        summary = media.summary;
        type = media.type;
        section = media.section;
        location = media.location;
        rating = media.rating;
        available = media.available;
        returnDate = media.returnDate;
    }

    public boolean needImagePreload() {
        return imageUrl.contains("couvertureAjax");
    }

    public String getLoadingImageUrl() {
        return loadingImageUrl;
    }

    public void setLoadingImageUrl(String loadingImageUrl) {
        this.loadingImageUrl = loadingImageUrl;
    }
}
