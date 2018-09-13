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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.ClassUtils;
import stargate.commons.utils.JsonSerializer;
import stargate.commons.utils.ObjectSerializer;

/**
 *
 * @author iychoi
 */
public class Task {
    
    private static final Log LOG = LogFactory.getLog(Task.class);
    
    protected String name;
    protected Set<String> nodeNames = new HashSet<String>();
    protected Class<TaskRunnable> taskClass;
    protected Class valueClass;
    protected Object value;
    protected EnumTaskPriority priority;
    
    public static Task createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        JsonSerializer serializer = new JsonSerializer();
        return (Task) serializer.fromJsonFile(file, Task.class);
    }
    
    public static Task createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        JsonSerializer serializer = new JsonSerializer();
        return (Task) serializer.fromJson(json, Task.class);
    }
    
    Task() {
        this.name = null;
        this.taskClass = null;
        this.valueClass = null;
        this.value = null;
        this.priority = EnumTaskPriority.TASK_PRIORITY_LOW;
    }
    
    public Task(String name, Class taskClass, Class valueClass, Object value, EnumTaskPriority priority) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        if(taskClass == null) {
            throw new IllegalArgumentException("taskClass is null");
        }
        
        if(valueClass == null) {
            throw new IllegalArgumentException("valueClass is null");
        }
        
        if(value == null) {
            throw new IllegalArgumentException("value is null");
        }
        
        initialize(name, taskClass, valueClass, value, null, priority);
    }
    
    public Task(String name, Class taskClass, Class valueClass, Object value, Collection<String> nodeNames, EnumTaskPriority priority) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        if(taskClass == null) {
            throw new IllegalArgumentException("taskClass is null");
        }
        
        if(valueClass == null) {
            throw new IllegalArgumentException("valueClass is null");
        }
        
        if(value == null) {
            throw new IllegalArgumentException("value is null");
        }
        
        initialize(name, taskClass, valueClass, value, nodeNames, priority);
    }
    
    private void initialize(String name, Class taskClass, Class valueClass, Object value, Collection<String> nodeNames, EnumTaskPriority priority) {
        this.name = name;
        this.taskClass = taskClass;
        this.valueClass = valueClass;
        this.value = value;
        
        this.priority = EnumTaskPriority.TASK_PRIORITY_LOW;
        if(priority != null) {
            this.priority = priority;
        }
        
        if(nodeNames != null) {
            for(String nodeName : nodeNames) {
                if(!this.nodeNames.contains(nodeName)) {
                    this.nodeNames.add(nodeName);
                }
            }
        }
    }
    
    @JsonProperty("name")
    public String getName() {
        return this.name;
    }
    
    @JsonProperty("name")
    public void setName(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        this.name = name;
    }
    
    @JsonProperty("node_names")
    public Collection<String> getNodeNames() {
        return Collections.unmodifiableCollection(this.nodeNames);
    }
    
    @JsonProperty("node_names")
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
    
    @JsonIgnore
    public void addNodeName(String nodeName) {
        if(nodeName == null || nodeName.isEmpty()) {
            throw new IllegalArgumentException("nodeName is null or empty");
        }
        
        if(!this.nodeNames.contains(nodeName)) {
            this.nodeNames.add(nodeName);
        }
    }
    
    @JsonIgnore
    public void setTaskClass(Class clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        
        this.taskClass = clazz;
    }
    
    @JsonProperty("task_class")
    public void setTaskClass(String clazz) throws ClassNotFoundException {
        if(clazz == null || clazz.isEmpty()) {
            throw new IllegalArgumentException("clazz is null or empty");
        }
        
        this.taskClass = ClassUtils.findClass(clazz);
    }
    
    @JsonIgnore
    public Class getTaskClass() {
        return this.taskClass;
    }
    
    @JsonProperty("task_class")
    public String getTaskClassString() {
        return this.taskClass.getName();
    }
    
    @JsonIgnore
    public void setValueClass(Class valueClass) {
        if(valueClass == null) {
            throw new IllegalArgumentException("valueClass is null");
        }
        
        this.valueClass = valueClass;
    }
    
    @JsonProperty("value_class")
    public void setValueClass(String clazz) throws ClassNotFoundException {
        if(clazz == null || clazz.isEmpty()) {
            throw new IllegalArgumentException("clazz is null or empty");
        }
        
        this.valueClass = ClassUtils.findClass(clazz);
    }
    
    @JsonIgnore
    public Class getValueClass() {
        return this.valueClass;
    }
    
    @JsonProperty("value_class")
    public String getValueClassString() {
        return this.taskClass.getName();
    }
    
    @JsonIgnore
    public void setValue(Object value) {
        if(value == null) {
            throw new IllegalArgumentException("value is null");
        }
        
        this.value = value;
    }
    
    @JsonProperty("value")
    public void setValueString(String valueString) throws IOException {
        if(valueString == null) {
            throw new IllegalArgumentException("valueString is null");
        }

        this.value = ObjectSerializer.fromString(valueString, this.valueClass);
    }
    
    @JsonIgnore
    public Object getValue() {
        return this.value;
    }
    
    @JsonProperty("value")
    public String getValueString() throws IOException {
        return ObjectSerializer.toString(this.value);
    }
    
    @JsonProperty("priority")
    public EnumTaskPriority getPriority() {
        return this.priority;
    }
    
    @JsonProperty("priority")
    public void setPriority(EnumTaskPriority priority) {
        if(priority == null) {
            throw new IllegalArgumentException("priority is null");
        }
        
        this.priority = priority;
    }
    
    @JsonIgnore
    public TaskRunnable getRunnable() throws Exception {
        TaskRunnable taskInstance = null;
        taskInstance = (TaskRunnable) ClassUtils.getClassInstance(taskClass);
        taskInstance.setParameter(this.value);
        return taskInstance;
    }
    
    @Override
    @JsonIgnore
    public String toString() {
        return String.format("Task %s", this.name);
    }
    
    @JsonIgnore
    public String toJson() throws IOException {
        JsonSerializer serializer = new JsonSerializer();
        return serializer.toJson(this);
    }
    
    @JsonIgnore
    public void saveTo(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        JsonSerializer serializer = new JsonSerializer();
        serializer.toJsonFile(file, this);
    }
}
