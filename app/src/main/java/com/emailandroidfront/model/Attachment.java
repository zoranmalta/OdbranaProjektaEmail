package com.emailandroidfront.model;

public class Attachment {
    private long id;
    private String data;
    private String type;
    private String name;
    private Long messageId;

    public Attachment(){}

    public Attachment(long id, String data, String type,Long messageId, String name) {
        this.id = id;
        this.data = data;
        this.type = type;
        this.name = name;
        this.messageId=messageId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMessage() {
        return messageId;
    }

    public void setMessage(Long message) {
        this.messageId = messageId;
    }
}
