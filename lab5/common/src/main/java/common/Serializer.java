package common;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Vi
 */
public interface Serializer {
    public abstract <T> void serialize(T object, OutputStream out) throws Exception;
    public abstract <T> T deserialize(InputStream in, Class<T> clazz) throws Exception;
}
