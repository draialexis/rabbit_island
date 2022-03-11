import java.io.*;

public class FileStuff
{
    static void createFile(String fileName)
    {
        try
        {
            File myObj = new File(fileName);
            if (myObj.createNewFile())
            {
                System.out.println("File created: " + myObj.getName());
            }
            else// TODO remove before shipping
            {// TODO remove before shipping
                throw new RuntimeException("file already exists");// TODO remove before shipping
            }// TODO remove before shipping
        } catch (IOException e)
        {
            System.out.println("file creating error");
            e.printStackTrace();
        }
    }

    static void writeToFile(String fileName, String s)
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
