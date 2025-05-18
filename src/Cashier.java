public class Cashier {
    private static Long GlobalID = Long.valueOf(0);

    private Long ID;
    private String firstName;
    private String lastName;
    private float salary;

    Cashier(String firstName, String lastName, float salary) {
        ID = GlobalID;
        GlobalID++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public Long getID() {
        return ID;
    }
}
