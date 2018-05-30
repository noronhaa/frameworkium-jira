import com.frameworkium.jira.zapi.cycle.AddToCycleEntity;
import com.frameworkium.jira.zapi.cycle.Cycle;
import com.frameworkium.jira.zapi.cycle.CycleEntity;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class CycleUnitTests {

    @Test
    public void canRetrieveProjectIdFromProjectKey(){
        String projectKey = "TP";
        int expectedId = 16506;

        Cycle cycle = new Cycle();
        int actualId = cycle.getProjectIdByKey(projectKey);

        Assert.assertEquals(actualId,expectedId);
    }

    @Test
    public void canRetrieveSpecificVersionId(){
        //get all versions, iterate through results to find id of version by name

        int projectId = 16506;
        String versionName = "ARGON";
        String expectedVersionId = "83930";


        Cycle cycle = new Cycle();
        String actualVersionId = cycle.getVersionIdByName(projectId, versionName);

        Assert.assertEquals(actualVersionId, expectedVersionId);

    }


    @Test
    public void canRetrieveSpecificVersionIdUsingVersionNameWIthDecimal(){
        //get all versions, iterate through results to find id of version by name

        int projectId = 16506;
        String versionName = "GA 1.1 - 1.0";
        String expectedVersionId = "58758";


        Cycle cycle = new Cycle();
        String actualVersionId = cycle.getVersionIdByName(projectId, versionName);

        Assert.assertEquals(actualVersionId, expectedVersionId);

    }


    @Test
    public void successfullyCreatingCycleWillReturnAnID(){
        String versionId = "83930";
        String projectId = "16506";

        Cycle cycle = new Cycle();
        int cycleId;

        //build request body
        CycleEntity cycleEntity = new CycleEntity("Auto Test Cycle 1",projectId,versionId);

        //execute request
        cycleId = cycle.createNewCycle(cycleEntity);

        //Clean up and delete cycle
        cycle.deleteCycle(String.valueOf(cycleId));

    }


    @Test(dependsOnMethods = "successfullyCreatingCycleWillReturnAnID")
    public void canAddTestsToATestCycle(){
        List<String> issuesToAddToCycle = Collections.singletonList("TP-12601");
        String versionId = "83930";
        String projectId = "16506";

        Cycle cycle = new Cycle();
        int cycleId;

        //create cycle
        CycleEntity cycleEntity = new CycleEntity("Auto Test Cycle 1",projectId,versionId);
        cycleId = cycle.createNewCycle(cycleEntity);


        //Create addToCycleEntity
        AddToCycleEntity addToCycleEntity = new AddToCycleEntity(
                String.valueOf(cycleId),
                issuesToAddToCycle,
                "1",
                projectId,
                Integer.valueOf(versionId)
        );

        //add tests to cycle
        cycle.addTestsToCycle(addToCycleEntity);

        //Clean up and delete cycle
        cycle.deleteCycle(String.valueOf(cycleId));

    }

    @Test(dependsOnMethods = "canAddTestsToATestCycle")
    public void canAddTestToACycleThatIsAlreadyInTheCycleWithoutError(){
        List<String> issuesToAddToCycle = Collections.singletonList("TP-12601");
        String versionId = "83930";
        String projectId = "16506";

        Cycle cycle = new Cycle();
        int cycleId;

        //create cycle
        CycleEntity cycleEntity = new CycleEntity("Auto Test Cycle 1",projectId,versionId);
        cycleId = cycle.createNewCycle(cycleEntity);


        //Create addToCycleEntity
        AddToCycleEntity addToCycleEntity = new AddToCycleEntity(
                String.valueOf(cycleId),
                issuesToAddToCycle,
                "1",
                projectId,
                Integer.valueOf(versionId)
        );

        //add tests to cycle
        cycle.addTestsToCycle(addToCycleEntity);

        //add same test again and see what happens
        cycle.addTestsToCycle(addToCycleEntity);

        //Clean up and delete cycle
        cycle.deleteCycle(String.valueOf(cycleId));


    }

}
