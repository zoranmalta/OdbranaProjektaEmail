package com.emailandroidfront.model;

public class Message_Tag {

    private Long id;
    private Tag tag;

    public Message_Tag(Long id, Tag tag) {
        this.id = id;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
