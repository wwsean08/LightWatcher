package com.wwsean08.LightWatcher.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wwsea_000 on 4/3/2016.
 */
public class FileHolder
{
    Map<String, LightControls> lightControlsMap = null;

    @SerializedName("file")
    String filePath;
    @SerializedName("propFile")
    String propertiesFile;
    @SerializedName("verifyContentChanged")
    boolean deepValidation;
    @SerializedName("controls")
    LightControls[] controls;

    public String getPropertiesFile()
    {
        return propertiesFile;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public boolean isDeepValidation()
    {
        return deepValidation;
    }

    public Map<String, LightControls> getControls()
    {
        if (lightControlsMap == null)
        {
            lightControlsMap = new HashMap<>();
            //Generate lightConrolsMap
            for (LightControls LC : controls)
            {
                lightControlsMap.put(LC.getKey(), LC);
            }
        }

        return lightControlsMap;
    }
}
