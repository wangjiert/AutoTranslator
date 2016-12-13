package com.konka.autotranslator;

import java.io.*;
public class Test{
    public static void main(String[] args) {
        File file = new File(".");
        String[] files = file.list(new FilenameFilter(){
            public boolean accept(File dir, String name){
                if(name.equalsIgnoreCase("english")) {
                    return true;
                }
                return false;
            }
        });
        System.out.println(files==null);
    }
}

