package respository;

import factory.PhoneFactory;
import model.Phone;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import factory.AppleFactory;
import factory.SamsungFactory;

public class FileWarehouseRepository implements IWarehouseRepository {
    private static final String FILE_PATH = "./warehouse_data/phones.txt";
    private final PhoneFactory appleFactory = new AppleFactory();
    private final PhoneFactory samsungFactory = new SamsungFactory();

    public FileWarehouseRepository() {
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (Exception e) {
            System.err.println("Không thể tạo file: " + e.getMessage());
        }
    }

    @Override
    public List<Phone> loadAll() throws Exception {
        List<Phone> lists = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists())
            return lists;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split(";");
                if (parts.length >= 7) {
                    String brand = parts[0].trim();
                    String id = parts[1].trim();
                    String productName = parts[2].trim();
                    String storage = parts[3].trim();
                    double price = Double.parseDouble(parts[4].trim());
                    int quantity = Integer.parseInt(parts[5].trim());
                    String extra = parts[6].trim();

                    if ("Apple".equalsIgnoreCase(brand)) {
                        lists.add(appleFactory.createPhone(id, productName, storage, price, quantity, extra));
                    } else if ("Samsung".equalsIgnoreCase(brand)) {
                        lists.add(samsungFactory.createPhone(id, productName, storage, price, quantity, extra));
                    }
                }

            }
        } catch (Exception e) {
            throw e;
        }
        return lists;

    }

    @Override
    public void save(Phone phone) throws Exception {
        List<Phone> lists = loadAll();
        boolean exists = false;

        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getId().equals(phone.getId())) {
                lists.set(i, phone);
                exists = true;
                break;
            }
        }

        if (!exists) {
            lists.add(phone);
        }

        rewriteFile(lists);
    }

    @Override
    public void delete(String id) {
        try {
            List<Phone> lists = loadAll();
            boolean exists = false;
            for (int i = 0; i < lists.size(); i++) {
                if (lists.get(i).getId().equals(id)) {
                    lists.remove(i);
                    exists = true;
                    break;
                }
            }
            if (exists) {
                rewriteFile(lists);
                return;
            }

            if (!exists) {
                System.out.println("Không tìm thấy: " + id);
                return;
            }

        } catch (Exception e) {
            System.err.println("Không thể xóa: " + e.getMessage());
        }
    }

    private void rewriteFile(List<Phone> lists) throws Exception {
        File file = new File(FILE_PATH);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Phone phone : lists) {
                bw.write(phone.toFileString());
                bw.newLine();
            }
        } catch (Exception e) {
            throw e;
        }
    }

}
