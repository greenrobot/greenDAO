package de.greenrobot.daotest.customtype;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.dao.converter.PropertyConverter;

// demo converter
public class StringListConverter implements PropertyConverter<List<String>, byte[]> {
    @Override
    public List<String> convertToEntityProperty(final byte[] bytes) {
        if (bytes == null)
            return null;

        String s = new String(bytes);
        return new ArrayList<>(Arrays.asList(s.split(" ")));
    }

    @Override
    public byte[] convertToDatabaseValue(final List<String> list) {
        if (list == null || list.isEmpty())
            return null;

        String s = TextUtils.join(" ", list);
        return s.getBytes();
    }
}
