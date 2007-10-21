/*
 * Copyright 2002-2006 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jexl;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

/**
 * Tests for Script
 * @since 1.1
 */
public class ScriptTest extends TestCase {

    /**
     * Create a new test case.
     * @param name case name
     */
    public ScriptTest(String name) {
        super(name);
    }

    /**
     * Test creating a script from a string.
     */
    public void testSimpleScript() throws Exception {
        String code = "while (x < 10) x = x + 1;";
        Script s = ScriptFactory.createScript(code);
        JexlContext jc = JexlHelper.createContext();
        jc.getVars().put("x", new Integer(1));
    
        Object o = s.execute(jc);
        assertEquals("Result is wrong", new Long(10), o);
        assertEquals("getText is wrong", code, s.getText());
    }
    
    public void testScriptFromFile() throws Exception {
        File testScript = new File("src/test-scripts/test1.jexl");
        Script s = ScriptFactory.createScript(testScript);
        JexlContext jc = JexlHelper.createContext();
        jc.getVars().put("out", System.out);
        Object result = s.execute(jc);
        assertNotNull("No result", result);
        assertEquals("Wrong result", new Long(7), result);
    }

    public void testScriptFromURL() throws Exception {
        URL testUrl = new File("src/test-scripts/test1.jexl").toURL();
        Script s = ScriptFactory.createScript(testUrl);
        JexlContext jc = JexlHelper.createContext();
        jc.getVars().put("out", System.out);
        Object result = s.execute(jc);
        assertNotNull("No result", result);
        assertEquals("Wrong result", new Long(7), result);
    }
}
