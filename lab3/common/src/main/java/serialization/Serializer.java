package serialization;


import common.Employees;

import java.io.InputStream;
import java.io.OutputStream;

public interface Serializer {
    public void serialize(Employees employees, OutputStream os);
    public Employees deserialize(InputStream is);
}
