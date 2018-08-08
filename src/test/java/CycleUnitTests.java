import com.frameworkium.jira.zapi.cycle.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class CycleUnitTests {

    @Test
    public void canRetrieveProjectIdFromProjectKey() {
        String projectKey = "TP";
        String expectedId = "16506";

        Cycle cycle = new Cycle();
        String actualId = cycle.getProjectIdByKey(projectKey);

        Assert.assertEquals(actualId, expectedId);
    }

    @Test(dependsOnMethods = "canRetrieveProjectIdFromProjectKey")
    public void canRetrieveSpecificVersionId() {
        //get all versions, iterate through results to find id of version by issueType

        Cycle cycle = new Cycle();


        String projectId = cycle.getProjectIdByKey("TP");
        String versionName = "ARGON";
        String expectedVersionId = "83930";


        String actualVersionId = cycle.getVersionIdByName(projectId, versionName);

        Assert.assertEquals(actualVersionId, expectedVersionId);
    }


    @Test(dependsOnMethods = "canRetrieveProjectIdFromProjectKey")
    public void canRetrieveSpecificVersionIdUsingVersionNameWIthDecimal() {
        //get all versions, iterate through results to find id of version by issueType

        Cycle cycle = new Cycle();


        String projectId = cycle.getProjectIdByKey("TP");
        String versionName = "GA 1.1 - 1.0";
        String expectedVersionId = "58758";

        String actualVersionId = cycle.getVersionIdByName(projectId, versionName);

        Assert.assertEquals(actualVersionId, expectedVersionId);
    }


    @Test
    public void successfullyCreatingCycleWillReturnAnID() {
        String versionId = "83930";
        String projectId = "16506";

        Cycle cycle = new Cycle();
        int cycleId;

        //build request body
        CycleEntity cycleEntity = new CycleEntity();
        cycleEntity.name = "Auto Test Cycle 1";
        cycleEntity.projectId = projectId;
        cycleEntity.versionId = versionId;

        //execute request
        cycleId = cycle.createNewCycle(cycleEntity);

        //Clean up and delete cycle
        cycle.deleteCycle(String.valueOf(cycleId));
    }


    @Test(dependsOnMethods = "successfullyCreatingCycleWillReturnAnID")
    public void canAddTestsToATestCycle() {
        List<String> issuesToAddToCycle = Collections.singletonList("TP-12601");
        String versionId = "83930";
        String projectId = "16506";

        Cycle cycle = new Cycle();
        int cycleId;

        //create cycle
        CycleEntity cycleEntity = new CycleEntity();
        cycleEntity.name = "Auto Test Cycle 1";
        cycleEntity.projectId = projectId;
        cycleEntity.versionId = versionId;
        cycleId = cycle.createNewCycle(cycleEntity);


        //Create addToCycleEntity
        AddToCycleEntity addToCycleEntity = new AddToCycleEntity();
        addToCycleEntity.cycleId = String.valueOf(cycleId);
        addToCycleEntity.issues = issuesToAddToCycle;
        addToCycleEntity.method = "1";
        addToCycleEntity.projectId = projectId;
        addToCycleEntity.versionId = Integer.valueOf(versionId);

        //add tests to cycle
        cycle.addTestsToCycle(addToCycleEntity);

        //Clean up and delete cycle
        cycle.deleteCycle(String.valueOf(cycleId));
    }

    @Test(dependsOnMethods = "canAddTestsToATestCycle")
    public void canAddTestToACycleThatIsAlreadyInTheCycleWithoutError() {
        List<String> issuesToAddToCycle = Collections.singletonList("TP-12601");
        String versionId = "83930";
        String projectId = "16506";

        Cycle cycle = new Cycle();

        //create cycle
        CycleEntity cycleEntity = new CycleEntity();
        cycleEntity.name = "Auto Test Cycle 1";
        cycleEntity.projectId = projectId;
        cycleEntity.versionId = versionId;
        int cycleId = cycle.createNewCycle(cycleEntity);


        //Create addToCycleEntity
        AddToCycleEntity addToCycleEntity = new AddToCycleEntity();
        addToCycleEntity.cycleId = String.valueOf(cycleId);
        addToCycleEntity.issues = issuesToAddToCycle;
        addToCycleEntity.method = "1";
        addToCycleEntity.projectId = projectId;
        addToCycleEntity.versionId = Integer.valueOf(versionId);

        //add tests to cycle
        cycle.addTestsToCycle(addToCycleEntity);

        //add same test again and see what happens
        cycle.addTestsToCycle(addToCycleEntity);

        //Clean up and delete cycle
        cycle.deleteCycle(String.valueOf(cycleId));
    }

}
