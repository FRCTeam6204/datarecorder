package org.team6204.frc.datarecorder;

import org.team6204.frc.datarecorder.testable.Testable;
import java.util.function.BooleanSupplier;

public class SimpleTest extends Test {

    private SimpleTest(Test.ConditionType t, DataRecorder _recorder) {
        super(Type.Simple, t, _recorder);
    }

    public SimpleTest(Runnable _startup, Runnable _teardown, BooleanSupplier _condition, DataRecorder _recorder) {
      this(ConditionType.Boolean, _recorder);
      tester = new BooleanTester(_recorder, _startup, _teardown, _condition);
    }

    public SimpleTest(Runnable _startup, Runnable _teardown, long _timeout, DataRecorder _recorder) {
      this(ConditionType.Timeout, _recorder);
      tester = new TimeoutTester(_recorder, _startup, _teardown, _timeout);
    }

    public SimpleTest(Testable.SimpleTestableBoolean t) {
        this(t::startup, t::teardown, t::condition, t.getDataRecorder());
    }

    public SimpleTest(Testable.SimpleTestableTimeout t) {
        this(t::startup, t::teardown, t.getTimeout(), t.getDataRecorder());
    }

    private class TimeoutTester extends Test.Tester  {
        private Runnable startup, teardown;
        private long timeout;

        public TimeoutTester(DataRecorder recorder, Runnable _startup, Runnable _teardown, long _timeout) {
            super(recorder);
            startup = _startup;
            teardown = _teardown;
            timeout = _timeout;
        }

        public void run() {
            startTesting();
            startup.run();
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException("thread interrupted during sleep()");
            }
            teardown.run();
            stopTesting();
        }

    }

    private class BooleanTester extends Test.Tester {
        private Runnable startup, teardown;
        private BooleanSupplier condition;

        public BooleanTester(DataRecorder recorder, Runnable _startup, Runnable _teardown, BooleanSupplier _condition) {
            super(recorder);
            startup = _startup;
            teardown = _teardown;
            condition = _condition;
        }

        public void run() {
            startTesting();
            startup.run();
            while (!condition.getAsBoolean()) {
                try {
                    Thread.sleep(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException("thread interrupted during sleep()");
                }
            }
            teardown.run();
            stopTesting();
        }
    }
}
