package asyncio;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class AsyncFile implements IAsyncIO {

    private String strFile = "";
    private ExecutorService executor;
    
    public AsyncFile(ExecutorService executor){
        this.executor = executor;
    }

    @Override
    public Future<String> asyncRead(String location) throws InterruptedException {
        
        System.out.println("Reading async from file");

        Future<String> data;
        data = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {

//                try {
//                    strFile = new Scanner(new File(location)).useDelimiter("\\Z").next();
//                    
//                    System.out.println(strFile);
//                    
//                    File inputFile = new File(location);
//
//                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//
//                    dbFactory.setIgnoringComments(true);
//                    dbFactory.setIgnoringElementContentWhitespace(true);
//                    dbFactory.setValidating(true);
//
//                    DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
//
//                    Document doc = (Document) dbBuilder.parse(inputFile);
//
//                    return doc;
//
//                } catch (ParserConfigurationException | SAXException | IOException ex) {
//                    System.out.println(ex.getMessage());
//                }
                try{
                    strFile = new Scanner(new File(location)).useDelimiter("\\Z").next();
                } catch (IOException ex) {
                    System.out.println(ex);
                }

                return strFile;
            }

            private String saveToString(Node n, String str) {
                
                if(n.getNodeType() == Node.TEXT_NODE) {
                    str += n.getNodeValue();
                    return str;
                }
                
                str += "<" + n.getNodeName(); 
                
                NamedNodeMap as = n.getAttributes();
                if(as != null && as.getLength() > 0) {
                    for (int i = 0; i < as.getLength(); i++) {
                        str += " " + as.item(i);
                    }
                }
                
                str += ">";
                
                NodeList children = n.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    str += saveToString(children.item(i), "");
                }
                
                str += "</" + n.getNodeName() + ">";
                
                return str;
            }
        });

//        Document xmlFile = getDocument(location);
        return data;
    }

    @Override
    public void asyncWrite(String file) {
    }
    
    @Override
    public void printString(){
        System.out.println(this.strFile);
    }
}
