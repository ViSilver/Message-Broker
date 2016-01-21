package common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Vi
 */
@XmlRootElement(name="employees")
public class Employees {
    public Employees() {
    }

    public Employees(List<Employee> employees) {
        this.employees = employees;
    }

    @XmlElement(name="employee")
    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    private List<Employee> employees;
}