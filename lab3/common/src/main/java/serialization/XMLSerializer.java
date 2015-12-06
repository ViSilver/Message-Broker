package serialization;


import common.Employees;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class XMLSerializer implements Serializer {

    @Override
    public void serialize(Employees employees, String filePath) {

        try {

            File file = new File(filePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(Employees.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(employees, file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
