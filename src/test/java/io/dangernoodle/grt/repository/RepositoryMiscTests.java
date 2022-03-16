package io.dangernoodle.grt.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;


public class RepositoryMiscTests
{
    @Test
    public void testColorsAreEqual()
    {
        Color color1 = new Color("00000");
        Color color2 = new Color("00000");

        assertEquals(color1, color2);
        assertEquals(45806671, color1.hashCode());

        assertNotEquals(color1, null);
        assertNotEquals(color1, new Permission("admin"));
    }

    @Test
    public void testPermissionsAreEqual()
    {
        Permission perm1 = new Permission("admin");
        Permission perm2 = new Permission("admin");

        assertEquals(perm1, perm2);
        assertEquals(92668782, perm1.hashCode());

        assertNotEquals(perm1, null);
        assertNotEquals(perm1, new Color("00000"));
    }
}
