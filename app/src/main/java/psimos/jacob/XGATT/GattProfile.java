package psimos.jacob.XGATT;

import java.util.ArrayList;

/**
 * Created by jacob on 2/25/2017.
 */

public class GattProfile {
    protected String uuid = "";
    protected String description = "";
    protected char rxType = ' ';
    protected int notificationBit = 0;
    protected int valueLength = 0;
    protected int priority = 0;

    public GattProfile(final String uuid, final char rxType, final int notificationBit, final int priority, final int valueLength, final String description){
        this.uuid = uuid;
        this.rxType = rxType;
        this.description = description;
        this.notificationBit = notificationBit;
        this.valueLength = valueLength;
        this.priority = priority;
    }


    public String getUuid(){
        return uuid;
    }

    public char getRXType(){
        return rxType;
    }

    public int getNotificationBit(){
        return notificationBit;
    }

    public int getValueLength(){
        return valueLength;
    }

    public int getPriority(){
        return priority;
    }

    public String getDescription(){
        return description;
    }
}
