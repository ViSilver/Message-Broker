package common;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class Employee implements Serializable, Identifiable {

    private Integer id;
    @XmlElement
    public String firstName;
    @XmlElement
    public String lastName;
    @XmlElement
    public String department;
    @XmlElement
    public double salary;

    public Employee() {
        this.firstName = "";
        this.lastName = "";
        this.department = "";
        this.salary = 0.0;
    }
    public Employee(String fn, String ln, String dep, double s) {
        this.firstName = fn;
        this.lastName = ln;
        this.department = dep;
        this.salary = s;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (id != null) {
            s.append(id);
            s.append(": ");
        }

        s.append(String.format("%-10s ", firstName));
        s.append(String.format("%-10s @ ", lastName));
        s.append(department);
        s.append(" -> ");
        s.append(salary);
        return s.toString();
    }
}