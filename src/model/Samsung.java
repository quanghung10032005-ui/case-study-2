package model;

public class Samsung extends Phone {
    private boolean hasSPen;

    public Samsung(String id, String productName, String storage, double price, int quantity, boolean hasSPen) {
        super(id, productName, "Samsung", storage, price, quantity);
        this.hasSPen = hasSPen;
    }

    public boolean isHasSPen() {
        return hasSPen;
    }

    public void setHasSPen(boolean hasSPen) {
        this.hasSPen = hasSPen;
    }

    @Override
    public String getDetails() {
        return String.format("[Samsung] Mã: %-8s | %-20s | %-6s | Tồn: %3d | Giá: %,12.0f VNĐ | S-Pen: %-4s",
                id, productName, storage, quantity, price, hasSPen ? "Có" : "Không");
    }

    @Override
    public String toFileString() {
        return String.join(";", "Samsung", id, productName, storage, String.valueOf(price), String.valueOf(quantity),
                String.valueOf(hasSPen));
    }
}