package factory;

import model.IPhone;
import model.Phone;

public class AppleFactory implements PhoneFactory {

    @Override
    public Phone createPhone(String id, String productName, String storage, double price, int quantity, String extra) {
        int batteryHealth = 0;
        try {
            batteryHealth = Integer.parseInt(extra);
        } catch (NumberFormatException e) {
            System.err.println("Lỗi định dạng số pin: " + extra);
            e.printStackTrace();
        }
        return new IPhone(id, productName, storage, price, quantity, batteryHealth);
    }
}
