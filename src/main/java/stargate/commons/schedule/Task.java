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
package stargate.commons.schedule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author iychoi
 */
public class Task {
    
    private static final Log LOG = LogFactory.getLog(Task.class);
    
    protected String name;
    protected Set<String> nodeNames = new HashSet<String>();
    protected Callable<?> callable;
    protected Object param;
    protected Future<?> future;
    
    Task() {
        this.name = null;
        this.callable = null;
        this.param = null;
        this.future = null;
    }
    
    public Task(String name, Object param) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        initialize(name, null, param, null);
    }
    
    public Task(String name, Object param, Collection<String> nodeNames) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        initialize(name, null, param, nodeNames);
    }
    
    public Task(String name, Callable<?> callable, Object param) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        if(callable == null) {
            throw new IllegalArgumentException("callable is null");
        }
        
        initialize(name, callable, param, null);
    }
    
    public Task(String name, Callable<?> callable, Object param, Collection<String> nodeNames) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        initialize(name, callable, param, nodeNames);
    }
    
    private void initialize(String name, Callable<?> callable, Object param, Collection<String> nodeNames) {
        this.name = name;
        this.callable = callable;
        this.param = param;
        
        if(nodeNames != null) {
            for(String nodeName : nodeNames) {
                // if it already exists, skip
                this.nodeNames.add(nodeName);
            }
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        this.name = name;
    }
    
    public Collection<String> getNodeNames() {
        return Collections.unmodifiableCollection(this.nodeNames);
    }
    
    public void addNodeNames(Collection<String> nodeNames) {
        if(nodeNames == null) {
            throw new IllegalArgumentException("nodeNames is null");
        }
        
        for(String nodeName : nodeNames) {
            if(!this.nodeNames.contains(nodeName)) {
                this.nodeNames.add(nodeName);
            }
        }
    }
    
    public void addNodeName(String nodeName) {
        if(nodeName == null || nodeName.isEmpty()) {
            throw new IllegalArgumentException("nodeName is null or empty");
        }
        
        if(!this.nodeNames.contains(nodeName)) {
            this.nodeNames.add(nodeName);
        }
    }
    
    public void setCallable(Callable<?> callable) {
        if(callable == null) {
            throw new IllegalArgumentException("callable is null");
        }
        
        this.callable = callable;
    }
    
    public Callable<?> getCallable() {
        return this.callable;
    }
    
    public void setParam(Object param) {
        if(param == null) {
            throw new IllegalArgumentException("param is null");
        }
        
        this.param = param;
    }
    
    public Object getParam() {
        return this.param;
    }
    
    public void setFuture(Future<?> future) {
        if(future == null) {
            throw new IllegalArgumentException("future is null");
        }
        
        this.future = future;
    }
    
    public Future<?> getFuture() {
        return this.future;
    }
    
    public String toString() {
        return String.format("Task %s", this.name);
    }
}
