package com.android.example.github.db;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;


public class GithubTypeConverters {
    @TypeConverter
    @Nullable
    public List<Integer> stringToIntList(@Nullable String data) {
        List<Integer> result = new ArrayList<>();
        if (data == null || "".equals(data)) {
            return result;
        }
        for (String elem : data.split(",")) {
            if (elem != null && !"".equals(elem)) {
                result.add(Integer.valueOf(elem));
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
