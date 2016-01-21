package warehouse;

import common.Employee;

/**
 * Created by Vi
 */
public class EmployeeDataAccessObject extends DataAccessObject<Employee> {
    private EmployeeDataAccessObject() {
        super();
    }

    public static synchronized EmployeeDataAccessObject getInstance() {
        if (instance == null) {
            instance = new EmployeeDataAccessObject();
        }
        return instance;
    }

    private static EmployeeDataAccessObject instance;
}
