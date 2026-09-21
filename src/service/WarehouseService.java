package service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import algorithms.BinarySearch;
import algorithms.LinearSearch;
import algorithms.SortById;
import algorithms.SortByPrice;
import model.Phone;
import respository.IWarehouseRepository;
import java.io.FileWriter;

public class WarehouseService implements IPhoneManager {
    private static WarehouseService instance;
    private final IWarehouseRepository repository;
    private final Map<String, Phone> phoneMap;
    private final List<Phone> phoneList;

    private WarehouseService(IWarehouseRepository repository) {
        this.repository = repository;
        this.phoneMap = new HashMap<>();
        this.phoneList = new LinkedList<>();
        loadDataFromRepository();
    }

    public static synchronized WarehouseService getInstance(IWarehouseRepository repository) {
        if (instance == null) {
            instance = new WarehouseService(repository);
        }
        return instance;
    }

    private void loadDataFromRepository() {
        try {
            List<Phone> lists = repository.loadAll();
            for (Phone p : lists) {
                phoneMap.put(p.getId(), p);
                phoneList.add(p);
            }

        } catch (Exception e) {
            System.err.println("Lỗi tải dữ liệu:" + e.getMessage());
        }
    }

    @Override
    public boolean addPhone(Phone phone) {
        // kiem tra du lieu rong hoac trung id

        if (phone == null || phoneMap.containsKey(phone.getId().toLowerCase()))
            return false;

        phoneMap.put(phone.getId().toLowerCase(), phone);
        phoneList.add(phone);

        // ghi xuong file
        try {
            repository.save(phone);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi lưu file: " + e.getMessage());
            return false;
        }

    }

    @Override
    public boolean updateStock(String id, int newQuantity, double newPrice) {
        Phone phone = lookup(id);
        if (phone == null)
            return false;

        // cap nhat thong tin
        phone.setQuantity(newQuantity);
        phone.setPrice(newPrice);

        // ghi xuong file
        try {
            repository.save(phone);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật file: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deletePhone(String id) {
        Phone phone = lookup(id);
        if (phone == null)
            return false;

        // xoa khoi map va list
        phoneMap.remove(id.toLowerCase());
        phoneList.remove(phone);

        repository.delete(id);

        return true;
    }

    @Override
    public Phone lookup(String id) {
        if (id == null)
            return null;

        return phoneMap.get(id.trim().toLowerCase());
    }

    @Override
    public List<Phone> getAll() {
        return new ArrayList<>(phoneList);

    }

    @Override
    public List<Phone> searchByKeyword(String keyword) {
        return LinearSearch.linearSearch(phoneList, keyword);
    }

    @Override
    public Phone searchBinary(String id) {
        List<Phone> copy = new ArrayList<>(phoneList);
        SortById.selectionSortById(copy);
        return BinarySearch.binarySearch(copy, id);
    }

    @Override
    public List<Phone> sortByPrice(boolean ascending) {
        List<Phone> copy = new ArrayList<>(phoneList);
        SortByPrice.sortByPrice(copy, ascending);
        return copy;
    }

    @Override
    public void exportReportAsync(String exportPath) {
        List<Phone> listToExport = new ArrayList<>(phoneList);
        Thread reportThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(exportPath));
                    writer.write("=== DANH SACH KHO HANG ===");
                    writer.newLine();

                    for (Phone p : listToExport) {
                        writer.write(p.getDetails());
                        writer.newLine();
                    }
                    writer.close();
                    System.out.println("\n[He thong]: Da xuat bao cao ra file: " + exportPath);
                } catch (Exception e) {
                    System.err.println("Loi khi xuat file: " + e.getMessage());
                }
            }
        });

        reportThread.start();
    }

}
