package io.dangernoodle.grt.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Repository.Settings.Color;


public class RepositoryTests
{
    @Test
    public void testColorsAreEqual()
    {
        Color color1 = Color.from("00000");
        Color color2 = Color.from("00000");

        assertThat(color1, equalTo(color2));
    }
}
