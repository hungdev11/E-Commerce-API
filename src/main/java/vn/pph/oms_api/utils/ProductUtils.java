package vn.pph.oms_api.utils;

import java.util.StringJoiner;

public class ProductUtils {
    public static String convertProductNameToSlug(String productName) {
        String[] splitedArr = productName.strip().toLowerCase().split("\\s+");
        StringJoiner stringJoiner = new StringJoiner("-");
        for (String word : splitedArr) {
            stringJoiner.add(word.replaceAll("[^a-z0-9]", ""));
        }
        return stringJoiner.toString();
    }
}
