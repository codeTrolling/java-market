public class CheckoutIsInactive extends RuntimeException {
    CheckoutIsInactive() {
        super("This checkout is currently inactive (no worker).");
    }
}
