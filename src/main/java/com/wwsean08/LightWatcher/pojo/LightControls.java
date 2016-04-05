package com.wwsean08.LightWatcher.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * {
 * "keyword": "red",
 * "command": "C:\\Users\\wwsea_000\\Desktop\\light_controls\\RedOn.bat",
 * "storeColor": true,
 * "revertColor": false
 * },
 * <p>
 * Created by wwsea_000 on 4/3/2016.
 */
public class LightControls
{
    @SerializedName("keyword")
    private String key;
    @SerializedName("command")
    private String command;
    @SerializedName("storeColor")
    private boolean storeColor;
    @SerializedName("revertColor")
    private boolean revertColor;

    public String getKey()
    {
        return key;
    }

    public String getCommand()
    {
        return command;
    }

    public boolean isStoreColor()
    {
        return storeColor;
    }

    public boolean isRevertColor()
    {
        return revertColor;
    }
}
