package Main.Algorithms.Other;

/**
 * Created by IMN on 10/1/2016.
 */
public class TimeIdPair implements Comparable<TimeIdPair>{
    public int id;
    public double time;

    public TimeIdPair(double time, int id){
        this.time = time;
        this.id = id;
    }

    @Override
    public int compareTo(TimeIdPair o) {
        return Double.compare(this.time, o.time);
    }

    @Override
    public String toString() {
        return String.format("(%d, %.1f)", id, time);
    }
}
