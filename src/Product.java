public class Product {
    private String ID;
    private ProductCategory category;
    private String name;
    private float price;

    Product(String id, ProductCategory category, String name, float price) {
        ID = id;
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public String getID() {
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
