/*
   Copyright 2018 The Trustees of University of Arizona

   Licensed under the Apache License, Version 2.0 (the "License" );
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package stargate.commons.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author iychoi
 */
public class ClassUtils {
    
    public static Class findClass(String className) throws ClassNotFoundException {
        return findClass(className, null);
    }
    
    public static Class findClass(String className, String[] lookupPaths) throws ClassNotFoundException {
        if(className == null || className.isEmpty()) {
            throw new IllegalArgumentException("className is not given");
        }
        
        if("int[]".equals(className)) {
            int[] ret = new int[0];
            return ret.getClass();
        } else if("long[]".equals(className)) {
            long[] ret = new long[0];
            return ret.getClass();
        } else if("double[]".equals(className)) {
            double[] ret = new double[0];
            return ret.getClass();
        } else if("float[]".equals(className)) {
            float[] ret = new float[0];
            return ret.getClass();
        }
        
        boolean array = false;
        String classNameRefined = className;
        if(className.endsWith("[]")) {
            // array
            array = true;
            classNameRefined = className.substring(0, className.length()-2);
        }
        
        Class clazz = null;
        
        // check whether the given className is full package path
        try {
            clazz = Class.forName(classNameRefined);
        } catch(ClassNotFoundException ex) {
        }

        // if the given className is not a full package path
        if(clazz == null) {
            if(lookupPaths != null) {
                for(String pkg : lookupPaths) {
                    String newClassName = pkg + "." + classNameRefined;
                    try {
                        clazz = Class.forName(newClassName);
                    } catch(ClassNotFoundException ex) {
                    }

                    if(clazz != null) {
                        break;
                    }
                }
            }
        }

        if(clazz == null) {
            throw new ClassNotFoundException("Class was not found : " + className);
        }
        
        if(array) {
            // call if it is array and canonical name
            String newClassName = String.format("[L%s;", clazz.getName());
            return findClass(newClassName, null);
        }
        
        return clazz;
    }
    
    public static Object getClassInstance(Class clazz) throws InstantiationException, IllegalAccessException {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is not given");
        }
        
        return clazz.newInstance();
    }
    
    public static void invokeMain(Class clazz, String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is not given");
        }
        
        Method method = null;

        try {
            // find main function
            Class[] argTypes = new Class[] { String[].class };
            method = clazz.getDeclaredMethod("main", argTypes);
        } catch(NoSuchMethodException ex) {
            throw new NoSuchMethodException("main function was not found in " + clazz.getName() + " class");
        }
        try {
            method.invoke(null, (Object)args);
        } catch (IllegalAccessException ex) {
            throw ex;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (InvocationTargetException ex) {
            throw ex;
        }
    }
    
    public static Object invokeCreateInstance(Class clazz, String json) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is not given");
        }
        
        Method method = null;

        try {
            // find main function
            Class[] argTypes = new Class[] { String.class };
            method = clazz.getDeclaredMethod("createInstance", argTypes);
        } catch(NoSuchMethodException ex) {
            throw new NoSuchMethodException("createInstance function was not found in " + clazz.getName() + " class");
        }
        try {
            return method.invoke(null, (Object)json);
        } catch (IllegalAccessException ex) {
            throw ex;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (InvocationTargetException ex) {
            throw ex;
        }
    }
}
