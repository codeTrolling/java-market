public class CheckoutIsInactive extends Exception {
    CheckoutIsInactive() {
        super("This checkout is currently inactive (no worker).");
    }
}
