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

import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import stargate.commons.utils.DateTimeUtils;

/**
 *
 * @author iychoi
 */
public class TaskSchedule {
    
    private static final Log LOG = LogFactory.getLog(TaskSchedule.class);
    
    private String name;
    private Runnable runnable;
    private TimeUnit periodTimeUnit;
    private long period;
    private long lastPerformedTime = 0;
    
    
    TaskSchedule() {
        this.name = null;
        this.runnable = null;
        this.periodTimeUnit = TimeUnit.MINUTES;
        this.period = 0; // nowait
        this.lastPerformedTime = 0;
    }
    
    public TaskSchedule(String name, Runnable runnable, TimeUnit periodTimeUnit, long period) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        if(runnable == null) {
            throw new IllegalArgumentException("runnable is null");
        }
        
        if(periodTimeUnit == null) {
            throw new IllegalArgumentException("periodTimeUnit is null");
        }
        
        if(period < 0) {
            throw new IllegalArgumentException("period is invalid");
        }
        
        initialize(name, runnable, periodTimeUnit, period);
    }
    
    private void initialize(String name, Runnable runnable, TimeUnit periodTimeUnit, long period) {
        this.name = name;
        this.runnable = runnable;
        this.periodTimeUnit = periodTimeUnit;
        this.period = period;
        this.lastPerformedTime = 0;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Runnable getRunnable() {
        return this.runnable;
    }
    
    public TimeUnit getPeriodTimeUnit() {
        return this.periodTimeUnit;
    }
    
    public long getPeriod() {
        return this.period;
    }
    
    public long getLastPerformedTime() {
        return this.lastPerformedTime;
    }
    
    public void updateLastPerformedTime() {
        this.lastPerformedTime = DateTimeUtils.getTimestamp();
    }
    
    public void setLastUpdateTime(long time) {
        this.lastPerformedTime = time;
    }
    
    public String toString() {
        return String.format("TaskSchedule %s", this.name);
    }
}
