package service;

import java.util.List;

import model.Phone;

public interface IPhoneManager {
    boolean addPhone(Phone phone);

    boolean updateStock(String id, int newQuantity, double newPrice);

    boolean deletePhone(String id);

    Phone lookup(String id);

    List<Phone> getAll();

    List<Phone> searchByKeyword(String keyword);

    Phone searchBinary(String id);

    List<Phone> sortByPrice(boolean ascending);

    void exportReportAsync(String exportPath);
}
