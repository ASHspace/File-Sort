package com.ashraf.filesort;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;


public class IntegerSort {
    static int ItemCount = 150000000; // item count for input file, default value generates file size: 924.2 MB (924,156,859 bytes)
    static int MemoryLimit = 17000000; // temp file limit for memory size, default value runs within -Xmx100m constraint, temp file size: 104.7 MB (104,740,574 bytes)
    
    public static void main(String[] args)
    {
        String fileName = randomInput(ItemCount); // input file generator
        mainSort(fileName); // sorted output file
    }
    
    static String randomInput(int n)
    {
        String inputFile = "input-list.txt";
        Random random = new Random();
  
        try
        {
            FileWriter fwriter = new FileWriter(inputFile);
            PrintWriter pwriter = new PrintWriter(fwriter);
   
            for (int i = 0; i < n; i++)
                pwriter.println(random.nextInt(32767 + 1 + 32768) - 32768);
            
            pwriter.close();
        }
  
        catch (IOException e)
        {
            System.out.println("IO Problem:\n");
            e.printStackTrace();
        }
  
        return inputFile;  
    }
    
    public static void mainSort(String inputFile)
    {
        String ftemp = "ftemp_";
        String loc = "~/temp/"; // temp directory location, if necessary
        
        int[] mbuffer = new int [MemoryLimit < ItemCount ? MemoryLimit : ItemCount];
        
        try
        {
            FileReader freader = new FileReader(inputFile);
            BufferedReader breader = new BufferedReader(freader);
            int i = 0, j = 0;
            int partition = (int) Math.ceil((double) ItemCount/MemoryLimit);
            
            for(i = 0; i<partition; i++)
            {
                // split to temporary file
                for(j = 0; j< (MemoryLimit < ItemCount ? MemoryLimit : ItemCount); j++)
                {
                    String cline = breader.readLine();
                    if(cline != null)
                        mbuffer[j] = Integer.parseInt(cline);
                    else
                        break;                        
                }
                
                // sort temporary partition elements
                Arrays.sort(mbuffer);
                
                
                // write sorted elements to temporary file
                FileWriter fwriter = new FileWriter(ftemp + Integer.toString(i) + ".txt");
                PrintWriter pwriter = new PrintWriter(fwriter);
                
                for(int k = 0; k<j; k++)
                    pwriter.println(mbuffer[k]);
                
                pwriter.close();
                fwriter.close();
            }
            
            breader.close();
            freader.close();
            
            // open each temp file and read element
            int[] fnumber = new int[partition];
            BufferedReader[] pbr = new BufferedReader[partition];
            
            for(i = 0; i<partition; i++)
            {
                pbr[i] = new BufferedReader(new FileReader(ftemp + Integer.toString(i) + ".txt"));
                String pline = pbr[i].readLine();
                
                if(pline != null)
                    fnumber[i] = Integer.parseInt(pline);
                else
                    fnumber[i] = Integer.MAX_VALUE;
            }
            
            // write loweest vakue to the output
            FileWriter fwriter = new FileWriter("output-list.txt");
            PrintWriter pwriter = new PrintWriter(fwriter);
            
            for(i = 0; i<ItemCount; i++)
            {
                int mnum = fnumber[0];
                int mloc = 0;
                
                for(j = 0; j<partition; j++)
                {
                    if(mnum > fnumber[j])
                    {
                        mnum = fnumber[j];
                        mloc = j;
                    }
                }
                
                pwriter.println(mnum);
                
                String nline = pbr[mloc].readLine();
                
                if(nline != null)
                    fnumber[mloc] = Integer.parseInt(nline);
                else
                    fnumber[mloc] = Integer.MAX_VALUE;
            }
            
            for(i=0; i<partition; i++)
                pbr[i].close();
            
            pwriter.close();
            fwriter.close();
            
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File Problem:\n");
            e.printStackTrace();
        }
        catch(IOException e)
        {
            System.out.println("IO Problem:\n");
            e.printStackTrace();
        }
    }
    
}
