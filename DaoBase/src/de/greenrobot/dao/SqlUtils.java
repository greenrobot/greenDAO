package de.greenrobot.dao;

public class SqlUtils {

    public static void appendCommaSeparated(StringBuilder builder, String valuePrefix, String[] values) {
        int length = values.length;
        for (int i = 0; i < length; i++) {
            builder.append(valuePrefix).append(values[i]);
            if (i < length - 1) {
                builder.append(',');
            }
        }
    }

    public static void appendCommaSeparatedEqPlaceholder(StringBuilder builder, String valuePrefix, String[] values) {
        int length = values.length;
        for (int i = 0; i < length; i++) {
            builder.append(valuePrefix).append(values[i]).append("=?");
            if (i < length - 1) {
                builder.append(',');
            }
        }
    }

    public static void apppendPlaceholders(StringBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            if (i < count - 1) {
                builder.append("?,");
            } else {
                builder.append('?');
            }
        }
    }

    public static String createSqlForInsert(String insertInto, String tablename, String[] columns) {
        StringBuilder builder = new StringBuilder(insertInto);
        builder.append(tablename).append(" (");
        appendCommaSeparated(builder, "", columns);
        builder.append(") VALUES (");
        apppendPlaceholders(builder, columns.length);
        builder.append(')');
        return builder.toString();
    }

    public static void appendColumnsEqualPlaceholders(StringBuilder builder, String[] pks) {
        for (int i = 0; i < pks.length; i++) {
            builder.append(pks[i]).append("=?");
            if (i < pks.length - 1) {
                builder.append(',');
            }
        }
    }

}
