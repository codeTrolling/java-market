import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;

public class StoreTests {
    private Store store;
    private Client client;

    @BeforeEach
    public void setUp() {
        Cashier c = new Cashier("bob", "smith", 1000);
        Cashier c2 = new Cashier("John", "Smith", 130);
        Product apple = new Product(ProductCategory.FOOD, "Apple", 1f, 30, LocalDate.of(2025, 5, 30));
        Product banana = new Product(ProductCategory.FOOD, "Banana", 1.39f, 3, LocalDate.of(2025, 5, 30));
        Product carOil = new Product(ProductCategory.NONFOOD, "Car Oil", 20.99f, 17, LocalDate.of(2027, 5, 30));
        Product someExpensiveThing = new Product(ProductCategory.NONFOOD, "Rolex", 2500f, 1, LocalDate.of(9999, 5, 30));
        Product expired = new Product(ProductCategory.FOOD, "Expired", 0, 125235325, LocalDate.of(1999, 9, 9));
        store = new Store("Walmart", new ArrayList<>(Arrays.asList(c, c2)), 0.1f, 0.2f, new ArrayList<>(Arrays.asList(apple, banana, carOil, someExpensiveThing, expired)), 5, 0.5f, 4);
        store.changeEmployeeCheckout(1, c2.getID().intValue());

        client = new Client(200);
    }

    @Test
    public void clientGetsCharged(){
        store.markProduct("Apple");
        store.makePurchase(1, client);

        assertEquals(199, client.getBalance(), 0.11, "Client should get charged after making a purchase.");
    }

    @Test
    public void badChekoutLineThrowsError() {
        store.markProduct("Apple");

        assertThrows(CheckoutIsInactive.class, () -> {store.makePurchase(0, client);});
    }

    @Test
    public void insufficientProductQuantity(){
        store.markProduct("Banana");
        store.markProduct("Banana");
        store.markProduct("Banana");
        store.markProduct("Banana");
        
        assertThrows(NotEnoughProduct.class, () -> {store.makePurchase(1, client);});
    }

    @Test
    public void expiredProduct() {
        store.markProduct("Expired");

        assertThrows(ExpiredProduct.class, () -> {store.makePurchase(1, client);}, "Expired Products should not be sold.");
    }

    @Test
    public void calculateProfit(){
        store.markProduct("Apple");
        store.makePurchase(1, client);

        // All products + employee salaries
        float expenses = 30 + (1.39f * 3) + (20.99f * 17) + 2500f + 1130;
        expenses = Math.round(expenses * 100f) / 100f;

        // 1 apple for 1$ + 10% markup = 1.10 -> 0.1 profit. expected profit should be expenses - 0.1
        assertEquals((expenses - 1) * -1, store.getProfit(), 1.11);
    }

    @Test
    public void dontSellIfClientDoesntHaveFund() {
        store.markProduct("Rolex");

        assertThrows(NotEnoughMoney.class, () -> {store.makePurchase(1, client);}, "A client who's broke shouldn't be able to buy expensive items.");
    }

    @Test
    public void productQuantityDecreases() {
        store.markProduct("Apple");
        store.markProduct("Apple");
        store.makePurchase(1, client);

        assertEquals(28, store.getProducts().get(0).getQuantity(), "The quantity of a product should go down with purchases");
    }
}
