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

package com.android.example.github.db;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class GithubTypeConverters {
    @TypeConverter
    @Nullable
    public List<Integer> stringToIntList(@Nullable String data) {
        List<Integer> result = new ArrayList<>();
        if (data == null || "".equals(data)) {
            return result;
        }
        for (String str : data.split(",")) {
            if (str != null && !"".equals(str)) {
                try {
                    result.add(Integer.valueOf(str));
                } catch(NumberFormatException ex) {
                    Timber.e(ex, "Cannot convert " + str + " to number");
                }
            }
        }
        return result;
    }

    @TypeConverter
    @Nullable
    public String intListToString(@Nullable List<Integer> ints) {
        if (ints == null || ints.isEmpty()) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ints.size(); i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(ints.get(i));
        }
        return sb.toString();
    }
}
