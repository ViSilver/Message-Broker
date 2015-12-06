package serialization;


import common.Employee;
import common.Employees;

import java.util.ArrayList;
import java.util.List;

public interface Serializer {
    public void serialize(Employees employees, String filePath);
}
