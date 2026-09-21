package algorithms;

import java.util.ArrayList;
import java.util.List;

import model.Phone;

public class LinearSearch {
    public static List<Phone> linearSearch(List<Phone> lists, String key) {
        List<Phone> results = new ArrayList<>();
        if (lists == null || lists.isEmpty() || key == null)
            return results;

        String target = key.trim().toLowerCase();
        if (target.isEmpty())
            return results;

        for (Phone phone : lists) {
            boolean matchName = phone.getProductName() != null
                    && phone.getProductName().toLowerCase().contains(target);
            boolean matchBrand = phone.getBrand() != null
                    && phone.getBrand().toLowerCase().contains(target);
            if (matchName || matchBrand) {
                results.add(phone);
            }
        }

        return results;
    }
}
