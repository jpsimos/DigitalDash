package psimos.jacob.dashboard;

import java.util.ArrayList;

/**
 * Created by jacob on 3/2/2017.
 */

public class MPGAverager{

    private int capacity = 10000;
    private double sum = 0.0;
    private int count = 0;

    public MPGAverager(final int capacity){
        this.capacity = capacity;
    }

    public boolean addDouble(final double val){
        if(count < capacity){
            sum += val;
            count++;
            return true;
        }
        return false;
    }

    public final double getAverage(){
        double average = sum / (double)count;
        return average;
    }

    public void reset(){
        count = 0;
        sum = 0.0;
    }
}