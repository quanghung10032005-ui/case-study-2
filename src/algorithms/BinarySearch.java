package algorithms;

import java.util.List;

import model.Phone;

public class BinarySearch {
    public static Phone binarySearch(List<Phone> lists, String id) {
        if (lists == null || lists.isEmpty() || id == null)
            return null;
        int left = 0;
        int right = lists.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Phone phone = lists.get(mid);
            int cmp = id.compareTo(phone.getId());
            if (cmp == 0) {
                return phone;
            } else if (cmp < 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return null;
    }
}
