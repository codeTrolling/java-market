public class NoSuchEmployee extends RuntimeException {
    NoSuchEmployee() {
        super("Employee ID not found.");
    }
}
