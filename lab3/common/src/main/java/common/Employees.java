package common;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlSeeAlso(Employee.class)
public class Employees extends ArrayList<Employee>{

    public Employees() {}

    public Employees (ArrayList<Employee> employees) {
        for (Employee employee : employees) {
            this.add(employee);
        }
    }

    @XmlElement(name = "employee")
    public List<Employee> getEmployees() {
        return this;
    }
}
