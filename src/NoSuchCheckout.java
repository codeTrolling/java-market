public class NoSuchCheckout extends RuntimeException {
    NoSuchCheckout() {
        super("Checkout does not exist.");
    }
}
