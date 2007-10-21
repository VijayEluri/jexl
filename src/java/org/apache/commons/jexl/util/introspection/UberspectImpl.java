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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jexl.util.AbstractExecutor;
import org.apache.commons.jexl.util.ArrayIterator;
import org.apache.commons.jexl.util.ArrayListWrapper;
import org.apache.commons.jexl.util.BooleanPropertyExecutor;
import org.apache.commons.jexl.util.EnumerationIterator;
import org.apache.commons.jexl.util.GetExecutor;
import org.apache.commons.jexl.util.PropertyExecutor;
import org.apache.commons.jexl.util.MapGetExecutor;
import org.apache.commons.logging.Log;

/**
 * Implementation of Uberspect to provide the default introspective
 * functionality of Velocity
 *
 * @since 1.0
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:henning@apache.org">Henning P. Schmiedehausen</a>
 * @version $Id: UberspectImpl.java 584050 2007-10-12 05:27:50Z proyal $
 */
public class UberspectImpl implements Uberspect, UberspectLoggable {
    /**
     * index of the first character of the property.
     */
    private static final int PROPERTY_START_INDEX = 3;

    /**
     * Our runtime logger.
     */
    private Log rlog;

    /**
     * the default Velocity introspector.
     */
    private Introspector introspector;

    /**
     * init - does nothing - we need to have setRuntimeLogger called before
     * getting our introspector, as the default vel introspector depends upon
     * it.
     * @throws Exception on any error.
     */
    public void init() throws Exception {
    }

