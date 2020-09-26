package com.example.lageraho.classes;

public class Contacts {

    public String Name, Status, Image;

    public Contacts(){

    }

    public Contacts(String name, String status, String image) {
        Name = name;
        Status = status;
        Image = image;
    }

    public String getNames() {
        return Name;
    }

    public void setNames(String name) {
        Name = name;
    }

    public String getStatuss() {
        return Status;
    }

    public void setStatuss(String status) {
        Status = status;
    }

    public String getImages() {
        return Image;
    }

    public void setImages(String image) {
        Image = image;
    }
}
