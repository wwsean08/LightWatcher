package com.wwsean08.LightWatcher;

import com.google.gson.Gson;
import com.wwsean08.LightWatcher.pojo.DataModel;
import com.wwsean08.LightWatcher.pojo.FileHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main
{

    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("To execute this please include a path to the JSON configuration file");
            System.exit(-1);
        }
        DataModel DM = parseConfigFile(args[0]);

        ExecutorService executor = Executors.newFixedThreadPool(DM.getFiles().length);
        for(FileHolder FH : DM.getFiles())
        {
            File file = new File(FH.getFilePath());
            if (!file.exists())
            {
                System.out.println("The file to watch needs to exist already. Unable to find " + FH.getFilePath());
                continue;
            }
            LightWatcherThread thread = new LightWatcherThread(FH);
            executor.submit(thread);
        }
    }

    private static DataModel parseConfigFile(String file)
    {
        Gson GSON = new Gson();
        DataModel DM = null;
        try
        {
            FileReader reader = new FileReader(new File(file));
            DM = GSON.fromJson(reader, DataModel.class);
            System.out.println(DM.getFiles());
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return DM;
    }
}
