// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.ntp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class TimeInfo
{
    private final NtpV3Packet _message;
    private List<String> _comments;
    private Long _delay;
    private Long _offset;
    private final long _returnTime;
    private boolean _detailsComputed;
    
    public TimeInfo(final NtpV3Packet message, final long returnTime) {
        this(message, returnTime, null, true);
    }
    
    public TimeInfo(final NtpV3Packet message, final long returnTime, final List<String> comments) {
        this(message, returnTime, comments, true);
    }
    
    public TimeInfo(final NtpV3Packet msgPacket, final long returnTime, final boolean doComputeDetails) {
        this(msgPacket, returnTime, null, doComputeDetails);
    }
    
    public TimeInfo(final NtpV3Packet message, final long returnTime, final List<String> comments, final boolean doComputeDetails) {
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }
        this._returnTime = returnTime;
        this._message = message;
        this._comments = comments;
        if (doComputeDetails) {
            this.computeDetails();
        }
    }
    
    public void addComment(final String comment) {
        if (this._comments == null) {
            this._comments = new ArrayList<String>();
        }
        this._comments.add(comment);
    }
    
    public void computeDetails() {
        if (this._detailsComputed) {
            return;
        }
        this._detailsComputed = true;
        if (this._comments == null) {
            this._comments = new ArrayList<String>();
        }
        final TimeStamp origNtpTime = this._message.getOriginateTimeStamp();
        final long origTime = origNtpTime.getTime();
        final TimeStamp rcvNtpTime = this._message.getReceiveTimeStamp();
        final long rcvTime = rcvNtpTime.getTime();
        final TimeStamp xmitNtpTime = this._message.getTransmitTimeStamp();
        final long xmitTime = xmitNtpTime.getTime();
        if (origNtpTime.ntpValue() == 0L) {
            if (xmitNtpTime.ntpValue() != 0L) {
                this._offset = xmitTime - this._returnTime;
                this._comments.add("Error: zero orig time -- cannot compute delay");
            }
            else {
                this._comments.add("Error: zero orig time -- cannot compute delay/offset");
            }
        }
        else if (rcvNtpTime.ntpValue() == 0L || xmitNtpTime.ntpValue() == 0L) {
            this._comments.add("Warning: zero rcvNtpTime or xmitNtpTime");
            if (origTime > this._returnTime) {
                this._comments.add("Error: OrigTime > DestRcvTime");
            }
            else {
                this._delay = this._returnTime - origTime;
            }
            if (rcvNtpTime.ntpValue() != 0L) {
                this._offset = rcvTime - origTime;
            }
            else if (xmitNtpTime.ntpValue() != 0L) {
                this._offset = xmitTime - this._returnTime;
            }
        }
        else {
            long delayValue = this._returnTime - origTime;
            if (xmitTime < rcvTime) {
                this._comments.add("Error: xmitTime < rcvTime");
            }
            else {
                final long delta = xmitTime - rcvTime;
                if (delta <= delayValue) {
                    delayValue -= delta;
                }
                else if (delta - delayValue == 1L) {
                    if (delayValue != 0L) {
                        this._comments.add("Info: processing time > total network time by 1 ms -> assume zero delay");
                        delayValue = 0L;
                    }
                }
                else {
                    this._comments.add("Warning: processing time > total network time");
                }
            }
            this._delay = delayValue;
            if (origTime > this._returnTime) {
                this._comments.add("Error: OrigTime > DestRcvTime");
            }
            this._offset = (rcvTime - origTime + (xmitTime - this._returnTime)) / 2L;
        }
    }
    
    public List<String> getComments() {
        return this._comments;
    }
    
    public Long getDelay() {
        return this._delay;
    }
    
    public Long getOffset() {
        return this._offset;
    }
    
    public NtpV3Packet getMessage() {
        return this._message;
    }
    
    public InetAddress getAddress() {
        final DatagramPacket pkt = this._message.getDatagramPacket();
        return (pkt == null) ? null : pkt.getAddress();
    }
    
    public long getReturnTime() {
        return this._returnTime;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final TimeInfo other = (TimeInfo)obj;
        return this._returnTime == other._returnTime && this._message.equals(other._message);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = (int)this._returnTime;
        result = 31 * result + this._message.hashCode();
        return result;
    }
}
