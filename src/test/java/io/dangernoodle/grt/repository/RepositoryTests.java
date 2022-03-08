package io.dangernoodle.grt.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;


public class RepositoryTests
{
    @Test
    public void testColorsAreEqual()
    {
        Color color1 = new Color("00000");
        Color color2 = new Color("00000");

        assertThat(color1, equalTo(color2));
    }

    @Test
    public void testPermissionsAreEqual()
    {
        Permission perm1 = new Permission("admin");
        Permission perm2 = new Permission("admin");

        assertThat(perm1, equalTo(perm2));
    }
}
