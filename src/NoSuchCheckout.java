public class NoSuchCheckout extends Exception {
    NoSuchCheckout() {
        super("Checkout does not exist.");
    }
}
