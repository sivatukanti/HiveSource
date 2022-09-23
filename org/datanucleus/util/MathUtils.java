// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.util.LinkedList;

public class MathUtils
{
    public static class SMA
    {
        private LinkedList values;
        private int length;
        private double sum;
        private double average;
        
        public SMA(final int length) {
            this.values = new LinkedList();
            this.sum = 0.0;
            this.average = 0.0;
            if (length <= 0) {
                throw new IllegalArgumentException("length must be greater than zero");
            }
            this.length = length;
        }
        
        public double currentAverage() {
            return this.average;
        }
        
        public synchronized double compute(final double value) {
            if (this.values.size() == this.length && this.length > 0) {
                this.sum -= this.values.getFirst();
                this.values.removeFirst();
            }
            this.sum += value;
            this.values.addLast(new Double(value));
            return this.average = this.sum / this.values.size();
        }
    }
}
