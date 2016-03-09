package com.wwsean08.LightWatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.*;

public class Main
{

    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("To execute this please include a path to the file to watch");
            System.exit(-1);
        }

        File file = new File(args[0]);
        if (!file.exists())
        {
            System.out.println("The file to watch needs to exist already");
            System.exit(-1);
        }
        LightWatcherThread thread = new LightWatcherThread(file);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(thread);
    }
}
