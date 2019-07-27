package com.mineaurion.aurionvotelistener.sponge;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedRandom<T extends Object> {

    private class Entry {
        double accumulatedWeight;
        T object;
    }

    private List<Entry> entries = new ArrayList<>();
    private double accumaletedWeight;
    private Random rand = new Random();

    public void addEntry(T object, double weight){
        accumaletedWeight += weight;
        Entry e = new Entry();
        e.object = object;
        e.accumulatedWeight = accumaletedWeight;
        entries.add(e);
    }

    public T getRandom() {
        double r = rand.nextDouble() * accumaletedWeight;

        for(Entry entry: entries){
            if(entry.accumulatedWeight >= r){
                return entry.object;
            }
        }
        return null; //should only happen when no entries
    }
}
