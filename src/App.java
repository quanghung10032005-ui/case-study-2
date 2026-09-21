import java.util.List;
import java.util.Scanner;

import factory.AppleFactory;
import factory.PhoneFactory;
import factory.SamsungFactory;
import model.Phone;
import respository.FileWarehouseRepository;
import respository.IWarehouseRepository;
import service.WarehouseService;

public class App {
    private static final Scanner scanner = new Scanner(System.in);

    // Khởi tạo Repository và lấy thể hiện duy nhất của Service qua Singleton
    private static final IWarehouseRepository repository = new FileWarehouseRepository();
    private static final WarehouseService service = WarehouseService.getInstance(repository);

    private static final PhoneFactory appleFactory = new AppleFactory();
    private static final PhoneFactory samsungFactory = new SamsungFactory();

    public static void main(String[] args) throws Exception {
        while (true) {
            printMenu();
            System.out.print("Chon chuc nang (0-8): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayAllPhones();
                    break;
                case "2":
                    addNewPhone();
                    break;
                case "3":
                    updatePhoneStock();
                    break;
                case "4":
                    deletePhoneById();
                    break;
                case "5":
                    searchLinear();
                    break;
                case "6":
                    searchBinary();
                    break;
                case "7":
                    sortByPrice();
                    break;
                case "8":
                    exportReport();
                    break;
                case "0":
                    System.out.println("Da thoat chuong trinh. Tam biet!");
                    return;
                default:
                    System.out.println("Lua chon khong hop le. Vui long chon lai!");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("================================================");
        System.out.println("        HE THONG QUAN LY KHO DIEN THOAI         ");
        System.out.println("================================================");
        System.out.println("1. Hien thi toan bo kho hang");
        System.out.println("2. Them moi dien thoai (Apple / Samsung)");
        System.out.println("3. Cap nhat ton kho & gia ban theo ma");
        System.out.println("4. Xoa dien thoai theo ma");
        System.out.println("5. Tim kiem theo ten hoac hang (Linear Search)");
        System.out.println("6. Tim kiem theo ma ID (Binary Search)");
        System.out.println("7. Sap xep danh sach theo gia (Selection Sort)");
        System.out.println("8. Xuat bao cao kho ra file (Da luong)");
        System.out.println("0. Thoat");
        System.out.println("------------------------------------------------");
    }

    // 1. Hiển thị danh sách
    private static void displayAllPhones() {
        List<Phone> list = service.getAll();
        if (list.isEmpty()) {
            System.out.println("Kho hang hien dang trong.");
            return;
        }
        System.out.println("\n--- DANH SACH THIET BI TRONG KHO ---");
        for (Phone p : list) {
            System.out.println(p.getDetails());
        }
    }

    // 2. Thêm mới điện thoại
    private static void addNewPhone() {
        System.out.println("\n--- THEM MOI DIEN THOAI ---");
        System.out.print("Chon thuong hieu (1 - Apple, 2 - Samsung): ");
        String brandChoice = scanner.nextLine().trim();

        if (!brandChoice.equals("1") && !brandChoice.equals("2")) {
            System.out.println("Lua chon hang khong hop le!");
            return;
        }

        System.out.print("Nhap ma ID (VD: IP15-128, SS24-256): ");
        String id = scanner.nextLine().trim();

        System.out.print("Nhap ten san pham (VD: iPhone 15, Galaxy S24): ");
        String name = scanner.nextLine().trim();

        System.out.print("Nhap bo nho (VD: 128GB, 256GB): ");
        String storage = scanner.nextLine().trim();

        System.out.print("Nhap gia ban (VNĐ): ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Gia nhap vao khong hop le!");
            return;
        }

        System.out.print("Nhap so luong ton kho: ");
        int quantity;
        try {
            quantity = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("So luong khong hop le!");
            return;
        }

        Phone newPhone;
        if (brandChoice.equals("1")) {
            System.out.print("pin: ");
            String batteryHealth = scanner.nextLine().trim();
            newPhone = appleFactory.createPhone(id, name, storage, price, quantity, batteryHealth);
        } else {
            System.out.print("Ho tro but S-Pen? (true / false): ");
            String hasPen = scanner.nextLine().trim();
            newPhone = samsungFactory.createPhone(id, name, storage, price, quantity, hasPen);
        }

        boolean success = service.addPhone(newPhone);
        if (success) {
            System.out.println("-> Them san pham thanh cong!");
        } else {
            System.out.println("-> That bai: Ma ID da ton tai trong he thong.");
        }
    }

    // 3. Cập nhật số lượng và giá bán
    private static void updatePhoneStock() {
        System.out.println("\n--- CAP NHAT TON KHO & DON GIA ---");
        System.out.print("Nhap ma ID can sua: ");
        String id = scanner.nextLine().trim();

        Phone current = service.lookup(id);
        if (current == null) {
            System.out.println("-> Khong tim thay ma san pham nay!");
            return;
        }

        System.out.println("Thiet bi hien tai: " + current.getDetails());

        try {
            System.out.print("Nhap so luong moi: ");
            int newQuantity = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Nhap gia ban moi (VNĐ): ");
            double newPrice = Double.parseDouble(scanner.nextLine().trim());

            boolean success = service.updateStock(id, newQuantity, newPrice);
            if (success) {
                System.out.println("-> Cap nhat thong tin thanh cong!");
            } else {
                System.out.println("-> Loi trong qua trinh cap nhat.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Du lieu so khong hop le!");
        }
    }

    // 4. Xóa sản phẩm
    private static void deletePhoneById() {
        System.out.println("\n--- XOA DIEN THOAI ---");
        System.out.print("Nhap ma ID can xoa: ");
        String id = scanner.nextLine().trim();

        boolean success = service.deletePhone(id);
        if (success) {
            System.out.println("-> Da xoa thanh cong san pham co ma: " + id);
        } else {
            System.out.println("-> Khong tim thay san pham co ma nay de xoa.");
        }
    }

    // 5. Tìm kiếm tuyến tính
    private static void searchLinear() {
        System.out.println("\n--- TIM KIEM THEO TEN HOAC HANG (LINEAR SEARCH) ---");
        System.out.print("Nhap tu khoa can tim: ");
        String kw = scanner.nextLine().trim();

        List<Phone> results = service.searchByKeyword(kw);
        if (results.isEmpty()) {
            System.out.println("-> Khong tim thay san pham nao khop voi tu khoa: " + kw);
        } else {
            System.out.println("Tim thay " + results.size() + " ket qua:");
            for (Phone p : results) {
                System.out.println(p.getDetails());
            }
        }
    }

    // 6. Tìm kiếm nhị phân
    private static void searchBinary() {
        System.out.println("\n--- TÌM KIEM THEO MA ID (BINARY SEARCH) ---");
        System.out.print("Nhap ma ID chinh xac can tim: ");
        String id = scanner.nextLine().trim();

        Phone found = service.searchBinary(id);
        if (found != null) {
            System.out.println("-> Đa tim thay: " + found.getDetails());
        } else {
            System.out.println("-> Khong tim thay ma ID nay trong kho.");
        }
    }

    // 7. Sắp xếp theo giá
    private static void sortByPrice() {
        System.out.println("\n--- SAP XEP THEO GIA (SELECTION SORT) ---");
        System.out.print("Chon thu tu (1 - Gia tang dan, 2 - Gia giam dan): ");
        String order = scanner.nextLine().trim();

        boolean ascending = !order.equals("2");
        List<Phone> sortedList = service.sortByPrice(ascending);

        System.out.println("Danh sach sau khi sap xep:");
        for (Phone p : sortedList) {
            System.out.println(p.getDetails());
        }
    }

    // 8. Xuất file báo cáo đa luồng
    private static void exportReport() {
        String path = "./warehouse_data/report.txt";
        System.out.println("Đang kích hoạt tiến trình xuất file ngầm...");
        service.exportReportAsync(path);
    }
}
