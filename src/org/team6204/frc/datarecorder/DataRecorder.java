package org.team6204.frc.datarecorder;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;
import java.io.IOException;

public class DataRecorder {
    private List<Recorder<Double>> doubleRecords = Collections.synchronizedList(new ArrayList<Recorder<Double>>());
    private List<Recorder<String>> stringRecords = Collections.synchronizedList(new ArrayList<Recorder<String>>());
    private List<Recorder<Boolean>> booleanRecords = Collections.synchronizedList(new ArrayList<Recorder<Boolean>>());
    private List<Recorder<List<Double>>> doubleArrayRecords = Collections.synchronizedList(new ArrayList<Recorder<List<Double>>>());
    private List<Recorder<List<String>>> stringArrayRecords = Collections.synchronizedList(new ArrayList<Recorder<List<String>>>());
    private List<Recorder<List<Boolean>>> booleanArrayRecords = Collections.synchronizedList(new ArrayList<Recorder<List<Boolean>>>());

    private Timer timer;

    private boolean recording;

    /**
     * Sampling rate in hertz
     */
    private long samplingRate = 50;

    public void recordDouble(String field, Supplier<Double> function) {
        doubleRecords.add(new Recorder<Double>(field, function));
    }

    public void recordString(String field, Supplier<String> function) {
        stringRecords.add(new Recorder<String>(field, function));
    }

    public void recordBoolean(String field, Supplier<Boolean> function) {
        booleanRecords.add(new Recorder<Boolean>(field, function));
    }

    public void recordDoubleArray(String field, Supplier<List<Double>> function) {
        doubleArrayRecords.add(new Recorder<List<Double>>(field, function));
    }

    public void recordStringArray(String field, Supplier<List<String>> function) {
        stringArrayRecords.add(new Recorder<List<String>>(field, function));
    }

    public void recordBooleanArray(String field, Supplier<List<Boolean>> function) {
        booleanArrayRecords.add(new Recorder<List<Boolean>>(field, function));
    }

    public void record() {
        recordAll(doubleRecords);
        recordAll(stringRecords);
        recordAll(booleanRecords);
        recordAll(doubleArrayRecords);
        recordAll(stringArrayRecords);
        recordAll(booleanArrayRecords);
    }

    public void startRecording() {
        recording = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                record();
            }
        }, 0, 1000 * 1/samplingRate);
    }

    public void stopRecording() {
        recording = false;
        timer.cancel();
    }

    public void save(String filename) throws IOException {
        CSVWriter writer = new CSVWriter();
        writeAll(writer, doubleRecords);
        writeAll(writer, stringRecords);
        writeAll(writer, booleanRecords);
        writeAll(writer, doubleArrayRecords);
        writeAll(writer, stringArrayRecords);
        writeAll(writer, booleanArrayRecords);

        writer.write(filename);
    }

    public List<?> getRecords(DataType type) {
        switch (type) {
        case Double:
            return doubleRecords;
        case DoubleArray:
            return doubleArrayRecords;
        case String:
            return stringRecords;
        case StringArray:
            return stringArrayRecords;
        case Boolean:
            return booleanArrayRecords;
        case BooleanArray:
            return booleanArrayRecords;
        default:
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unchecked")
    public Recorder<?> getRecorderByName(DataType type, String name) {
        for (Recorder<?> recorder : (List<Recorder<?>>) getRecords(type)) {
            if (recorder.getName().equals(name)) {
                return recorder;
            }
        }
        throw new IllegalArgumentException();
    }

    public double getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(long rate) {
        samplingRate = rate;
    }

    public boolean isRecording() {
        return recording;
    }

    private void recordAll(List<? extends Recorder<?>> list) {
        for (Recorder<?> recorder : list) {
            recorder.record();
        }
    }

    private void writeAll(CSVWriter writer, List<? extends Recorder<?>> list) {
        for (Recorder<?> recorder : list) {
            writer.addField(recorder.getName(), (String[]) recorder.getRecord().stream().map(Object::toString).toArray(String[]::new));
        }
    }

}
