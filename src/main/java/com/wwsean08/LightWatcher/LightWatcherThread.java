package com.wwsean08.LightWatcher;

import com.wwsean08.LightWatcher.pojo.FileHolder;
import com.wwsean08.LightWatcher.pojo.LightControls;

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
    private final FileHolder fileHolder;
    private final String PROPFILE;

    private final String COLOR = "color";

    private String prevFileData = "DEADBEEF";

    public LightWatcherThread(FileHolder fileHolder)
    {
        this.fileHolder = fileHolder;
        this.file = new File(fileHolder.getFilePath());
        this.PROPFILE = fileHolder.getPropertiesFile();
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
            if (dir.isFile())
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
                        currentLine = br.readLine();
                        if (currentLine != null)
                        {
                            line = currentLine;
                            if (fileHolder.isDeepValidation())
                            {
                                System.out.println("Deep validation entered");
                                if (prevFileData.equals("DEADBEEF"))
                                {
                                    System.out.println("Deep Validation first entry");
                                    prevFileData = line;
                                }
                                else if (!prevFileData.equals(line))
                                {
                                    System.out.println("Deep Validation different, prev: " + prevFileData + " current: " + line);
                                    prevFileData = line;
                                    parseLine(line);
                                }
                                else
                                {
                                    System.out.println("Deep Validation Same");
                                }
                            }
                            else
                            {
                                parseLine(line);
                            }
                        }
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
        LightControls control = fileHolder.getControls().get(line);
        if (control != null)
        {
            execCommand(control.getCommand());
            if (control.isRevertColor())
            {
                String originalColor = props.getProperty(COLOR, "off");
                parseLine(originalColor);
            }
            if (control.isStoreColor())
            {
                props.setProperty(COLOR, control.getKey());
            }

            try
            {
                OutputStream OS = new FileOutputStream(new File(PROPFILE));
                props.store(OS, "");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
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
