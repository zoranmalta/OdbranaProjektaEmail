package com.emailandroidfront.model;

import java.util.List;

//nasledjujemo Parcalable da bismo mogli da prenesemo ceo objekat pomocu intenta
//u drugu aktivnost
public class Message  {

    private long id;
    private String sendto;
    private String sendbc;
    private String sendcc;
    private long account_id;
    private String subject;
    private String content;
    private String date;
    private boolean seen;
    private List<Message_Tag> tags;
    private List<Attachment> attachments;
    private AccountUser accountDto;

    public Message(){}

    public Message(long id, String sendto, long account_id, String subject,String date,boolean seen,
                   String content, List<Message_Tag> tags,List<Attachment> attachments,
                   AccountUser accountDto) {
        this.id=id;
        this.sendto=sendto;
        this.account_id=account_id;
        this.subject=subject;
        this.content=content;
        this.date=date;
        this.seen=seen;
        this.tags=tags;
        this.attachments=attachments;
        this.accountDto=accountDto;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getSendto() {
        return sendto;
    }
    public void setSendto(String sendto) {
        this.sendto = sendto;
    }

    public long getAccount_id() {
        return account_id;
    }
    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Message_Tag> getTags() {
        return tags;
    }

    public void setTags(List<Message_Tag> tags) {
        this.tags = tags;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public AccountUser getAccountDto() {
        return accountDto;
    }

    public void setAccountDto(AccountUser accountDto) {
        this.accountDto = accountDto;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setSendbc(String sendbc) {
        this.sendbc = sendbc;
    }

    public void setSendcc(String sendcc) {
        this.sendcc = sendcc;
    }

    public String getSendbc() {
        return sendbc;
    }

    public String getSendcc() {
        return sendcc;
    }
}
