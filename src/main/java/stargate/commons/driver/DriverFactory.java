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
package stargate.commons.driver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author iychoi
 */
public class DriverFactory {
    
    private static final Log LOG = LogFactory.getLog(DriverFactory.class);
    
    public static AbstractDriver createDriver(DriverInjection driverInjection) throws DriverFailedToLoadException {
        if(driverInjection == null) {
            throw new IllegalArgumentException("driverInjection is null");
        }
        
        return createDriver(driverInjection.getDriverClass(), driverInjection.getDriverConfig());
    }
    
    public static AbstractDriver createDriver(Class driverClass, AbstractDriverConfig driverConfig) throws DriverFailedToLoadException {
        if(driverClass == null) {
            throw new IllegalArgumentException("driverClass is null");
        }

        if(driverConfig == null) {
            throw new IllegalArgumentException("driverConfig is null");
        }
        
        Method method = null;

        try {
            // find getinstance function
            Class[] argTypes = new Class[] { AbstractDriverConfig.class };
            method = driverClass.getDeclaredMethod("getInstance", argTypes);
        } catch(NoSuchMethodException ex) {
            // no getinstance static function
        }
        
        if(method != null) {
            // getInstance(config) is implemented
            try {
                return (AbstractDriver) method.invoke(null, (Object)driverConfig);
            } catch (IllegalAccessException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (IllegalArgumentException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (InvocationTargetException ex) {
                throw new DriverFailedToLoadException(ex);
            }
        } else {
            // getInstance(config) is not implemented
            // call "constructor"
            
            try {
                Constructor constructor = driverClass.getConstructor(AbstractDriverConfig.class);
                return (AbstractDriver) constructor.newInstance(driverConfig);
            } catch (InstantiationException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (IllegalAccessException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (IllegalArgumentException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (InvocationTargetException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (NoSuchMethodException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (SecurityException ex) {
                throw new DriverFailedToLoadException(ex);
            }
        }
    }
}
