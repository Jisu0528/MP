package com.example.mp;

public class Diary {
    private long id;
    private String date;
    private String content;
    private String picture;
    private String temperature;
    private String rainType;
    private String sky;

    public Diary() {}

    public Diary(String date, String content, String picture, String temperature, String rainType, String sky) {
        this.date = date;
        this.content = content;
        this.picture = picture;
        this.temperature = temperature;
        this.rainType = rainType;
        this.sky = sky;
    }

    public long getId() {return id;}
    public String getContent() {return content;}
    public String getDate() {return date;}
    public String getPicture() {return picture;}
    public String getRainType() {return rainType;}
    public String getSky() {return sky;}
    public String getTemperature() {return temperature;}

    public void setContent(String content) {this.content = content;}
    public void setDate(String date) {this.date = date;}
    public void setId(long id) {this.id = id;}
    public void setPicture(String picture) {this.picture = picture;}
    public void setRainType(String rainType) {this.rainType = rainType;}
    public void setSky(String sky) {this.sky = sky;}
    public void setTemperature(String temperature) {this.temperature = temperature;}
}