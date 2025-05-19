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
        this.initialProducts = new ArrayList<Product>(products);
        this.products = new ArrayList<Product>(products);
        this.daysToExpiryDateDiscount = daysToExpiryDateDiscount;
        this.expiryDateDiscount = expiryDateDiscount;

        this.checkouts = new HashMap<Integer,Cashier>();
        for(int i = 0; i < checkoutsCount; i++){
            checkouts.put(i, null);
        }
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public int getReceiptCount() {
        return receiptCount;
    }

    public float getSalesRevenue() {
        return totalSalesRevenue;
    }

    public void changeEmployeeCheckout(int checkout, int employeeID) throws NoSuchCheckout, NoSuchEmployee {
        if (!checkouts.containsKey(checkout)) {
            throw new NoSuchCheckout();
        }

        Cashier employee = null;
        for (int i = 0; i < employees.size(); i++) {
            if (employeeID == employees.get(i).getID()) {
                employee = employees.get(i);
                break; 
            }

            if (i == employees.size() - 1) {
                throw new NoSuchEmployee();
            }
        }

        checkouts.put(checkout, employee);
    }

    public void markProduct(String productToMark) {
        for(int i = 0; i < markedProducts.size(); i++) {
            if (markedProducts.get(i).get(0) == productToMark) {
                Integer newQuantity = Integer.valueOf(markedProducts.get(i).get(1));
                newQuantity++;
                markedProducts.get(i).set(1, newQuantity.toString());

                return;
            }    
        }
        markedProducts.add(new ArrayList<String>(Arrays.asList(productToMark, "1")));
    }

    public void makePurchase(int checkout, Client client) throws NoSuchCheckout, CheckoutIsInactive, NotEnoughMoney, NotEnoughProduct, ExpiredProduct {
        if (!checkouts.containsKey(checkout)) {
            throw new NoSuchCheckout();
        }

        if (checkouts.get(checkout) == null) {
            throw new CheckoutIsInactive();
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-dd-MM-HH:mm");
        String receipt = "Receipt number: " + (receiptCount + 1) + "\nCashier: " + checkouts.get(checkout).getFullName() + "\nTime: " + LocalDateTime.now().format(dateFormatter)
        + "\n ==========================================" + "\nProducts:\n";

        float totalPrice = 0;
        for (int i = 0; i < markedProducts.size(); i++) {
            Integer quantityToBePurchased = Integer.valueOf(markedProducts.get(i).get(1));
            for (int j = 0; j < products.size(); j++) {
                if (products.get(j).getName() != markedProducts.get(i).get(0)) {
                    continue;
                }

                if (products.get(j).getQuantity() < quantityToBePurchased) {
                    throw new NotEnoughProduct("Insufficient " + products.get(j).getName() + " quantity. You want to buy " + quantityToBePurchased + " but there's only " + products.get(j).getQuantity());
                }

                float price = products.get(j).getPrice() * quantityToBePurchased;
                price += price * (products.get(j).getCategory() == ProductCategory.FOOD ? foodMarkup : nonFoodMarkup);
                
                long daysUntilProductExpires = ChronoUnit.DAYS.between(LocalDate.now(), products.get(j).getExpiryDate());
                if (daysUntilProductExpires < 0) {
                    throw new ExpiredProduct(products.get(j).getName() + " has expired.");
                }
                if (daysUntilProductExpires <= daysToExpiryDateDiscount) {
                    price += price * expiryDateDiscount;
                }
                price = Math.round(price * 100f) / 100f;
                totalPrice += price;

                receipt += products.get(j).getName();
                int spacesToAdd = 30 - products.get(j).getName().length();
                for (int k = 0; k < spacesToAdd; k++) {
                    receipt += " ";
                }
                receipt += "x" + quantityToBePurchased + "    " + price + "\n";
            }
        }

        receipt += "Total price: " + totalPrice;
        
        if(client.getBalance() < totalPrice) {
            throw new NotEnoughMoney("You need " + (totalPrice - client.getBalance()) + " more money.");
        }
        
        System.out.println(receipt);
        serializeReceipt(receipt);
        client.setBalance(client.getBalance() - totalPrice);

        for(int i = 0; i < markedProducts.size(); i++) {
            for (int j = 0; j < products.size(); j++) {
                if (products.get(j).getName() != markedProducts.get(i).get(0)) {
                    continue;
                }

                products.get(j).setQuantity(products.get(j).getQuantity() - Integer.valueOf(markedProducts.get(i).get(1)));
            }
        }

        markedProducts.clear();
        receiptCount++;
        totalSalesRevenue += totalPrice;
    }

    private void serializeReceipt(String receipt) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Receipts/" + getName() + "_receipt_" + (receiptCount + 1) + ".ser"));
            FileWriter writer = new FileWriter("Receipts/" + getName() + "_receipt_" + (receiptCount + 1) + ".txt");
            writer.write(receipt);
            out.writeObject(receipt);

            writer.close();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelOrder() {
        markedProducts.clear();
    }

    public String getReceipt(int number) {
        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("Receipts/" + getName() + "_receipt_" + number + ".ser"));
            String receipt = (String) in.readObject();
            in.close();

            return receipt;
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Receipt not found";
        }
    }

    public String getReceipt(String number) {
        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("Receipts/" + getName() + "_receipt_" + number + ".ser"));
            String receipt = (String) in.readObject();
            in.close();

            return receipt;
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Receipt not found";
        }
    }

    public String getName() {
        return name;
    }

    public float getSalesProfit() {
        float salesExpenses = 0;
        for (int i = 0; i < products.size(); i++){
            salesExpenses += products.get(i).getPrice() * products.get(i).getQuantity();
        }
        salesExpenses = Math.round(salesExpenses * 100f) / 100f;

        return totalSalesRevenue - salesExpenses;
    }

    public float getEmployeeSalariesExpenses() {
        float employeeSalaries = 0;
        for (int i = 0; i < employees.size(); i++){
            employeeSalaries += employees.get(i).getSalary();
        }

        return employeeSalaries;
    }

    public float getProfit() {
        return Math.round((getSalesProfit() - getEmployeeSalariesExpenses()) * 100f) / 100f;
    }

    // mainly for testing
    public void addProduct(Product product) {
        products.add(product);
        initialProducts.add(product);
    }
}
