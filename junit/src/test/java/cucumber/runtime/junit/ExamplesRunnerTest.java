package cucumber.runtime.junit;

import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberScenarioOutline;
import org.junit.Test;
import org.junit.runner.Description;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ExamplesRunnerTest {

    @Test
    public void shouldCreateUniqueDescriptionForExampleScenariosWithSameDisplayName() throws Exception {
        CucumberFeature cucumberFeature = TestFeatureBuilder.feature("featurePath", "" +
                "Feature: feature name\n" +
                "  Scenario Outline:\n" +
                "    Given something\n" +
                "    Examples:\n" +
                "    | a | b |\n" +
                "    | 1 | 2 |\n" +
                "    | 1 | 2 |\n");

        ExamplesRunner runner = new ExamplesRunner(
                null,
                ((CucumberScenarioOutline)cucumberFeature.getFeatureElements().get(0)).getCucumberExamplesList().get(0),
                null
        );

        // fish out the data from runner
        Description runnerDescription = runner.getDescription();
        Description firstExamplesDescription = runnerDescription.getChildren().get(0);
        Description secondExamplesDescription = runnerDescription.getChildren().get(1);

        assertEquals(firstExamplesDescription.getDisplayName(), secondExamplesDescription.getDisplayName());
        assertNotEquals(firstExamplesDescription, secondExamplesDescription);
    }

}
