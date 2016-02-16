package com.nbonnec.mediaseb.data.api.endpoints;
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

public interface MSSEndpoints {

    public String baseUrl();

    public String simpleSearchUrl(String search);

    public String imageUrl(String href);

    public String newsUrl();

    public String nextUrl(String nextUrl);
}
