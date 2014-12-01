package com.example.steve.impromptu.Tests;

import android.test.InstrumentationTestCase;

/**
 * Created by Sean on 11/30/2014.
 */
public class ExampleTest extends InstrumentationTestCase {

     //NOTE: test methods MUST start with "test"
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}
