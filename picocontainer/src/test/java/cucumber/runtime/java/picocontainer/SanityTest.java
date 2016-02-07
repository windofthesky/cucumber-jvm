package cucumber.runtime.java.picocontainer;

import cucumber.runtime.junit.SanityChecker;
import cucumber.runtime.junit.SanityChecker2;
import org.junit.Test;

public class SanityTest {
    @Test
    public void reports_events_correctly_with_cucumber_runner() {
        SanityChecker.run(RunCukesTest.class, true);
    }
    @Test
    public void reports_events_correctly_with_junit_core_runner() {
        SanityChecker2.run(RunCukesTest.class, true);
    }
}
