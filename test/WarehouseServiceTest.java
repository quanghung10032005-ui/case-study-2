package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.IPhone;
import model.Phone;
import model.Samsung;
import respository.IWarehouseRepository;

/**
 * Lớp kiểm thử đơn vị hoàn chỉnh cho WarehouseService sử dụng JUnit 5.
 * Đảm bảo bao phủ 100% các phương thức nghiệp vụ và chạy độc lập trên RAM (MockRepository).
 */
public class WarehouseServiceTest {

    private WarehouseService warehouseService;
    private MockRepository mockRepository;

    // 5 đối tượng mẫu kiểm thử
    private Phone p1;
    private Phone p2;
    private Phone p3;
    private Phone p4;
    private Phone p5;

    /**
     * Lớp nội tĩnh MockRepository implements IWarehouseRepository.
     * Lưu trữ dữ liệu trên bộ nhớ RAM bằng ArrayList<Phone> để cô lập hoàn toàn môi trường test,
     * không can thiệp đọc/ghi vào file vật lý phones.txt.
     */
    public static class MockRepository implements IWarehouseRepository {
        private final List<Phone> memoryStorage = new ArrayList<>();

        public MockRepository() {
        }

        public MockRepository(List<Phone> initialData) {
            if (initialData != null) {
                this.memoryStorage.addAll(initialData);
            }
        }

        @Override
        public void save(Phone phone) throws Exception {
            if (phone == null) {
                throw new IllegalArgumentException("Đối tượng Phone không được null");
            }
            boolean exists = false;
            for (int i = 0; i < memoryStorage.size(); i++) {
                if (memoryStorage.get(i).getId().equalsIgnoreCase(phone.getId())) {
                    memoryStorage.set(i, phone);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                memoryStorage.add(phone);
            }
        }

        @Override
        public void delete(String id) {
            if (id == null) {
                return;
            }
            memoryStorage.removeIf(p -> p.getId().equalsIgnoreCase(id.trim()));
        }

        @Override
        public List<Phone> loadAll() throws Exception {
            // Trả về bản sao độc lập của dữ liệu trên RAM
            return new ArrayList<>(memoryStorage);
        }

        public List<Phone> getMemoryStorage() {
            return memoryStorage;
        }
    }

