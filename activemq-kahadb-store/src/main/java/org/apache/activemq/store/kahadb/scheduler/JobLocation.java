/**
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
package org.apache.activemq.store.kahadb.scheduler;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.activemq.store.kahadb.disk.journal.Location;
import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;

class JobLocation {
   
    private String jobId;
    private int repeat;
    private long startTime;
    private long delay;
    private long nextTime;
    private long period;
    private String cronEntry;
    private final Location location;

    public JobLocation(Location location) {
        this.location = location;

    }

    public JobLocation() {
        this(new Location());
    }
   
    public void readExternal(DataInput in) throws IOException {
        this.jobId = in.readUTF();
        this.repeat = in.readInt();
        this.startTime = in.readLong();
        this.delay = in.readLong();
        this.nextTime = in.readLong();
        this.period = in.readLong();
        this.cronEntry=in.readUTF();
        this.location.readExternal(in);
    }

    public void writeExternal(DataOutput out) throws IOException {
        out.writeUTF(this.jobId);
        out.writeInt(this.repeat);
        out.writeLong(this.startTime);
        out.writeLong(this.delay);
        out.writeLong(this.nextTime);
        out.writeLong(this.period);
        if (this.cronEntry==null) {
            this.cronEntry="";
        }
        out.writeUTF(this.cronEntry);
        this.location.writeExternal(out);
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return this.jobId;
    }

    /**
     * @param jobId
     *            the jobId to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    

    /**
     * @return the repeat
     */
    public int getRepeat() {
        return this.repeat;
    }

    /**
     * @param repeat
     *            the repeat to set
     */
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    /**
     * @return the start
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setStartTime(long start) {
        this.startTime = start;
    }
    
    /**
     * @return the nextTime
     */
    public synchronized long getNextTime() {
        return this.nextTime;
    }

    /**
     * @param nextTime the nextTime to set
     */
    public synchronized void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }

    /**
     * @return the period
     */
    public long getPeriod() {
        return this.period;
    }

    /**
     * @param period
     *            the period to set
     */
    public void setPeriod(long period) {
        this.period = period;
    }
    
    /**
     * @return the cronEntry
     */
    public synchronized String getCronEntry() {
        return this.cronEntry;
    }

    /**
     * @param cronEntry the cronEntry to set
     */
    public synchronized void setCronEntry(String cronEntry) {
        this.cronEntry = cronEntry;
    }
    
    public boolean isCron() {
        return getCronEntry() != null && getCronEntry().length() > 0;
    }
    
    /**
     * @return the delay
     */
    public long getDelay() {
        return this.delay;
    }

    /**
     * @param delay the delay to set
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return this.location;
    }
    
    public String toString() {
        return "Job [id=" + jobId + ", startTime=" + new Date(startTime)
                + ", delay=" + delay + ", period=" + period + ", repeat="
                + repeat + ", nextTime=" + new Date(nextTime) + "]"; 
    }

    static class JobLocationMarshaller extends VariableMarshaller<List<JobLocation>> {
        static final JobLocationMarshaller INSTANCE = new JobLocationMarshaller();
        public List<JobLocation> readPayload(DataInput dataIn) throws IOException {
            List<JobLocation> result = new ArrayList<JobLocation>();
            int size = dataIn.readInt();
            for (int i = 0; i < size; i++) {
                JobLocation jobLocation = new JobLocation();
                jobLocation.readExternal(dataIn);
                result.add(jobLocation);
            }
            return result;
        }

        public void writePayload(List<JobLocation> value, DataOutput dataOut) throws IOException {
            dataOut.writeInt(value.size());
            for (JobLocation jobLocation : value) {
                jobLocation.writeExternal(dataOut);
            }
        }
    }
}
