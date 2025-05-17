public class Product {
    static Long GlobalID = Long.valueOf(0);

    private Long ID;
    private ProductCategory category;
    private String name;
    private float price;

    Product(ProductCategory category, String name, float price) {
        ID = GlobalID;
        GlobalID++;
        this.category = category;
        this.name = name;
        this.price = price;
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

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
