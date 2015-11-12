package common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private String firstName;
    private String lastName;
    private String department;
    private Double salary;

    public Employee(String firstName, String lastName, String departament, Double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = departament;
        this.salary = salary;
    }

    public Employee() {
    }

    public Employee(JSONObject json) {
        try {
            this.firstName = json.getString("firstname");
            this.lastName = json.getString("lastname");
            this.department = json.getString("department");
            this.salary = json.getDouble("salary");
        } catch (JSONException ex) {
            System.out.println(ex);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee {" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Employee employee = (Employee) object;
            if (this.firstName.equals(employee.getFirstName())
                    && this.lastName.equals(employee.getLastName())
                    && this.department.equals(employee.getDepartment())
                    && this.salary.equals(employee.getSalary())) {
                result = true;
            }
        }
        return result;
    }

    public String toJSONString() {
        return "{" +
                "firstname: " + firstName +
                ",lastname:" + lastName +
                ",deppartment" + department +
                ",salary" + salary +
                "}";
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("Employee", this.toJSONString());
        return json;
    }
}
