package de.greenrobot.daotest.customtype;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.converter.PropertyConverter;

// demo converter
public class CustomEnumListConverter implements PropertyConverter<List<CustomEnum>, byte[]> {
    @Override
    public List<CustomEnum> convertToEntityProperty(final byte[] bytes) {
        if (bytes == null)
            return null;

        final String[] strings = new String(bytes).split(";");

        final ArrayList<CustomEnum> result = new ArrayList<>();
        for (String s : strings) {
            result.add(CustomEnum.valueOf(s));
        }
        return result;
    }

    @Override
    public byte[] convertToDatabaseValue(final List<CustomEnum> list) {
        String s = TextUtils.join(";", list);
        return s.getBytes();
    }
}
