// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.util;

import java.util.PriorityQueue;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class Metrics2Util
{
    @InterfaceAudience.Private
    public static class NameValuePair implements Comparable<NameValuePair>
    {
        private String name;
        private long value;
        
        public NameValuePair(final String metricName, final long value) {
            this.name = metricName;
            this.value = value;
        }
        
        public String getName() {
            return this.name;
        }
        
        public long getValue() {
            return this.value;
        }
        
        @Override
        public int compareTo(final NameValuePair other) {
            return (int)(this.value - other.value);
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof NameValuePair && this.compareTo((NameValuePair)other) == 0;
        }
        
        @Override
        public int hashCode() {
            return Long.valueOf(this.value).hashCode();
        }
    }
    
    @InterfaceAudience.Private
    public static class TopN extends PriorityQueue<NameValuePair>
    {
        private static final long serialVersionUID = 5134028249611535803L;
        private int n;
        private long total;
        
        public TopN(final int n) {
            super(n);
            this.total = 0L;
            this.n = n;
        }
        
        @Override
        public boolean offer(final NameValuePair entry) {
            this.updateTotal(entry.value);
            if (this.size() == this.n) {
                final NameValuePair smallest = this.peek();
                if (smallest.value >= entry.value) {
                    return false;
                }
                this.poll();
            }
            return super.offer(entry);
        }
        
        private void updateTotal(final long value) {
            this.total += value;
        }
        
        public long getTotal() {
            return this.total;
        }
    }
}
