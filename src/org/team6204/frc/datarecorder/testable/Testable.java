package org.team6204.frc.datarecorder.testable;

import org.team6204.frc.datarecorder.DataRecorder;

public final class Testable {
    private interface BaseTestable {
        public DataRecorder getDataRecorder();
    }

    private interface SimpleTestable extends BaseTestable {
        public void startup();
        public void teardown();
    }

    private interface PeriodicTestable extends BaseTestable {
        public void periodic();
    }

    private interface BooleanTestable {
        public boolean condition();
    }

    private interface TimeoutTestable {
        public long getTimeout();
    }

    public interface SimpleTestableBoolean extends SimpleTestable, BooleanTestable {}

    public interface SimpleTestableTimeout extends SimpleTestable, TimeoutTestable {}

    public interface PeriodicTestableBoolean extends PeriodicTestable, BooleanTestable {}

    public interface PeriodicTestableTimeout extends PeriodicTestable, TimeoutTestable {}
}
