package io.dangernoodle.grt.statuscheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.repository.RepositoryBuilder;
import io.dangernoodle.grt.util.JsonTransformer;


public class RepositoryStatusCheckTest
{
    private String branchName;

    private RepositoryBuilder builder;

    private Repository repository;

    private Collection<String> result;

    private RepositoryStatusCheck statusCheck;

    @BeforeEach
    public void beforeEach() throws Exception
    {
        statusCheck = new RepositoryStatusCheck();
        builder = new RepositoryBuilder(new JsonTransformer());

        builder.setName("grt-test-repository")
               .setOrganization("organization");
    }

    @Test
    public void testBranchWithNoProtection()
    {
        givenABranchWithNoProtection();
        whenGetStatusCheck();
        thenStatusCheckIsEmptyList();
    }

    @Test
    public void testBranchWithProtection()
    {
        givenABranchWithProtection();
        whenGetStatusCheck();
        thenStatusCheckIsReturned();
    }

    @Test
    public void testGetCommands()
    {
        assertEquals(1, statusCheck.getCommands().size());
        assertTrue(statusCheck.getCommands().contains("repository"));
    }

    private void givenABranchWithNoProtection()
    {
        branchName = "other";
    }

    private void givenABranchWithProtection()
    {
        branchName = "master";
        builder.addRequiredContext(branchName, "grt-test-repository");
    }

    private void thenStatusCheckIsEmptyList()
    {
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void thenStatusCheckIsReturned()
    {
        assertEquals(1, result.size());
        assertTrue(result.contains("grt-test-repository"));
    }

    private void whenGetStatusCheck()
    {
        repository = builder.build();
        result = statusCheck.getRequiredChecks(branchName, repository);
    }
}
