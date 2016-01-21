package warehouse;

import common.Employee;

/**
 * Created by Vi
 */
public class EmployeeStorage extends DataStorage<Employee> {
    private EmployeeStorage() {
        super();
    }

    public static synchronized EmployeeStorage getInstance() {
        if (instance == null) {
            instance = new EmployeeStorage();
        }
        return instance;
    }

    private static EmployeeStorage instance;
}
