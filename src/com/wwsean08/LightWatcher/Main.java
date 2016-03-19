package com.wwsean08.LightWatcher;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main
{

    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("To execute this please include a path to the file to watch");
            System.exit(-1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(args.length);
        for(String arg : args)
        {
            File file = new File(arg);
            if (!file.exists())
            {
                System.out.println("The file to watch needs to exist already. Unable to find " + arg);
            }
            LightWatcherThread thread = new LightWatcherThread(file);
            executor.submit(thread);
        }
    }
}
