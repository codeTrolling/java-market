public class ExpiredProduct extends RuntimeException {
    ExpiredProduct(String message) {
        super(message);
    }
}
