import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Cashier c = new Cashier("bob", "smith", 1000);
        Cashier c2 = new Cashier("John", "Smith", 30);
        Product apple = new Product(ProductCategory.FOOD, "Apple", 1.28f, 30, LocalDate.of(2025, 6, 30));
        Product banana = new Product(ProductCategory.FOOD, "Banana", 1.39f, 3, LocalDate.of(2025, 6, 30));
        Product carOil = new Product(ProductCategory.NONFOOD, "Car Oil", 20.99f, 17, LocalDate.of(2027, 5, 30));
        Store s = new Store("Walmart", new ArrayList<>(Arrays.asList(c, c2)), 0.1f, 0.2f, new ArrayList<>(Arrays.asList(apple, banana, carOil)), 5, 0.5f, 4);
        StoreService ss = new StoreService();

        Client client = new Client(400);
        ss.changeEmployeeCheckout(s, 1, 0);
        ss.markProduct(s, "Apple");
        ss.markProduct(s, "Apple");
        ss.markProduct(s, "Banana");
        ss.markProduct(s, "Car Oil");

        ss.makePurchase(s, 1, client);

        ss.markProduct(s, "Banana");
        ss.markProduct(s, "Banana");
        ss.markProduct(s, "Car Oil");
        ss.markProduct(s, "Apple");
        ss.makePurchase(s, 1, client);

        ss.soldProducts(s);
        System.out.println("Total Sales Profit: " + ss.getSalesProfit(s));
        System.out.println("Total profit: " + ss.getProfit(s));

        System.out.println(ss.getReceipt(s, 1));
    }
}
