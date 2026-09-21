package algorithms;

import java.util.List;

import model.Phone;

public class SortById {
    public static void selectionSortById(List<Phone> lists) {
        if (lists == null || lists.size() <= 1) {
            return;
        }
        for (int i = 0; i < lists.size(); i++) {
            int minIndex = i;
            for (int j = i + 1; j < lists.size(); j++) {
                if (lists.get(j).getId().compareTo(lists.get(minIndex).getId()) < 0) {
                    minIndex = j;
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
