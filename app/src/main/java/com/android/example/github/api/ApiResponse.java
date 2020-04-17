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

import java.io.IOException;

import okhttp3.Headers;
import retrofit2.Response;

public class ApiResponse<T> {
    public static <T> ApiErrorResponse<T> create(Throwable error) {
        return new ApiErrorResponse(error.getMessage() == null ? "unknown error" : error.getMessage());
    }

    public  static <T> ApiResponse<T> create(Response<T> response) {
        if (response.isSuccessful()) {
            T body = response.body();
            if (body == null || response.code() == 204) {
                return new ApiEmptyResponse();
            } else {
                Headers linkHeader = response.headers();
                if (linkHeader != null) {
                    return new ApiSuccessResponse(body, linkHeader.get("link"));
                } else {
                    return new ApiSuccessResponse(body, null);
                }
            }
        } else {
            try {
                String errorMsg = null;
                if (response.errorBody() != null) {
                    String msg = response.errorBody().string();
                    if (msg == null || msg.isEmpty()) {
                        errorMsg = response.message();
                    } else {
                        errorMsg = msg;
                    }
                }
                return new ApiErrorResponse(errorMsg == null ? "unknown error" : errorMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
