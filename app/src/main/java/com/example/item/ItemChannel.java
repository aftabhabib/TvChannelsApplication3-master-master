package com.example.item;

public class ItemChannel {

    private String Json;

    public String getJson() {
        return Json;
    }

    public void setJson(String json) {
        Json = json;
    }

    private int id;
    private String ChannelUrl;
    private String Image;
    private String ChannelName;
    private String Description;
    private boolean isTv;

    public ItemChannel() {
        // TODO Auto-generated constructor stub
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelUrl() {
        return ChannelUrl;
    }

    public void setChannelUrl(String url) {
        this.ChannelUrl = url;
    }


    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String channelname) {
        this.ChannelName = channelname;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String desc) {
        this.Description = desc;
    }

    public boolean isTv() {
        return isTv;
    }

    public void setIsTv(boolean flag) {
        this.isTv = flag;
    }

}
