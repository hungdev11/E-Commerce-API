package vn.pph.oms_api.utils;

import java.util.StringJoiner;

public class ProductUtils {
    public String convertProductNameToSlug(String productName) {
        productName = productName.strip().toLowerCase();
        StringJoiner stringJoiner = new StringJoiner("\\s+");
        String[] splitedArr = productName.split(" ");
        for (String word : splitedArr) {
            word = word.replaceAll("[^a-z0-9]", "");
            stringJoiner.add(word);
        }
        return stringJoiner.toString();
    }
}
