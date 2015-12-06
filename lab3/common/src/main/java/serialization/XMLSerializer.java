package serialization;


import common.Employees;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;

public class XMLSerializer implements Serializer {

    @Override
    public void serialize(Employees employees, OutputStream os) {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Employees.class);

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(employees, os);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Employees deserialize(InputStream is) {

        Employees employees = null;

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Employees.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            employees = (Employees) jaxbUnmarshaller.unmarshal(is);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return employees;
    }
}
