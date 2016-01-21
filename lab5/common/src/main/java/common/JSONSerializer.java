package common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Vi
 */
public class JSONSerializer implements Serializer {
    public <T> void serialize(T object, OutputStream out) throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.writerWithDefaultPrettyPrinter().writeValue(out, object);
    }

    public <T> T deserialize(InputStream in, Class<T> clas) throws Exception {
        ObjectMapper om = new ObjectMapper();
        return (T) om.readValue(in, clas);
    }
}
