package io.dangernoodle.grt.extensions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.TestFiles;
import io.dangernoodle.grt.Repository;


public class DefaultStatusCheckFactoryTest
{
    private String branchName;

    private DefaultStatusCheckFactory factory;

    private Repository repository;

    private Collection<String> result;

    @BeforeEach
    public void beforeEach()
    {
        factory = new DefaultStatusCheckFactory();
        repository = TestFiles.mockRepository.parseIntoObject(Repository.class);
    }

    @Test
    public void testBranchWithNoProtection()
    {
        givenABranchWithNoProtection();
        whenGetStatusCheck();
        thenStatusCheckIsEmpty();
    }

    @Test
    public void testBranchWithProtection()
    {
        givenABranchWithProtection();
        whenGetStatusCheck();
        thenStatusCheckIsReturned();
    }

    private void givenABranchWithNoProtection()
    {
        branchName = "other";
    }

    private void givenABranchWithProtection()
    {
        branchName = "master";
    }

    private void thenStatusCheckIsEmpty()
    {
        assertThat(result.isEmpty(), equalTo(true));
    }

    private void thenStatusCheckIsReturned()
    {
        assertThat(result.size(), equalTo(1));
        assertThat(result.contains("grt-test-repository"), equalTo(true));
    }

    private void whenGetStatusCheck()
    {
        result = factory.getRequiredStatusChecks(branchName, repository);
    }
}
