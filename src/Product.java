import java.time.LocalDate;

public class Product {
    static Long GlobalID = Long.valueOf(0);

    private Long ID;
    private ProductCategory category;
    private String name;
    private float price;
    private int quantity;
    private LocalDate expiryDate;

    Product(ProductCategory category, String name, float price, int quantity, LocalDate expiryDate) {
        ID = GlobalID;
        GlobalID++;
        this.category = category;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public Long getID() {
        return ID;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