    /**
     * Dùng Java Reflection để reset thuộc tính static instance của Singleton WarehouseService về null.
     */
    private void resetSingletonInstance() throws Exception {
        Field instanceField = WarehouseService.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    /**
     * Thiết lập môi trường trước mỗi kịch bản test:
     * - Reset Singleton để làm sạch trạng thái cũ.
     * - Khởi tạo 5 đối tượng mẫu p1, p2, p3, p4, p5 (đa dạng thương hiệu, giá cả, số lượng).
     * - Nạp dữ liệu vào MockRepository và lấy thể hiện mới của WarehouseService.
     */
    @BeforeEach
    void setUp() throws Exception {
        // 1. Xóa sạch thể hiện cũ của WarehouseService
        resetSingletonInstance();

        // 2. Khởi tạo 5 thiết bị mẫu
        p1 = new IPhone("IP13", "iPhone 13", "128GB", 14500000.0, 10, 85);
        p2 = new IPhone("IP15", "iPhone 15", "256GB", 24000000.0, 5, 95);
        p3 = new Samsung("SS23", "Galaxy S23", "128GB", 16900000.0, 8, false);
        p4 = new Samsung("SS24U", "Galaxy S24 Ultra", "512GB", 33900000.0, 4, true);
        p5 = new Samsung("SSA54", "Galaxy A54", "128GB", 7490000.0, 20, false);

        List<Phone> initialData = new ArrayList<>();
        initialData.add(p1);
        initialData.add(p2);
        initialData.add(p3);
        initialData.add(p4);
        initialData.add(p5);

        // 3. Khởi tạo MockRepository chứa 5 máy mẫu
        mockRepository = new MockRepository(initialData);

        // 4. Lấy đối tượng WarehouseService mới với kho dữ liệu mẫu
        warehouseService = WarehouseService.getInstance(mockRepository);
    }

    // =========================================================================
    // NHÓM THÊM MỚI (addPhone)
    // =========================================================================

    @Test
    @DisplayName("TC_SERVICE_ADD_01: Thêm thành công sản phẩm mới hợp lệ (kỳ vọng trả về true, số lượng kho tăng, lookup tìm thấy máy)")
    void testAddPhone_Success() {
        Phone newPhone = new IPhone("IP16", "iPhone 16 Pro", "256GB", 28000000.0, 12, 100);

        boolean result = warehouseService.addPhone(newPhone);

        assertTrue(result, "Thêm sản phẩm mới hợp lệ vào kho phải trả về true");
        assertEquals(6, warehouseService.getAll().size(), "Số lượng máy trong kho phải tăng lên 6 sau khi thêm thành công");
        Phone lookedUp = warehouseService.lookup("IP16");
        assertNotNull(lookedUp, "Phải tìm thấy sản phẩm vừa thêm khi tra cứu bằng mã ID");
        assertEquals("iPhone 16 Pro", lookedUp.getProductName(), "Tên sản phẩm tìm thấy phải khớp với thông tin đã thêm");
    }

    @Test
    @DisplayName("TC_SERVICE_ADD_02: Chặn thêm khi trùng mã ID đã tồn tại trong kho (kỳ vọng trả về false, số lượng kho giữ nguyên)")
    void testAddPhone_DuplicateId_Fails() {
        // p1 có mã là "IP13", tạo đối tượng mới trùng mã "ip13" (viết thường)
        Phone duplicatePhone = new Samsung("ip13", "Galaxy Clone", "128GB", 9000000.0, 3, false);

        boolean result = warehouseService.addPhone(duplicatePhone);

        assertFalse(result, "Thêm sản phẩm trùng mã ID đã có trong kho phải trả về false");
        assertEquals(5, warehouseService.getAll().size(), "Số lượng thiết bị trong kho phải giữ nguyên 5 khi thêm trùng mã");
    }

    @Test
    @DisplayName("TC_SERVICE_ADD_03: Chặn thêm khi đối tượng truyền vào là null (kỳ vọng trả về false)")
    void testAddPhone_NullPhone_Fails() {
        boolean result = warehouseService.addPhone(null);

        assertFalse(result, "Thêm đối tượng null vào kho phải trả về false");
        assertEquals(5, warehouseService.getAll().size(), "Tổng số lượng thiết bị trong kho không được thay đổi khi thêm null");
    }

    // =========================================================================
    // NHÓM TRA CỨU TỨC THÌ (lookup)
    // =========================================================================

    @Test
    @DisplayName("TC_SERVICE_LOOKUP_01: Tra cứu thành công với mã ID viết thường hoặc chữ hoa xen kẽ (không phân biệt hoa thường)")
    void testLookup_CaseInsensitive_Success() {
        // Tra cứu mã viết thường toàn bộ
        Phone foundLower = warehouseService.lookup("ip13");
        assertNotNull(foundLower, "Tra cứu bằng mã chữ thường 'ip13' phải tìm thấy sản phẩm");
        assertEquals("IP13", foundLower.getId(), "Mã ID của sản phẩm tìm thấy phải là IP13");

        // Tra cứu mã hoa thường xen kẽ
        Phone foundMixed = warehouseService.lookup("sS24u");
        assertNotNull(foundMixed, "Tra cứu bằng mã xen kẽ 'sS24u' phải tìm thấy sản phẩm");
        assertEquals("SS24U", foundMixed.getId(), "Mã ID của sản phẩm tìm thấy phải là SS24U");
    }

    @Test
    @DisplayName("TC_SERVICE_LOOKUP_02: Tra cứu trả về null khi truyền mã ID không tồn tại trong hệ thống")
    void testLookup_NonExistentId_ReturnsNull() {
        Phone result = warehouseService.lookup("UNKNOWN_ID_999");

        assertNull(result, "Tra cứu với mã ID không tồn tại trong kho phải trả về null");
    }

    @Test
    @DisplayName("TC_SERVICE_LOOKUP_03: Tra cứu khi truyền mã null hoặc chuỗi chỉ chứa khoảng trắng thừa")
    void testLookup_NullOrWhitespace_ReturnsNull() {
        Phone resultNull = warehouseService.lookup(null);
        assertNull(resultNull, "Tra cứu với mã null phải trả về null");

        Phone resultSpaces = warehouseService.lookup("    ");
        assertNull(resultSpaces, "Tra cứu với chuỗi chỉ chứa khoảng trắng thừa phải trả về null");
    }

    // =========================================================================
    // NHÓM CẬP NHẬT KHO (updateStock)
    // =========================================================================

    @Test
    @DisplayName("TC_SERVICE_UPDATE_01: Cập nhật tồn kho và đơn giá thành công khi đúng mã ID tồn tại (kỳ vọng true, kiểm tra lại số lượng và đơn giá mới)")
    void testUpdateStock_ExistingId_Success() {
        int newQuantity = 25;
        double newPrice = 13500000.0;

        boolean result = warehouseService.updateStock("IP13", newQuantity, newPrice);

        assertTrue(result, "Cập nhật tồn kho và đơn giá cho mã ID tồn tại phải trả về true");
        Phone updatedPhone = warehouseService.lookup("IP13");
        assertNotNull(updatedPhone, "Sản phẩm được cập nhật phải tồn tại trong kho");
        assertEquals(newQuantity, updatedPhone.getQuantity(), "Số lượng tồn kho mới phải là 25");
        assertEquals(newPrice, updatedPhone.getPrice(), "Đơn giá mới phải là 13,500,000 VNĐ");
    }

    @Test
    @DisplayName("TC_SERVICE_UPDATE_02: Cập nhật thất bại trả về false khi mã ID không có trong kho")
    void testUpdateStock_NonExistentId_ReturnsFalse() {
        boolean result = warehouseService.updateStock("NOT_IN_WAREHOUSE", 10, 5000000.0);

        assertFalse(result, "Cập nhật sản phẩm với mã ID không tồn tại trong kho phải trả về false");
    }

    // =========================================================================
    // NHÓM XÓA THIẾT BỊ (deletePhone)
    // =========================================================================

    @Test
    @DisplayName("TC_SERVICE_DELETE_01: Xóa thành công máy đang tồn tại (kỳ vọng true, tổng số máy giảm 1, lookup mã đó sau khi xóa trả về null)")
    void testDeletePhone_ExistingId_Success() {
        boolean result = warehouseService.deletePhone("IP13");

        assertTrue(result, "Xóa máy đang tồn tại trong kho phải trả về true");
        assertEquals(4, warehouseService.getAll().size(), "Tổng số lượng máy trong kho phải giảm 1 còn 4 máy sau khi xóa");
        assertNull(warehouseService.lookup("IP13"), "Tra cứu lại máy với mã vừa xóa phải trả về null");
    }

    @Test
    @DisplayName("TC_SERVICE_DELETE_02: Xóa thất bại trả về false khi mã ID không tồn tại")
    void testDeletePhone_NonExistentId_ReturnsFalse() {
        boolean result = warehouseService.deletePhone("UNKNOWN_ID");

        assertFalse(result, "Xóa sản phẩm với mã ID không tồn tại phải trả về false");
        assertEquals(5, warehouseService.getAll().size(), "Tổng số máy trong kho phải giữ nguyên 5 khi xóa không thành công");
    }

    // =========================================================================
    // NHÓM TÌM KIẾM (searchByKeyword & searchBinary)
    // =========================================================================

    @Test
    @DisplayName("TC_SERVICE_SEARCH_01: Tìm kiếm tuyến tính với từ khóa viết hoa/thường xen kẽ hoặc có khoảng trắng thừa ở hai đầu")
    void testSearchByKeyword_CaseAndWhitespace_Success() {
        // Tìm kiếm với từ khóa có khoảng trắng và chữ hoa thường xen kẽ "   iPhOnE   "
        List<Phone> results = warehouseService.searchByKeyword("   iPhOnE   ");

        assertNotNull(results, "Danh sách kết quả tìm kiếm không được null");
        assertEquals(2, results.size(), "Từ khóa 'iPhOnE' phải tìm thấy đúng 2 thiết bị iPhone (p1, p2)");
        assertTrue(results.stream().anyMatch(p -> p.getId().equals("IP13")), "Kết quả tìm kiếm phải chứa iPhone 13");
        assertTrue(results.stream().anyMatch(p -> p.getId().equals("IP15")), "Kết quả tìm kiếm phải chứa iPhone 15");
    }

    @Test
    @DisplayName("TC_SERVICE_SEARCH_02: Tìm kiếm tuyến tính trả về danh sách rỗng (isEmpty) khi từ khóa không khớp bất kỳ tên hay hãng nào")
    void testSearchByKeyword_NoMatch_ReturnsEmpty() {
        List<Phone> results = warehouseService.searchByKeyword("NokiaLumia");

        assertNotNull(results, "Danh sách kết quả trả về không được null");
        assertTrue(results.isEmpty(), "Danh sách kết quả phải rỗng khi không khớp với bất kỳ tên hay hãng nào");
    }

    @Test
    @DisplayName("TC_SERVICE_SEARCH_03: Tìm kiếm nhị phân theo mã ID chính xác trên danh sách đã sắp xếp")
    void testSearchBinary_ExistingId_Success() {
        Phone result = warehouseService.searchBinary("SS24U");

        assertNotNull(result, "Tìm kiếm nhị phân phải tìm thấy thiết bị với mã ID chính xác");
        assertEquals("SS24U", result.getId(), "Mã thiết bị tìm thấy phải khớp chính xác với 'SS24U'");
        assertEquals("Galaxy S24 Ultra", result.getProductName(), "Tên máy tìm thấy phải là 'Galaxy S24 Ultra'");
    }

    @Test
    @DisplayName("TC_SERVICE_SEARCH_04: Tìm kiếm nhị phân trả về null khi ID không tồn tại")
    void testSearchBinary_NonExistentId_ReturnsNull() {
        Phone result = warehouseService.searchBinary("NON_EXISTENT_PHONE");

        assertNull(result, "Tìm kiếm nhị phân với ID không tồn tại trong kho phải trả về null");
    }

    // =========================================================================
    // NHÓM SẮP XẾP CHỌN (sortByPrice)
    // =========================================================================

    @Test
    @DisplayName("TC_SERVICE_SORT_01: Sắp xếp theo giá tăng dần (phần tử đầu tiên có giá thấp nhất, phần tử cuối có giá cao nhất)")
    void testSortByPrice_Ascending_Success() {
        List<Phone> sortedList = warehouseService.sortByPrice(true);

        assertNotNull(sortedList, "Danh sách sau sắp xếp không được null");
        assertEquals(5, sortedList.size(), "Danh sách sau sắp xếp phải giữ nguyên 5 phần tử");

        // Kiểm tra phần tử đầu tiên (thấp nhất: SSA54 với 7,490,000 VNĐ)
        assertEquals("SSA54", sortedList.get(0).getId(), "Phần tử đầu tiên phải có mã SSA54 (giá thấp nhất)");
        assertEquals(7490000.0, sortedList.get(0).getPrice(), "Giá của phần tử đầu tiên phải là 7,490,000 VNĐ");

        // Kiểm tra phần tử cuối cùng (cao nhất: SS24U với 33,900,000 VNĐ)
        assertEquals("SS24U", sortedList.get(4).getId(), "Phần tử cuối cùng phải có mã SS24U (giá cao nhất)");
        assertEquals(33900000.0, sortedList.get(4).getPrice(), "Giá của phần tử cuối cùng phải là 33,900,000 VNĐ");

        // Kiểm tra tính đơn điệu tăng dần của toàn bộ danh sách
        for (int i = 0; i < sortedList.size() - 1; i++) {
            assertTrue(sortedList.get(i).getPrice() <= sortedList.get(i + 1).getPrice(),
                    "Mỗi phần tử phải có giá nhỏ hơn hoặc bằng phần tử đứng sau nó trong sắp xếp tăng dần");
        }
    }

    @Test
    @DisplayName("TC_SERVICE_SORT_02: Sắp xếp theo giá giảm dần (phần tử đầu tiên có giá cao nhất, phần tử cuối có giá thấp nhất)")
    void testSortByPrice_Descending_Success() {
        List<Phone> sortedList = warehouseService.sortByPrice(false);

        assertNotNull(sortedList, "Danh sách sau sắp xếp không được null");
        assertEquals(5, sortedList.size(), "Danh sách sau sắp xếp phải giữ nguyên 5 phần tử");

        // Kiểm tra phần tử đầu tiên (cao nhất: SS24U với 33,900,000 VNĐ)
        assertEquals("SS24U", sortedList.get(0).getId(), "Phần tử đầu tiên phải có mã SS24U (giá cao nhất)");
        assertEquals(33900000.0, sortedList.get(0).getPrice(), "Giá của phần tử đầu tiên phải là 33,900,000 VNĐ");

        // Kiểm tra phần tử cuối cùng (thấp nhất: SSA54 với 7,490,000 VNĐ)
        assertEquals("SSA54", sortedList.get(4).getId(), "Phần tử cuối cùng phải có mã SSA54 (giá thấp nhất)");
        assertEquals(7490000.0, sortedList.get(4).getPrice(), "Giá của phần tử cuối cùng phải là 7,490,000 VNĐ");

        // Kiểm tra tính đơn điệu giảm dần của toàn bộ danh sách
        for (int i = 0; i < sortedList.size() - 1; i++) {
            assertTrue(sortedList.get(i).getPrice() >= sortedList.get(i + 1).getPrice(),
                    "Mỗi phần tử phải có giá lớn hơn hoặc bằng phần tử đứng sau nó trong sắp xếp giảm dần");
        }
    }

    // =========================================================================
    // CÁC PHƯƠNG THỨC NGHIỆP VỤ KHÁC (getAll, exportReportAsync)
    // =========================================================================

    @Test
    @DisplayName("TC_SERVICE_GETALL_01: Lấy toàn bộ danh sách thiết bị đảm bảo tính bao đóng và độc lập")
    void testGetAll_Success() {
        List<Phone> list = warehouseService.getAll();

        assertNotNull(list, "Danh sách toàn bộ thiết bị không được null");
        assertEquals(5, list.size(), "Kho ban đầu phải trả về đủ 5 thiết bị");

        // Đảm bảo thay đổi trên bản sao không làm thay đổi dữ liệu nội tại
        list.clear();
        assertEquals(5, warehouseService.getAll().size(),
                "Thao tác clear trên danh sách trả về không được làm mất dữ liệu gốc trong kho");
    }

    @Test
    @DisplayName("TC_SERVICE_EXPORT_01: Xuất báo cáo kho hàng bất đồng bộ ra file")
    void testExportReportAsync_Success() throws InterruptedException {
        String testExportPath = "./test_export_report.txt";
        File file = new File(testExportPath);

        // Xóa file cũ nếu tồn tại
        if (file.exists()) {
            file.delete();
        }

        warehouseService.exportReportAsync(testExportPath);

        // Đợi luồng phụ hoàn thành ghi file (tối đa 1.5 giây)
        int waitTimeMs = 0;
        while (!file.exists() && waitTimeMs < 1500) {
            Thread.sleep(100);
            waitTimeMs += 100;
        }

        assertTrue(file.exists(), "File báo cáo xuất bất đồng bộ phải được tạo thành công trên ổ đĩa");
        assertTrue(file.length() > 0, "Dung lượng file báo cáo phải lớn hơn 0 byte sau khi ghi");

        // Dọn dẹp file test sau khi hoàn tất
        file.delete();
    }
}
