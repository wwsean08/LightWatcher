package com.wwsean08.LightWatcher;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Created by wwsea_000 on 3/8/2016.
 */
public class LightWatcherThread implements Runnable
{

    private final File file;
    private final Properties props;

    private final String COLOR = "color";
    private final String PROPFILE = "C:\\Users\\wwsea_000\\Desktop\\light_controls\\status.properties";

    public LightWatcherThread(File file)
    {
        this.file = file;
        System.out.println(file.getName());
        props = new Properties();
        try
        {
            InputStream IS = new FileInputStream(new File(PROPFILE));
            props.load(IS);
            parseLine(props.getProperty(COLOR, "off"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            File dir = file;
            System.out.println("Watching directory: " + dir.getAbsolutePath());
            if(dir.isFile())
            {
                dir = dir.getParentFile();
            }
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
                    ex.printStackTrace();
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents())
                {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    if (kind == ENTRY_MODIFY &&
                            fileName.toString().equals(file.getName()))
                    {
                        String line = "";
                        String currentLine;
                        BufferedReader br = new BufferedReader(new FileReader(this.file));
                        while ((currentLine = br.readLine()) != null)
                        {
                            line = currentLine;
                        }
                        parseLine(line);
                    }
                }

                boolean valid = key.reset();
                if (!valid)
                {
                    System.out.println("Key now invalid");
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void parseLine(String line)
    {
        System.out.println(line);
        if (line.contains("red"))
        {
            execCommand("C:\\Users\\wwsea_000\\Desktop\\light_controls\\RedOn.bat");
            props.setProperty(COLOR, "red");
        }
        else if (line.contains("green"))
        {
            execCommand("C:\\Users\\wwsea_000\\Desktop\\light_controls\\GreenOn.bat");
            props.setProperty(COLOR, "green");
        }
        else if (line.contains("blue"))
        {
            execCommand("C:\\Users\\wwsea_000\\Desktop\\light_controls\\BlueOn.bat");
            props.setProperty(COLOR, "blue");
        }
        else if (line.contains("off"))
        {
            execCommand("C:\\Users\\wwsea_000\\Desktop\\light_controls\\AllOff.bat");
            props.setProperty(COLOR, "off");
        }
        else if (line.contains("follow"))
        {
            execCommand("C:\\Users\\wwsea_000\\Desktop\\light_controls\\BlueFlash.bat");
            String originalColor = props.getProperty(COLOR, "off");
            System.out.println(originalColor);
            parseLine(originalColor);
        }
        else if (line.contains("police"))
        {
            execCommand("C:\\Users\\wwsea_000\\Desktop\\light_controls\\PoliceFlash.bat");
            String originalColor = props.getProperty(COLOR, "off");
            System.out.println(originalColor);
            parseLine(originalColor);
        }
        try
        {
            OutputStream OS = new FileOutputStream(new File(PROPFILE));
            props.store(OS, "");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void execCommand(String script)
    {
        try
        {
            ProcessBuilder PB = new ProcessBuilder(script);
            PB.redirectError(ProcessBuilder.Redirect.INHERIT);
            PB.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Process p = PB.start();
            p.waitFor();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
