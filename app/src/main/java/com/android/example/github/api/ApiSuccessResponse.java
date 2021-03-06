/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.github.api;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class ApiSuccessResponse<T> extends ApiResponse<T> {
    private Pattern LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"");
    private Pattern PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)");
    private final String NEXT_LINK = "next";

    private T body;
    private Map<String, String> links;

    public ApiSuccessResponse(T body, @Nullable String linkHeader) {
        super();
        this.body = body;
        if (linkHeader == null || "".equals(linkHeader)) {
            this.links = new HashMap<>();
        } else {
            this.links = extractLinks(linkHeader);
        }
    }

    public T getBody() {
        return body;
    }

    @Nullable
    public Integer getNextPage() {
        String next = links.get(NEXT_LINK);
        if (next != null) {
            Matcher matcher = PAGE_PATTERN.matcher(next);
            if (!matcher.find() || matcher.groupCount() != 1) {
                return null;
            } else {
                try {
                    return Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException ex) {
                    Timber.w("cannot parse next page from %s", next);
                    return null;
                }
            }
        } else {
            return  null;
        }
    }

    private Map<String, String> extractLinks(String url) {
        Map<String, String> links = new HashMap <>();
        Matcher matcher = LINK_PATTERN.matcher(url);

        while (matcher.find()) {
            int count = matcher.groupCount();
            if (count == 2) {
                links.put(matcher.group(2), matcher.group(1));
            }
        }
        return links;
    }
}
