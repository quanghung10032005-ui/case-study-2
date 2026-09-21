package factory;

import model.Phone;

public interface PhoneFactory {
    Phone createPhone(String id, String productName, String storage, double price, int quantity, String extra);

}
