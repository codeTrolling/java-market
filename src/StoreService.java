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
import java.util.Map;

public class StoreService {
    public void changeEmployeeCheckout(Store s, int checkout, int employeeID) throws NoSuchCheckout, NoSuchEmployee {
        Map<Integer, Cashier> checkouts = s.getCheckouts();
        ArrayList<Cashier> employees = s.getEmployees();

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

    public void markProduct(Store s, String productToMark) {
        ArrayList<ArrayList<String>> markedProducts = s.getMarkedProducts();

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

    public void makePurchase(Store s, int checkout, Client client) throws NoSuchCheckout, CheckoutIsInactive, NotEnoughMoney, NotEnoughProduct, ExpiredProduct {
        Map<Integer, Cashier> checkouts = s.getCheckouts();
        int receiptCount = s.getReceiptCount();
        ArrayList<ArrayList<String>> markedProducts = s.getMarkedProducts();
        ArrayList<Product> products = s.getProducts();
        float foodMarkup = s.getFoodMarkup();
        float nonFoodMarkup = s.getNonFoodMarkup();
        int daysToExpiryDateDiscount = s.getDaysToExpiryDateDiscound();
        float expiryDateDiscount = s.getExpiryDateDiscount();

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
                    price -= price * expiryDateDiscount;
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
        serializeReceipt(s, receipt);
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
        s.setReceiptCount(receiptCount + 1);
        s.addTotalSalesRevenue(totalPrice);
    }

    private void serializeReceipt(Store s, String receipt) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Receipts/" + s.getName() + "_receipt_" + (s.getReceiptCount() + 1) + ".ser"));
            FileWriter writer = new FileWriter("Receipts/" + s.getName() + "_receipt_" + (s.getReceiptCount() + 1) + ".txt");
            writer.write(receipt);
            out.writeObject(receipt);

            writer.close();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getReceipt(Store s, int number) {
        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("Receipts/" + s.getName() + "_receipt_" + number + ".ser"));
            String receipt = (String) in.readObject();
            in.close();

            return receipt;
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Receipt not found";
        }
    }

    public String getReceipt(Store s, String number) {
        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("Receipts/" + s.getName() + "_receipt_" + number + ".ser"));
            String receipt = (String) in.readObject();
            in.close();

            return receipt;
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Receipt not found";
        }
    }

    public float getSalesProfit(Store s) {
        ArrayList<Product> products = s.getProducts();
        ArrayList<Product> initialProducts = s.getInitialProducts();
        float totalSalesRevenue = s.getTotalSalesRevenue();

        float salesExpenses = 0;
        for (int i = 0; i < products.size(); i++){
            salesExpenses += products.get(i).getPrice() * initialProducts.get(i).getQuantity();
        }
        salesExpenses = Math.round(salesExpenses * 100f) / 100f;

        return totalSalesRevenue - salesExpenses;
    }

    public float getEmployeeSalariesExpenses(Store s) {
        float employeeSalaries = 0;
        ArrayList<Cashier> employees = s.getEmployees();
        for (int i = 0; i < employees.size(); i++){
            employeeSalaries += employees.get(i).getSalary();
        }

        return employeeSalaries;
    }

    public float getProfit(Store s) {
        return Math.round((getSalesProfit(s) - getEmployeeSalariesExpenses(s)) * 100f) / 100f;
    }

    // mainly for testing
    public void addProduct(Store s, Product product) {
        s.getProducts().add(product);
        s.getInitialProducts().add(product);
    }

    public void soldProducts(Store s) {
        ArrayList<Product> products = s.getProducts();
        ArrayList<Product> initialProducts = s.getInitialProducts();
        float foodMarkup = s.getFoodMarkup();
        float nonFoodMarkup = s.getNonFoodMarkup();
        int daysToExpiryDateDiscount = s.getDaysToExpiryDateDiscound();
        float expiryDateDiscount = s.getExpiryDateDiscount();

        System.out.println("Sold products:");
        for(int i = 0; i < products.size(); i++) {
            int soldCount = initialProducts.get(i).getQuantity() - products.get(i).getQuantity();
            System.out.println(products.get(i).getName() + ": " + soldCount);

            float expense = Math.round((initialProducts.get(i).getPrice() * initialProducts.get(i).getQuantity()) * 100f) / 100f;
            float revenue = products.get(i).getPrice() * soldCount;
            revenue += revenue * (products.get(i).getCategory() == ProductCategory.FOOD ? foodMarkup : nonFoodMarkup);
            
            long daysUntilProductExpires = ChronoUnit.DAYS.between(LocalDate.now(), products.get(i).getExpiryDate());
            if (daysUntilProductExpires <= daysToExpiryDateDiscount) {
                revenue += revenue * expiryDateDiscount;
            }
            revenue = Math.round(revenue * 100f) / 100f;

            System.out.println("Revenue: " + (Math.round((revenue - expense) * 100f) / 100f));
            System.out.println("=============================");
        }
    }

    public void cancelOrder(Store s) {
        s.getMarkedProducts().clear();
    }
}
