/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests for map literals
 *
 * @author Peter Royal
 * @since 1.2
 */
public class MapLiteralTest extends TestCase {

    public void testLiteralWithStrings() throws Exception {
        Expression e = ExpressionFactory.createExpression( "[ 'foo' => 'bar' ]" );
        JexlContext jc = JexlHelper.createContext();

        Object o = e.evaluate( jc );
        assertEquals( Collections.singletonMap( "foo", "bar" ), o );
    }

    public void testLiteralWithMultipleEntries() throws Exception {
        Expression e = ExpressionFactory.createExpression( "[ 'foo' => 'bar', 'eat' => 'food' ]" );
        JexlContext jc = JexlHelper.createContext();

        Map expected = new HashMap();
        expected.put( "foo", "bar" );
        expected.put( "eat", "food" );

        Object o = e.evaluate( jc );
        assertEquals( expected, o );
    }

    public void testLiteralWithNumbers() throws Exception {
        Expression e = ExpressionFactory.createExpression( "[ 5 => 10 ]" );
        JexlContext jc = JexlHelper.createContext();

        Object o = e.evaluate( jc );
        assertEquals( Collections.singletonMap( new Integer( 5 ), new Integer( 10 ) ), o );
    }

    public void testSizeOfSimpleMapLiteral() throws Exception {
        Expression e = ExpressionFactory.createExpression( "size([ 'foo' => 'bar' ])" );
        JexlContext jc = JexlHelper.createContext();

        Object o = e.evaluate( jc );
        assertEquals( new Integer( 1 ), o );
    }

    public void testCallingMethodsOnNewMapLiteral() throws Exception {
        Expression e = ExpressionFactory.createExpression( "size([ 'foo' => 'bar' ].values())" );
        JexlContext jc = JexlHelper.createContext();

        Object o = e.evaluate( jc );
        assertEquals( new Integer( 1 ), o );
    }

    public void testNotEmptySimpleMapLiteral() throws Exception {
        Expression e = ExpressionFactory.createExpression( "empty([ 'foo' => 'bar' ])" );
        JexlContext jc = JexlHelper.createContext();

        Object o = e.evaluate( jc );
        assertFalse( ( (Boolean) o ).booleanValue() );
    }

}
