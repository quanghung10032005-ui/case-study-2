package algorithms;

import java.util.List;

import model.Phone;

public class SortByPrice {
    public static void sortByPrice(List<Phone> lists, boolean ascending) {
        if (lists == null || lists.size() <= 1) {
            return;
        }

        for (int i = 0; i < lists.size(); i++) {
            int minIndex = i;
            for (int j = i + 1; j < lists.size(); j++) {
                if (ascending) {
                    if (lists.get(j).getPrice() < lists.get(minIndex).getPrice()) {
                        minIndex = j;
                    }
                } else {
                    if (lists.get(j).getPrice() > lists.get(minIndex).getPrice()) {
                        minIndex = j;
                    }
                }
            }
            if (minIndex != i) {
                Phone temp = lists.get(i);
                lists.set(i, lists.get(minIndex));
                lists.set(minIndex, temp);
            }
        }
    }
}
