package io.dangernoodle.grt.extensions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.RepositoryFiles;
import io.dangernoodle.grt.Repository;


public class DefaultStatusCheckFactoryTest
{
    private String branchName;

    private DefaultStatusCheckFactory factory;

    private Repository repository;

    private Collection<String> result;

    @BeforeEach
    public void beforeEach() throws Exception
    {
        factory = new DefaultStatusCheckFactory();
        repository = Repository.load(RepositoryFiles.mockRepository.getFile());
    }

    @Test
    public void testBranchWithNoProtection()
    {
        givenABranchWithNoProtection();
        whenGetStatusCheck();
        thenStatusCheckIsNull();
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

    private void thenStatusCheckIsNull()
    {
        assertThat(result, nullValue());
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
