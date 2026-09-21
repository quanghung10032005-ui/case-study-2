package model;

public abstract class Phone {
    protected String id;
    protected String productName;
    protected String brand;
    protected String storage;
    protected double price;
    protected int quantity;

    public Phone() {

    }

    public Phone(String id, String productName, String brand, String storage, double price, int quantity) {
        this.id = id;
        this.productName = productName;
        this.brand = brand;
        this.storage = storage;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrand() {
        return brand;
    }

    public String getStorage() {
        return storage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public abstract String getDetails();

    public abstract String toFileString();

}
