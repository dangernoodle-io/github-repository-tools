package grt;

import java.io.IOException;

import org.junit.jupiter.api.condition.EnabledIf;


@EnabledIf("systemProperty.get('grt.it.organization') != null")
public class OrgRepositoryIT extends UserRepositoryIT
{
    @Override
    protected String getOrganization() throws IOException
    {
        return System.getProperty("grt.it.organization");
    }
}
