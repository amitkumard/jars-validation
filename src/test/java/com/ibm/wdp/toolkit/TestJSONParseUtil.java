/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */
package com.ibm.wdp.toolkit;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import com.ibm.wdp.toolkit.util.JSONParseUtil;

public class TestJSONParseUtil { @Test
  public void testJSONParseUtil() {
    assertTrue(JSONParseUtil.isJSONParsable());
  }
}
