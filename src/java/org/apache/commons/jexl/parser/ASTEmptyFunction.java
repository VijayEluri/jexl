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

package org.apache.commons.jexl.parser;

import org.apache.commons.jexl.JexlContext;

import java.util.Collection;
import java.util.Map;

/**
 * function to see if reference doesn't exist in context.
 * 
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 * @author <a href="mailto:tobrien@apache.org">Tim O'Brien</a>
 * @version $Id: ASTEmptyFunction.java 480412 2006-11-29 05:11:23Z bayard $
 */
public class ASTEmptyFunction extends SimpleNode {
    /**
     * Create the node given an id.
     * 
     * @param id node id.
     */
    public ASTEmptyFunction(int id) {
        super(id);
    }

    /**
     * Create a node with the given parser and id.
     * 
     * @param p a parser.
     * @param id node id.
     */
    public ASTEmptyFunction(Parser p, int id) {
        super(p, id);
    }

    /** {@inheritDoc} */
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /** {@inheritDoc} */
    public Object value(JexlContext jc) throws Exception {
        SimpleNode sn = (SimpleNode) jjtGetChild(0);

        /*
         * I can't believe this
         */

        Object o = sn.value(jc);

        if (o == null) {
            return Boolean.TRUE;
        }

        if (o instanceof String && "".equals(o)) {
            return Boolean.TRUE;
        }

        if (o.getClass().isArray() && ((Object[]) o).length == 0) {
            return Boolean.TRUE;
        }

        if (o instanceof Collection && ((Collection) o).isEmpty()) {
            return Boolean.TRUE;
        }

        /*
         * Map isn't a collection
         */
        if (o instanceof Map && ((Map) o).isEmpty()) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
