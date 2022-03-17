package com.alexisdrai.util;

import java.io.*;

public class FileStuff
{
    public static void createFile(String fileName)
    {
        try
        {
            File myObj = new File(fileName);
            if (myObj.createNewFile())
            {
                System.out.println("File created: " + myObj.getName());
            }
            else
            {
                throw new RuntimeException("file already exists, please delete or remove them first");
            }
        } catch (IOException e)
        {
            System.out.println("file creating error");
            e.printStackTrace();
        }
    }

    public static void writeToFile(String fileName, String s)
    {
        try
                (
                        FileWriter fw = new FileWriter(fileName, true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw)
                )
        {
            out.println(s);
        } catch (IOException e)
        {
            System.out.println("file writing error");
            e.printStackTrace();
        }

    }
}