    /**
     * Sets the runtime logger - this must be called before anything else
     * besides init() as to get the logger. Makes the pull model appealing...
     * @param runtimeLogger service to use for logging.
     */
    public void setRuntimeLogger(Log runtimeLogger) {
        rlog = runtimeLogger;
        introspector = new Introspector(rlog);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator getIterator(Object obj, Info i) throws Exception {
        if (obj.getClass().isArray()) {
            return new ArrayIterator(obj);
        } else if (obj instanceof Collection) {
            return ((Collection) obj).iterator();
        } else if (obj instanceof Map) {
            return ((Map) obj).values().iterator();
        } else if (obj instanceof Iterator) {
            rlog.warn("Warning! The iterative " + " is an Iterator in the #foreach() loop at [" + i.getLine() + ","
                    + i.getColumn() + "]" + " in template " + i.getTemplateName() + ". Because it's not resetable,"
                    + " if used in more than once, this may lead to" + " unexpected results.");

            return ((Iterator) obj);
        } else if (obj instanceof Enumeration) {
            rlog.warn("Warning! The iterative " + " is an Enumeration in the #foreach() loop at [" + i.getLine() + ","
                    + i.getColumn() + "]" + " in template " + i.getTemplateName() + ". Because it's not resetable,"
                    + " if used in more than once, this may lead to" + " unexpected results.");

            return new EnumerationIterator((Enumeration) obj);
        } else {
            // look for an iterator() method to support the JDK5 Iterable
            // interface or any user tools/DTOs that want to work in
            // foreach without implementing the Collection interface
            Class type = obj.getClass();
            try {
                Method iter = type.getMethod("iterator", null);
                Class returns = iter.getReturnType();
                if (Iterator.class.isAssignableFrom(returns)) {
                    return (Iterator) iter.invoke(obj, null);
                } else {
                    rlog.error("iterator() method of reference in #foreach loop at "
                            + i + " does not return a true Iterator.");
                }
            }
            catch (NoSuchMethodException nsme) {
                // eat this one, but let all other exceptions thru
            }
        }

        /*  we have no clue what this is  */
        rlog.warn("Could not determine type of iterator in " + "#foreach loop " + " at [" + i.getLine() + ","
                + i.getColumn() + "]" + " in template " + i.getTemplateName());

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public VelMethod getMethod(Object obj, String methodName, Object[] args, Info i) throws Exception {
        if (obj == null) {
            return null;
        }

        Method m = introspector.getMethod(obj.getClass(), methodName, args);
        if (m != null) {
            return new VelMethodImpl(m);
        } else if (obj.getClass().isArray()) {
            // check for support via our array->list wrapper
            m = introspector.getMethod(ArrayListWrapper.class, methodName, args);
            if (m != null) {
                // and create a method that knows to wrap the value
                // before invoking the method
                return new VelMethodImpl(m, true);
            }
        } else if (obj instanceof Class) {
            m = introspector.getMethod((Class) obj, methodName, args);
        }

        return (m == null) ? null : new VelMethodImpl(m);
    }

    /**
     * {@inheritDoc}
     */
    public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i) throws Exception {
        AbstractExecutor executor;

        Class claz = obj.getClass();

        /*
         * first try for a getFoo() type of property (also getfoo() )
         */

        executor = new PropertyExecutor(rlog, introspector, claz, identifier);

        /*
         * Let's see if we are a map...
         */
        if (!executor.isAlive()) {
            executor = new MapGetExecutor(rlog, claz, identifier);
        }

        /*
         *  look for boolean isFoo()
         */
        if (!executor.isAlive()) {
            executor = new BooleanPropertyExecutor(rlog, introspector, claz, identifier);
        }

        /*
         * if that didn't work, look for get("foo")
         */

        if (!executor.isAlive()) {
            executor = new GetExecutor(rlog, introspector, claz, identifier);
        }

        return new VelGetterImpl(executor);
    }

    /**
     * {@inheritDoc}
     */
    public VelPropertySet getPropertySet(Object obj, String identifier, Object arg, Info i) throws Exception {
        Class claz = obj.getClass();

        VelMethod vm = null;
        try {
            /*
             * first, we introspect for the set<identifier> setter method
             */

            Object[] params = {arg};

            try {
                vm = getMethod(obj, "set" + identifier, params, i);

                if (vm == null) {
                    throw new NoSuchMethodException();
                }
            } catch (NoSuchMethodException nsme2) {
                StringBuffer sb = new StringBuffer("set");
                sb.append(identifier);

                if (Character.isLowerCase(sb.charAt(PROPERTY_START_INDEX))) {
                    sb.setCharAt(PROPERTY_START_INDEX, Character.toUpperCase(sb.charAt(PROPERTY_START_INDEX)));
                } else {
                    sb.setCharAt(PROPERTY_START_INDEX, Character.toLowerCase(sb.charAt(PROPERTY_START_INDEX)));
                }

                vm = getMethod(obj, sb.toString(), params, i);

                if (vm == null) {
                    throw new NoSuchMethodException();
                }
            }
        } catch (NoSuchMethodException nsme) {
            /*
             * right now, we only support the Map interface
             */

            if (Map.class.isAssignableFrom(claz)) {
                Object[] params = {new Object(), new Object()};

                vm = getMethod(obj, "put", params, i);

                if (vm != null) {
                    return new VelSetterImpl(vm, identifier);
                }
            }
        }

        return (vm == null) ? null : new VelSetterImpl(vm);
    }

    /**
     * An implementation of {@link VelMethod}.
     */
    public static class VelMethodImpl implements VelMethod {
        final Method method;
        Boolean isVarArg;
        final boolean wrapArray;

        /**
         * Create a new instance.
         *
         * @param m the method.
         */
        public VelMethodImpl(Method m) {
            this(m, false);
        }

        public VelMethodImpl(Method method, boolean wrapArray) {
            this.method = method;
            this.wrapArray = wrapArray;
        }

        /**
         * {@inheritDoc}
         */
        public Object invoke(Object o, Object[] params) throws Exception {
            if (isVarArg()) {
                Class[] formal = method.getParameterTypes();
                int index = formal.length - 1;
                Class type = formal[index].getComponentType();
                if (params.length >= index) {
                    params = handleVarArg(type, index, params);
                }
            } else if (wrapArray) {
                o = new ArrayListWrapper(o);
            }

            try {
                return method.invoke(o, params);
            } catch (InvocationTargetException e) {
                final Throwable t = e.getTargetException();

                if (t instanceof Exception) {
                    throw (Exception) t;
                } else if (t instanceof Error) {
                    throw (Error) t;
                } else {
                    throw e;
                }
            }
        }

        /**
         * @returns true if this method can accept a variable number of arguments
         */
        public boolean isVarArg() {
            if (isVarArg == null) {
                Class[] formal = method.getParameterTypes();
                if (formal == null || formal.length == 0) {
                    this.isVarArg = Boolean.FALSE;
                } else {
                    Class last = formal[formal.length - 1];
                    // if the last arg is an array, then
                    // we consider this a varargs method
                    this.isVarArg = Boolean.valueOf(last.isArray());
                }
            }
            return isVarArg.booleanValue();
        }

        /**
         * @param type   The vararg class type (aka component type
         *               of the expected array arg)
         * @param index  The index of the vararg in the method declaration
         *               (This will always be one less than the number of
         *               expected arguments.)
         * @param actual The actual parameters being passed to this method
         * @returns The actual parameters adjusted for the varargs in order
         * to fit the method declaration.
         */
        private Object[] handleVarArg(Class type, int index, Object[] actual) {
            // if no values are being passed into the vararg
            if (actual.length == index) {
                // create an empty array of the expected type
                actual = new Object[]{Array.newInstance(type, 0)};
            }
            // if one value is being passed into the vararg
            else if (actual.length == index + 1) {
                // make sure the last arg is an array of the expected type
                if (IntrospectionUtils.isMethodInvocationConvertible(type,
                        actual[index].getClass(),
                        false)) {
                    // create a 1-length array to hold and replace the last param
                    Object lastActual = Array.newInstance(type, 1);
                    Array.set(lastActual, 0, actual[index]);
                    actual[index] = lastActual;
                }
            }
            // if multiple values are being passed into the vararg
            else if (actual.length > index + 1) {
                // put the last and extra actual in an array of the expected type
                int size = actual.length - index;
                Object lastActual = Array.newInstance(type, size);
                for (int i = 0; i < size; i++) {
                    Array.set(lastActual, i, actual[index + i]);
                }

                // put all into a new actual array of the appropriate size
                Object[] newActual = new Object[index + 1];
                for (int i = 0; i < index; i++) {
                    newActual[i] = actual[i];
                }
                newActual[index] = lastActual;

                // replace the old actual array
                actual = newActual;
            }
            return actual;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isCacheable() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public String getMethodName() {
            return method.getName();
        }

        /**
         * {@inheritDoc}
         */
        public Class getReturnType() {
            return method.getReturnType();
        }
    }

    /**
     * {@inheritDoc}
     */
    public static class VelGetterImpl implements VelPropertyGet {
        /**
         * executor for performing the get.
         */
        protected final AbstractExecutor ae;

        /**
         * Create the getter using an {@link AbstractExecutor} to
         * do the work.
         *
         * @param exec the executor.
         */
        public VelGetterImpl(AbstractExecutor exec) {
            ae = exec;
        }

        /**
         * {@inheritDoc}
         */
        public Object invoke(Object o) throws Exception {
            return ae.execute(o);
        }

        /**
         * {@inheritDoc}
         */
        public boolean isCacheable() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isAlive() {
            return ae.isAlive();
        }

        /**
         * {@inheritDoc}
         */
        public String getMethodName() {
            return ae.getMethod().getName();
        }
    }

    /**
     * {@inheritDoc}
     */
    public static class VelSetterImpl implements VelPropertySet {
        /**
         * the method to call.
         */
        protected VelMethod vm = null;
        /**
         * the key for indexed and other properties.
         */
        protected String putKey = null;

        /**
         * Create an instance.
         *
         * @param velmethod the method to call on set.
         */
        public VelSetterImpl(VelMethod velmethod) {
            this.vm = velmethod;
        }

        /**
         * Create an instance.
         *
         * @param velmethod the method to call on set.
         * @param key       the index or other value passed to a
         *                  setProperty(xxx, value) method.
         */
        public VelSetterImpl(VelMethod velmethod, String key) {
            this.vm = velmethod;
            putKey = key;
        }

        /**
         * {@inheritDoc}
         */
        public Object invoke(Object o, Object value) throws Exception {
            List al = new ArrayList();

            if (putKey == null) {
                al.add(value);
            } else {
                al.add(putKey);
                al.add(value);
            }

            return vm.invoke(o, al.toArray());
        }

        /**
         * {@inheritDoc}
         */
        public boolean isCacheable() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public String getMethodName() {
            return vm.getMethodName();
        }

    }
}
