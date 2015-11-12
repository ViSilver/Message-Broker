package broker;


import org.json.JSONObject;
import iasyncio.FileIO;
import utils.Subscriber;
import iasyncio.NetworkIO;
import utils.Message;
import utils.MessageFile;
import utils.DeliveryConfirmationParameter;
import utils.MessageParameter;
import utils.PingParameter;
import utils.SubscribtionParameter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.XML;


public class Consumer implements Runnable {

    void subscribeApp(SubscribtionParameter params) {

        String appName = params.getAppID();
        String ip = params.getIp();
        int port = params.getPort();

        Subscriber rcvr = new Subscriber(appName, port, ip);
        try {
            this.subscribers.put(rcvr);
        } catch (InterruptedException ex) {
            Logger.getLogger(Consumer.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    synchronized void sendMessage(Message mess, NetworkIO netWriter) {
        String to = ((MessageParameter) mess.getParams()).getReceiverID();
        Iterator<Subscriber> it = this.subscribers.iterator();

        if (this.subscribers.isEmpty()) {
            System.out.println("The subs q is empty");
        }

        while (it.hasNext()) {
            Subscriber sub = it.next();
            System.out.println(sub.name);
            if (to.equals(sub.name)) {
                netWriter.setPort(sub.port);
                netWriter.write(to, mess);
                System.out.println("Resending the message to " + to);
                return;
            }
        }

        // save the message into a file
        System.out.println("The receiver wasn't found.");
    }

    void pingResponse(String data) {
        
    }

    void changeMessDelivStatus(DeliveryConfirmationParameter param) {

        String messID = param.getMessageID();
        String senderID = param.getSenderID();

        Iterator<MessageFile> it = messFiles.iterator();

        while (it.hasNext()) {
            MessageFile messFile = it.next();
            MessageParameter messParam = messFile.getParams();

            if (messParam.getSenderID().equals(senderID)
                    && messParam.getMessID().equals(messID)) {
                messFile.setDelivered(true); // this might not work
                // remove messFile from queue
                // add the new messFile to queue
            }
        }
    }

    private BlockingQueue<Message> queMessage;
    private BlockingQueue<Subscriber> subscribers;
    private BlockingQueue<MessageFile> messFiles;
    private ExecutorService fileWriter = Executors.newCachedThreadPool();
    private boolean replica;

    Consumer(BlockingQueue q, BlockingQueue subs, BlockingQueue mF, boolean replica) {
        queMessage = q;
        subscribers = subs;
        messFiles = mF;
        this.replica = replica;
    }

    @Override
    public void run() {
        Message message = new Message();
        MessageParameter messParam;
        DeliveryConfirmationParameter delivConfParam;
        SubscribtionParameter subscribParam;
        PingParameter pingParam;
        NetworkIO netWriter = new NetworkIO();
        FileIO fileWrite = new FileIO();

        while (true) {

            if (!replica) {
                try {
                    message = queMessage.take();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    message = queMessage.poll(30, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {
                    System.out.println("Master is not responding. "
                            + "Switching to master's channel");
                    netWriter.setPort(2999);
                    message.setType("change_port");
                    netWriter.write("localhost", message);
                }
            }

            switch (message.getType()) {
                case "subscribe":
                    subscribParam = (SubscribtionParameter) message.getParams();
                    subscribeApp(subscribParam);
                    break;

                case "mess":
                    messParam = (MessageParameter) message.getParams();
                    System.out.println("Received a message: " + messParam.getMessID());
                    sendMessage(message, netWriter);

                    Message confirmation = new Message();
                    confirmation.setType("deliv_conf");

                    DeliveryConfirmationParameter confParam = new DeliveryConfirmationParameter();
                    confParam.setMessageID(messParam.getMessID());
                    confParam.setSenderID(messParam.getSenderID());
                    confirmation.setParams(confParam);

                    {
                        try {
                            queMessage.put(confirmation);

                        } catch (InterruptedException ex) {
                            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    MessageFile messFile = new MessageFile();
                    messFile.setParams(messParam);
                    messFile.setDelivered(false);

                    final String filePath = "src/broker/" + messParam.getSenderID()
                            + "_" + messParam.getMessID() + ".json";

                    Message copyMessage = message;
                    String body = message.getBody();
                    
                    try {
                        JSONObject xmlJSONObj = XML.toJSONObject(body);
                        body = xmlJSONObj.toString(4);
                        System.out.println(body);
                    } catch (JSONException je) {
                        System.out.println(je.toString());
                    }
                    
                    copyMessage.setBody(body);
                    final Message finalMessage = copyMessage;

                    Callable<Void> callable;
                    callable = new Callable<Void>() {

                        @Override
                        public Void call() throws Exception {

                            fileWrite.write(filePath, finalMessage);
                            return null;

                        }
                    };

                    Future<Void> futureFile = fileWriter.submit(callable);

                    messFile.setFilePath(filePath);
                    messFile.setFileWrite(futureFile);
                    break;

                case "ping":
                    // the broker replicat sends a ping
                    // to see if this instance is working
                    pingParam = (PingParameter) message.getParams();
                    pingResponse(pingParam.getSenderID());
                    break;

                case "pong": // receives a pong from the main broker
                {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

                case "deliv_conf":
                    // received a delivery confirmation
                    // from app about a sent message
                    DeliveryConfirmationParameter param = (DeliveryConfirmationParameter) message.getParams();

                    if (!param.getSenderID().equals("Broker")) {
                        System.out.println("Deliver confirmation for: " + param.getMessageID());
                        sendMessage(message, netWriter);
                    } else {
                        System.out.println("The message " + param.getMessageID() + " was delivered");
                    }
                    changeMessDelivStatus(param); 
                    break;

                default:
                    break;
            }
        }
    }
}
