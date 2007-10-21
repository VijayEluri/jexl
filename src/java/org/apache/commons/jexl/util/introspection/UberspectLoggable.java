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

package org.apache.commons.jexl.util.introspection;

import org.apache.commons.logging.Log;

/**
 * Marker interface to let an uberspector indicate it can and wants to log.
 * 
 * Thanks to Paulo for the suggestion
 * 
 * @since 1.0
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 * @version $Id: UberspectLoggable.java 480412 2006-11-29 05:11:23Z bayard $
 * 
 */
public interface UberspectLoggable {
    /**
     * Sets the logger. This will be called before any calls to the uberspector
     * @param logger the logger.
     */
    void setRuntimeLogger(Log logger);
}
