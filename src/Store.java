import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Store {
    private ArrayList<Cashier> employees;
    private Map<Integer, Cashier> checkouts;
    private float foodMarkup;
    private float nonFoodMarkup;
    private ArrayList<Product> initialProducts;
    private ArrayList<Product> products;
    private int daysToExpiryDateDiscount;
    // the percentage discount
    private float expiryDateDiscount;
    private String name;

    // The inner array lists contains the name of the product and the quantity to be marked/purchased.
    // e.g [["apple", "2"], ["banana", "4"]]
    private ArrayList<ArrayList<String>> markedProducts = new ArrayList<ArrayList<String>>();

    private int receiptCount = 0; 
    private float totalSalesRevenue = 0;

    Store(String name, ArrayList<Cashier> employees, float foodMarkup, float nonFoodMarkup, ArrayList<Product> products, int daysToExpiryDateDiscount, float expiryDateDiscount, int checkoutsCount) {
        this.name = name;
        this.employees = new ArrayList<Cashier>(employees);
        this.foodMarkup = foodMarkup;
        this.nonFoodMarkup = nonFoodMarkup;
        this.products = new ArrayList<Product>(products);
        this.initialProducts = new ArrayList<>();
        products.forEach((product) -> {
            initialProducts.add(new Product(product));
        });
        this.daysToExpiryDateDiscount = daysToExpiryDateDiscount;
        this.expiryDateDiscount = expiryDateDiscount;

        this.checkouts = new HashMap<Integer,Cashier>();
        for(int i = 0; i < checkoutsCount; i++){
            checkouts.put(i, null);
        }
    }

    public ArrayList<Product> getInitialProducts(){
        return initialProducts;
    }

    public float getTotalSalesRevenue() {
        return totalSalesRevenue;
    }

    public float setTotalSalesRevenue(float newValue) {
        totalSalesRevenue = newValue;
        return totalSalesRevenue;
    }

    public void addTotalSalesRevenue(float toAdd) {
        totalSalesRevenue += toAdd;
    }

    public int getDaysToExpiryDateDiscound() {
        return daysToExpiryDateDiscount;
    }

    public float getExpiryDateDiscount() {
        return expiryDateDiscount;
    }

    public float getFoodMarkup() {
        return foodMarkup;
    }

    public float getNonFoodMarkup() {
        return nonFoodMarkup;
    }

    public ArrayList<ArrayList<String>> getMarkedProducts() {
        return markedProducts;
    }

    public ArrayList<Cashier> getEmployees() {
        return employees;
    }

    public Map<Integer, Cashier> getCheckouts() {
        return checkouts;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public int getReceiptCount() {
        return receiptCount;
    }

    public int setReceiptCount(int newValue) {
        receiptCount = newValue;
        return receiptCount;
    }

    public float getSalesRevenue() {
        return totalSalesRevenue;
    }

    public String getName() {
        return name;
    }
}
