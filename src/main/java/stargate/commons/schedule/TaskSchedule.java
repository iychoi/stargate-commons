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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author iychoi
 */
public class TaskSchedule extends Task {
    
    private static final Log LOG = LogFactory.getLog(TaskSchedule.class);
    
    protected boolean repeat;
    protected long delay; // in sec
    protected long interval; // in sec
    
    TaskSchedule() {
        super();
        
        this.repeat = false;
        this.delay = 0;
        this.interval = 0;
    }
    
    public TaskSchedule(String name, Runnable runnable, Object param, boolean repeat, long delay, long interval) {
        super(name, runnable, param);
        
        initialize(repeat, delay, interval);
    }
    
    public TaskSchedule(String name, Runnable runnable, Object param, Collection<String> nodeNames, boolean repeat, long delay, long interval) {
        super(name, runnable, param, nodeNames);
        
        initialize(repeat, delay, interval);
    }
    
    private void initialize(boolean repeat, long delay, long interval) {
        this.repeat = repeat;
        this.delay = Math.max(delay, 0);
        this.interval = Math.max(0, interval);
    }
    
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
    
    public boolean isRepeat() {
        return this.repeat;
    }
    
    public void setDelay(long delay) {
        this.delay = Math.max(delay, 0);
    }
    
    public long getDelay() {
        return this.delay;
    }
    
    public void setInterval(long interval) {
        this.interval = Math.max(0, interval);
    }
    
    public long getInterval() {
        return this.interval;
    }
}
