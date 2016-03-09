package com.wwsean08.LightWatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Created by wwsea_000 on 3/8/2016.
 */
public class LightWatcherThread implements Runnable
{

    private final File file;

    public LightWatcherThread(File file)
    {
        this.file = file;
    }

    @Override
    public void run()
    {
        try
        {
            File dir = file;
            System.out.println("Watching directory: " + dir.getAbsolutePath());
            WatchService watcher = file.toPath().getFileSystem().newWatchService();
            dir.toPath().register(watcher, ENTRY_MODIFY, ENTRY_CREATE);
            while (true)
            {
                WatchKey key;
                try
                {
                    key = watcher.take();
                }
                catch (InterruptedException ex)
                {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents())
                {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    if (kind == ENTRY_MODIFY &&
                            fileName.toString().equals("light.txt"))
                    {
                        String lastLine = "";
                        String currentLine;
                        BufferedReader br = new BufferedReader(new FileReader(this.file + File.separator + fileName.toString()));
                        while ((currentLine = br.readLine()) != null)
                        {
                            lastLine = currentLine;
                        }
                        if (lastLine.contains("red"))
                        {
                            setColor("C:\\Users\\wwsea_000\\Desktop\\light_controls\\RedOn.bat");
                        } else if (lastLine.contains("green"))
                        {
                            setColor("C:\\Users\\wwsea_000\\Desktop\\light_controls\\GreenOn.bat");
                        } else if (lastLine.contains("blue"))
                        {
                            setColor("C:\\Users\\wwsea_000\\Desktop\\light_controls\\BlueOn.bat");
                        } else if (lastLine.contains("off"))
                        {
                            setColor("C:\\Users\\wwsea_000\\Desktop\\light_controls\\AllOff.bat");
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid)
                {
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setColor(String script)
    {
        try
        {
            Runtime.getRuntime().exec(script);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
