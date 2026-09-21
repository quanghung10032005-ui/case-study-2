package factory;

import model.Phone;
import model.Samsung;

public class SamsungFactory implements PhoneFactory {

    @Override
    public Phone createPhone(String id, String productName, String storage, double price, int quantity, String extra) {
        boolean hasSPen = false;
        try {
            hasSPen = Boolean.parseBoolean(extra);
        } catch (NumberFormatException e) {
            System.err.println("Lỗi định dạng S-Pen: " + extra);
            e.printStackTrace();
        }
        return new Samsung(id, productName, storage, price, quantity, hasSPen);
    }
}
