package model;

public class IPhone extends Phone {
    private int batteryHealth;

    public IPhone(String id, String productName, String storage, double price, int quantity, int batteryHealth) {
        super(id, productName, "Apple", storage, price, quantity);
        this.batteryHealth = batteryHealth;
    }

    public int getBatteryHealth() {
        return batteryHealth;
    }

    public void setBatteryHealth(int batteryHealth) {
        this.batteryHealth = batteryHealth;
    }

    @Override
    public String getDetails() {
        return String.format("[Apple] Mã: %-10s | %-20s | %-6s | Tồn: %3d | Giá: %,12.0f VNĐ | Pin: %3d%%",
                id, productName, storage, quantity, price, batteryHealth);
    }

    @Override
    public String toFileString() {
        return String.join(";", "Apple", id, productName, storage, String.valueOf(price), String.valueOf(quantity),
                String.valueOf(batteryHealth));
    }

}
