package org.team6204.frc.datarecorder;

import java.util.List;
import java.util.Iterator;
import java.util.function.Consumer;

public class Player<T> {
    private Iterator<T> dataStream;
    private Consumer<T> outputFunction;
    private String name = "";

    public Player(List<T> data) {
        dataStream = data.iterator();
    }

    public Player(List<T> data, Consumer<T> function) {
        this(data);
        setOutputFunction(function);
    }

    public void setOutputFunction(Consumer<T> function) {
        outputFunction = function;
    }

    /**
     * Throws NullPointerException if outputFunction is null.
     */
    public void play() {
        if (dataStream.hasNext()) {
            outputFunction.accept(dataStream.next());
        }
    }

    public boolean hasNext() {
        return dataStream.hasNext();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
