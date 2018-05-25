package io.dangernoodle.grt.extensions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.RepositoryBuilder;


public class DefaultStatusCheckFactoryTest
{
    private String branchName;

    private DefaultStatusCheckFactory factory;

    private Repository repository;

    private Collection<String> result;

    private RepositoryBuilder builder;

    @BeforeEach
    public void beforeEach() throws Exception
    {
        factory = new DefaultStatusCheckFactory();
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
        assertThat(result, notNullValue());
        assertThat(result.isEmpty(), equalTo(true));
    }

    private void thenStatusCheckIsReturned()
    {
        assertThat(result.size(), equalTo(1));
        assertThat(result.contains("grt-test-repository"), equalTo(true));
    }

    private void whenGetStatusCheck()
    {
        repository = builder.build();
        result = factory.getRequiredStatusChecks(branchName, repository);
    }
}
