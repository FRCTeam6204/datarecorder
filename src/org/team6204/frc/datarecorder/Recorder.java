package org.team6204.frc.datarecorder;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;

public class Recorder<T>  {
    private String name;
    private List<T> record;
    private Supplier<T> supplier;

    public Recorder(String name, Supplier<T> supplier) {
        this.name = name;
        this.supplier = supplier;
        record = Collections.synchronizedList(new ArrayList<T>());
    }

    public void record() {
        record.add(supplier.get());
    }

    public String getName() {
        return name;
    }

    public List<T> getRecord() {
        return record;
    }
}
