import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.*;
import org.mockito.*;

public class StoreTestsMockito {
    @Mock
    private StoreService ss;
    
    @Spy
    private Client client;
    private Store s;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        Cashier c = new Cashier("bob", "smith", 1000);
        Cashier c2 = new Cashier("John", "Smith", 130);
        Product apple = new Product(ProductCategory.FOOD, "Apple", 1f, 30, LocalDate.of(2025, 6, 30));
        Product banana = new Product(ProductCategory.FOOD, "Banana", 1.39f, 3, LocalDate.of(2025, 6, 30));
        Product carOil = new Product(ProductCategory.NONFOOD, "Car Oil", 20.99f, 17, LocalDate.of(2027, 5, 30));
        Product someExpensiveThing = new Product(ProductCategory.NONFOOD, "Rolex", 2500f, 1, LocalDate.of(9999, 5, 30));
        Product expired = new Product(ProductCategory.FOOD, "Expired", 0, 125235325, LocalDate.of(1999, 9, 9));
        s = new Store("Walmart", new ArrayList<>(Arrays.asList(c, c2)), 0.1f, 0.2f, new ArrayList<>(Arrays.asList(apple, banana, carOil, someExpensiveThing, expired)), 5, 0.5f, 4);

        ss = mock(StoreService.class);
        doNothing().when(ss).changeEmployeeCheckout(s, 1, c2.getID().intValue());
        client = new Client(200);
    }

    @Test
    public void TestPurchasingProcess() {
        doNothing().when(ss).markProduct(s, "Apple");
        doNothing().when(ss).markProduct(s, "Apple");
        doNothing().when(ss).markProduct(s, "Apple");
        doNothing().when(ss).makePurchase(s, 1, client);

        verify(ss, times(3)).markProduct(s, "Apple");
        verify(ss).makePurchase(s, 1, client);

        float expenses = 30 + (1.39f * 3) + (20.99f * 17) + 2500f + 1130;
        expenses = Math.round(expenses * 100f) / 100f;
        // test for profit
        assertEquals((expenses - 3.3) * -1, ss.getProfit(s), 0.11);

        // test for client balance
        assertEquals(196.7, client.getBalance(), 0.1);

        // test if quantity went down
        assertEquals(27, s.getProducts().get(0).getQuantity());
    }
}
