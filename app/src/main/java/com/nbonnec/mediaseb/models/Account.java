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

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.Date;

@AutoValue
public abstract class Account {
    public abstract String name();
    @Nullable
    public abstract String surname();
    @Nullable
    public abstract String address();
    @Nullable
    public abstract String postalCode();
    @Nullable
    public abstract String city();
    @Nullable
    public abstract String birthDate();
    @Nullable
    public abstract String mail();
    @Nullable
    public abstract String phoneNumber();
    @Nullable
    public abstract String loanNumber();
    @Nullable
    public abstract String reservation();
    @Nullable
    public abstract String availableReservation();
    @Nullable
    public abstract String cardNumber();
    @Nullable
    public abstract String fare();
    @Nullable
    public abstract String balance();
    @Nullable
    public abstract Date renewDate();
    @Nullable

    public static Builder builder() {
        return new AutoValue_Account.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setName(String value);
        public abstract Builder setSurname(String value);
        public abstract Builder setAddress(String value);
        public abstract Builder setPostalCode(String value);
        public abstract Builder setCity(String value);
        public abstract Builder setBirthDate(String value);
        public abstract Builder setMail(String value);
        public abstract Builder setPhoneNumber(String value);
        public abstract Builder setLoanNumber(String value);
        public abstract Builder setReservation(String value);
        public abstract Builder setAvailableReservation(String value);
        public abstract Builder setCardNumber(String value);
        public abstract Builder setFare(String value);
        public abstract Builder setBalance(String value);
        public abstract Builder setRenewDate(Date value);
        public abstract Account build();
    }
}