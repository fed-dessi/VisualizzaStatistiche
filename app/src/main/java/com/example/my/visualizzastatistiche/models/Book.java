package com.example.my.visualizzastatistiche.models;

import java.util.Date;

public class Book {
    private String name;
    private Date date;
    private String metodo;
    private String author;
    private String year;
    private String editor;
    private int venditaID;



    public Book() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public int getVenditaID() {return venditaID;}

    public void setVenditaID(int venditaID) {this.venditaID = venditaID;}
}
