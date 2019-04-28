package com.ach.ei.nifi.test.core;

import com.ach.nifi.core.NifiProcStreamWriter;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WriteResourceToStreamTest {

    private TestRunner testRunner;

    @Before
    public void init() {
        testRunner = TestRunners.newTestRunner(NifiProcStreamWriter.class);
    }

    @Test
    public void testProcessor() {
        testRunner.enqueue(new byte[] { 1, 2, 3, 4, 5 });
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(NifiProcStreamWriter.REL_SUCCESS, 1);
        final byte[] data = testRunner
                .getFlowFilesForRelationship(NifiProcStreamWriter.REL_SUCCESS).get(0)
                .toByteArray();
        final String stringData = new String(data);
        Assert.assertEquals("this came from a resource", stringData);
    }

}