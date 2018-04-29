package org.team6204.frc.datarecorder;

import org.team6204.frc.datarecorder.testable.Testable;
import java.util.function.BooleanSupplier;
import java.util.Timer;
import java.util.TimerTask;

public class PeriodicTest extends Test {

    private PeriodicTest(Test.ConditionType t, DataRecorder r) {
        super(Type.Periodic, t, r);
    }

    public PeriodicTest(Runnable periodicFunction, BooleanSupplier condition, DataRecorder _recorder) {
        this(ConditionType.Boolean, _recorder);
        tester = new BooleanTester(_recorder, periodicFunction, condition);
    }

    public PeriodicTest(Runnable periodicFunction, long timeout, DataRecorder _recorder) {
        this(ConditionType.Timeout, _recorder);
        tester = new TimeoutTester(_recorder, periodicFunction, timeout);
    }

    public PeriodicTest(Testable.PeriodicTestableBoolean t) {
        this(t::periodic, t::condition, t.getDataRecorder());
    }

    public PeriodicTest(Testable.PeriodicTestableTimeout t) {
        this(t::periodic, t.getTimeout(), t.getDataRecorder());
    }

    private class TimeoutTester extends Test.Tester {
        private Runnable periodicFunction;
        private long timeout;

        public TimeoutTester(DataRecorder recorder, Runnable _periodicFunction, long _timeout) {
            super(recorder);
            periodicFunction = _periodicFunction;
            timeout = _timeout;
        }

        public synchronized void run() {
            TimeoutTester ref = this;
            Timer periodicTimer = new Timer();
            Timer stopTimer = new Timer();
            TimerTask periodicTask = new TimerTask() {
                public void run() {
                    periodicFunction.run();
                }
            };
            TimerTask stopTask = new TimerTask() {
                public void run() {
                    periodicTimer.cancel();
                    stopTesting();
                    ref.notifyAll();
                }
            };

            startTesting();
            periodicTimer.schedule(periodicTask, 0, period);
            stopTimer.schedule(stopTask, timeout);
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException("thread interrupted during wait()");
            }
        }

    }

    private class BooleanTester extends Test.Tester {
        private Runnable periodicFunction;
        private BooleanSupplier condition;

        public BooleanTester(DataRecorder recorder, Runnable _periodicFunction, BooleanSupplier _condition) {
            super(recorder);
            periodicFunction = _periodicFunction;
            condition = _condition;
        }

        public synchronized void run() {
            BooleanTester ref = this;
            Timer periodicTimer = new Timer();
            TimerTask periodicTask = new TimerTask() {
                public void run() {
                    periodicFunction.run();
                }
            };
            startTesting();
            periodicTimer.schedule(periodicTask, 0, period);
            try {
                Thread.sleep(period);
                if (condition.getAsBoolean()) {
                    periodicTimer.cancel();
                    stopTesting();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("thread interrupted during wait()");
            }
        }
    }
}
