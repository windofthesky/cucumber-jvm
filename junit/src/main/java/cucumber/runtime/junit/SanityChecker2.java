package cucumber.runtime.junit;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Listener that makes sure Cucumber fires events in the right order
 */
public class SanityChecker2 extends RunListener {
    private static final String INDENT = "  ";
    public static final String INSANITY = "INSANITY";

    private List<Description> tests = new ArrayList<Description>();
    private Description suiteDescription;
    private final StringWriter out = new StringWriter();

    public static void run(Class<?> testClass) {
        run(testClass, false);
    }

    public static void run(Class<?> testClass, boolean debug) {
        JUnitCore core = new JUnitCore();
        SanityChecker2 listener = new SanityChecker2();
        core.addListener(listener);
        core.run(testClass);
        String output = listener.getOutput();
        if (output.contains(INSANITY)) {
            throw new RuntimeException("Something went wrong\n" + output);
        }
        if (debug) {
            System.out.println("===== " + testClass.getName());
            System.out.println(output);
            System.out.println("=====");
        }
    }

    @Override
    public void testFinished(Description description) throws Exception {
        try {
            Description lastStarted = tests.remove(tests.size() - 1);
            spaces();
            out.append("END   " + description.getDisplayName()).append("\n");
            if (!lastStarted.equals(description)) {
                out.append(INSANITY).append("\n");
                String errorMessage = String.format("Started : %s\nEnded   : %s\n", lastStarted.getDisplayName(), description.getDisplayName());
                out.append(errorMessage).append("\n");
            }
        } catch (Exception e) {
            out.append(INSANITY).append("\n");
            e.printStackTrace(new PrintWriter(out));
        }
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        suiteDescription = description;
        out.append("ABOUT TO START TEST SUITE").append("\n");
    }

    @Override
    public void testStarted(Description description) throws Exception {
        spaces();
        out.append("START " + description.getDisplayName()).append("\n");
        tests.add(description);
        checkDescriptionInSuiteDescription(description);
    }

    private void spaces() {
        for (int i = 0; i < tests.size(); i++) {
            out.append(INDENT);
        }
    }

    public String getOutput() {
        return out.toString();
    }

    private void checkDescriptionInSuiteDescription(Description description) {
        boolean result = checkDescriptionInSuiteDescription(suiteDescription, description);
        if (!result) {
            out.append(INSANITY).append("\n");
            String errorMessage = String.format("Could not locate : %s\n", description.getDisplayName());
            out.append(errorMessage).append("\n");
        }
    }

    private boolean checkDescriptionInSuiteDescription(Description suiteDescription, Description description) {
        if (suiteDescription.equals(description)) {
            for (int i = 0; i < tests.size() - 1; i++) {
                out.append(INDENT);
            }
            out.append(String.format("FOUND %s\n", description.getDisplayName()));
            return true;
        }
        if (suiteDescription.isTest()) {
            return false;
        }
        for (Description childDescription : suiteDescription.getChildren()) {
            boolean result = checkDescriptionInSuiteDescription(childDescription, description);
            if (result) {
                return true;
            }
        }
        return false;
    }
}
