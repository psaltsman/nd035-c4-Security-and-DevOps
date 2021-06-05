package com.udacity.examples.Testing;

import org.junit.*;
import static org.junit.Assert.*;

import com.udacity.examples.Testing.Helper;
import java.util.*;

public class HelperTest {

    @Test
    public void getMergedList() {

        List<String> list = Arrays.asList("1", "2", "3");

        assertEquals("1, 2, 3", Helper.getMergedList(list));

        assertNotEquals("3, 2, 1", Helper.getMergedList(list));
    }
}