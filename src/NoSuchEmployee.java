public class NoSuchEmployee extends Exception {
    NoSuchEmployee() {
        super("Employee ID not found.");
    }
}
