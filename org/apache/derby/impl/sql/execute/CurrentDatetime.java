// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.sql.Timestamp;
import java.sql.Time;
import java.util.Date;

public class CurrentDatetime
{
    private Date currentDatetime;
    private java.sql.Date currentDate;
    private Time currentTime;
    private Timestamp currentTimestamp;
    
    private final void setCurrentDatetime() {
        if (this.currentDatetime == null) {
            this.currentDatetime = new Date();
        }
    }
    
    public java.sql.Date getCurrentDate() {
        if (this.currentDate == null) {
            this.setCurrentDatetime();
            this.currentDate = new java.sql.Date(this.currentDatetime.getTime());
        }
        return this.currentDate;
    }
    
    public Time getCurrentTime() {
        if (this.currentTime == null) {
            this.setCurrentDatetime();
            this.currentTime = new Time(this.currentDatetime.getTime());
        }
        return this.currentTime;
    }
    
    public Timestamp getCurrentTimestamp() {
        if (this.currentTimestamp == null) {
            this.setCurrentDatetime();
            this.currentTimestamp = new Timestamp(this.currentDatetime.getTime());
        }
        return this.currentTimestamp;
    }
    
    public void forget() {
        this.currentDatetime = null;
        this.currentDate = null;
        this.currentTime = null;
        this.currentTimestamp = null;
    }
}
