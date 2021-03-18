package com.zetcode.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FileLogWrite {

    public static void FileWrite(String message, String fileName){
        try{
            Calendar cal = Calendar.getInstance();
            Date date=cal.getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss", Locale.US);
            String formattedDate = dateFormat.format(date);
            FileWriter fstream = new FileWriter(fileName, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(formattedDate + "  -  " + message);
            out.newLine();
            if (message.equals("Exit"))
                out.newLine();
            out.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }
}
