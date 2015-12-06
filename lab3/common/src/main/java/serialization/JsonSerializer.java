package serialization;


import common.Employees;

import java.io.InputStream;
import java.io.OutputStream;

public class JsonSerializer implements Serializer{
    @Override
    public void serialize(Employees employees, OutputStream os) {

    }

    @Override
    public Employees deserialize(InputStream is) {
        return null;
    }
}
