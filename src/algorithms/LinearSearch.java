package algorithms;

import java.util.ArrayList;
import java.util.List;

import model.Phone;

public class LinearSearch {
    public static List<Phone> linearSearch(List<Phone> lists, String key){
        List<Phone> results = new ArrayList();
        if(lists == null || lists.isEmpty())
            return results; 
        
        String target = key.toLowerCase(); 
        
        for(Phone phone : lists){
            if(phone.getProductName().toLowerCase().contains(target)){
                results.add(phone);
            }
        }

        return results;
    }
}
