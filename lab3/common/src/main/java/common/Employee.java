package common;

import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
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

    @XmlElement
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @XmlElement
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    @XmlElement
    public void setDepartment(String department) {
        this.department = department;
    }

    public Double getSalary() {
        return salary;
    }

    @XmlElement
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
        JSONObject json = new JSONObject();

        json.put("firstname", firstName);
        json.put("lastname", lastName);
        json.put("department", department);
        json.put("salary", salary);

        return json.toString();
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("Employee", new JSONObject(this.toJSONString()));
        return json;
    }

    public static Employee fromJSON(JSONObject jsonObject) {

        Employee emp = new Employee();

        emp.setFirstName(jsonObject.getString("firstname"));
        emp.setLastName(jsonObject.getString("lastname"));
        emp.setDepartment(jsonObject.getString("department"));
        emp.setSalary(jsonObject.getDouble("salary"));

        return emp;
    }
}
