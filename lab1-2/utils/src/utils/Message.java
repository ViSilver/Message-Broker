package utils;

import java.io.Serializable;

public class Message implements Serializable {
    
    private String type;
    private String body;
    private Parameter params;
    
    public Message(){
        type = null;
        body = null;
        params = null;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setParams(Parameter params) {
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    public Parameter getParams() {
        return params;
    }
    
    @Override
    public String toString(){
        return this.body;
    }
}
