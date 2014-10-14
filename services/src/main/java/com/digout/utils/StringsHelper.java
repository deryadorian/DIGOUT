package com.digout.utils;

public final class StringsHelper {

    public static String addPrefix(String str, final String prefix, final int count) {
        for (int i = 0; i < count; i++) {
            str = prefix + str;
        }
        return str;
    }

    public static String addPrefixSuffixToString(final String str, final String prefix, final String suffix) {
        return prefix + str + suffix;
    }

    public static String appendAll(final Object... objs) {
        final StringBuilder builder = new StringBuilder();
        for (Object s : objs) {
            builder.append(s);
        }
        return builder.toString();
    }

    // todo:make more universally method consider 3 and more digits after point (beta method)
    public static String convertDoubleToBankAmountType(final Double amount) {
        String str = amount.toString();
        if (str.contains(".")) {
            String[] strings = str.split("\\.");
            if (strings[1].length() == 1) {
                strings[1] += "0";
            }
            return strings[0] + strings[1];
        } else {
            str += "00";
            return str;
        }
    }

    private StringsHelper() {
    }

}
