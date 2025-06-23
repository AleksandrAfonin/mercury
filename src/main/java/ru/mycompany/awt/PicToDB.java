package ru.mycompany.awt;

import java.io.File;

public class PicToDB {
    public static void main(String[] args) {
        File path = new File(new File(".", "sprites"), "pic");
        SQLiteProvider.getInstance().picToDB(path, "seofast", Browser.CHROME.name(), "visits");

    }
}
