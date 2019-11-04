package cn.edu.sysu.bookmanager.bookmanager.model;


import lombok.Data;

@Data
public class Book {
    private int id;
    private String name;
    private String author;
    private String price;
}
