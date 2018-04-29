package org.team6204.frc.datarecorder;

import java.time.Instant;
import java.io.IOException;

// A 'test' is the process of executing an action and recording data as a result of that action
public abstract class Test {
    public final long DEFAULT_PERIOD = 20;

    private final Type type;
    private final ConditionType conType;
    protected DataRecorder recorder;
    protected Tester tester;
    protected Thread testerThread;
    protected long period;

    public enum Type {
      Simple,
      Periodic;
    }

    public enum ConditionType {
        Timeout,
        Boolean,
    }

    protected Test(Type t, ConditionType ct, DataRecorder r) {
        type = t;
        conType = ct;
        recorder = r;
        setPeriod(DEFAULT_PERIOD);
    }

    public void test() {
        testerThread = new Thread(tester);
        testerThread.start();
    }

    public void save(String path) throws IOException {
        recorder.save(path);
    }

    public void reset() {
        try {
            testerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("tester thread interrupted while trying to join");
        }
        recorder.clear();
    }

    public Type getType() {
        return type;
    }

    public ConditionType getConditionType() {
        return conType;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long p) {
        period = p;
        recorder.setSamplingPeriod(period);
    }

    public boolean isRunning() {
        return testerThread.isAlive();
    }

    public long getTime() {
        return tester.getTime();
    }

    protected abstract class Tester implements Runnable {
        private DataRecorder recorder;
        private long time, startTime;

        protected Tester(DataRecorder _recorder) {
            recorder = _recorder;
        }

        protected void startTesting() {
            startTime = Instant.now().toEpochMilli();
            recorder.startRecording();
        }

        protected void stopTesting() {
            time = Instant.now().toEpochMilli() - startTime;
            recorder.stopRecording();
        }

        public long getTime() {
            return time;
        }
    }

}
