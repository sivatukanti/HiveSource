// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import java.math.RoundingMode;
import java.math.BigDecimal;

public enum StorageUnit
{
    EB {
        @Override
        public double toBytes(final double value) {
            return multiply(value, 1.15292150460684698E18);
        }
        
        @Override
        public double toKBs(final double value) {
            return multiply(value, 1.125899906842624E15);
        }
        
        @Override
        public double toMBs(final double value) {
            return multiply(value, 1.099511627776E12);
        }
        
        @Override
        public double toGBs(final double value) {
            return multiply(value, 1.073741824E9);
        }
        
        @Override
        public double toTBs(final double value) {
            return multiply(value, 1048576.0);
        }
        
        @Override
        public double toPBs(final double value) {
            return multiply(value, 1024.0);
        }
        
        @Override
        public double toEBs(final double value) {
            return value;
        }
        
        @Override
        public String getLongName() {
            return "exabytes";
        }
        
        @Override
        public String getShortName() {
            return "eb";
        }
        
        @Override
        public String getSuffixChar() {
            return "e";
        }
        
        @Override
        public double getDefault(final double value) {
            return this.toEBs(value);
        }
        
        @Override
        public double fromBytes(final double value) {
            return divide(value, 1.15292150460684698E18);
        }
    }, 
    PB {
        @Override
        public double toBytes(final double value) {
            return multiply(value, 1.125899906842624E15);
        }
        
        @Override
        public double toKBs(final double value) {
            return multiply(value, 1.099511627776E12);
        }
        
        @Override
        public double toMBs(final double value) {
            return multiply(value, 1.073741824E9);
        }
        
        @Override
        public double toGBs(final double value) {
            return multiply(value, 1048576.0);
        }
        
        @Override
        public double toTBs(final double value) {
            return multiply(value, 1024.0);
        }
        
        @Override
        public double toPBs(final double value) {
            return value;
        }
        
        @Override
        public double toEBs(final double value) {
            return divide(value, 1024.0);
        }
        
        @Override
        public String getLongName() {
            return "petabytes";
        }
        
        @Override
        public String getShortName() {
            return "pb";
        }
        
        @Override
        public String getSuffixChar() {
            return "p";
        }
        
        @Override
        public double getDefault(final double value) {
            return this.toPBs(value);
        }
        
        @Override
        public double fromBytes(final double value) {
            return divide(value, 1.125899906842624E15);
        }
    }, 
    TB {
        @Override
        public double toBytes(final double value) {
            return multiply(value, 1.099511627776E12);
        }
        
        @Override
        public double toKBs(final double value) {
            return multiply(value, 1.073741824E9);
        }
        
        @Override
        public double toMBs(final double value) {
            return multiply(value, 1048576.0);
        }
        
        @Override
        public double toGBs(final double value) {
            return multiply(value, 1024.0);
        }
        
        @Override
        public double toTBs(final double value) {
            return value;
        }
        
        @Override
        public double toPBs(final double value) {
            return divide(value, 1024.0);
        }
        
        @Override
        public double toEBs(final double value) {
            return divide(value, 1048576.0);
        }
        
        @Override
        public String getLongName() {
            return "terabytes";
        }
        
        @Override
        public String getShortName() {
            return "tb";
        }
        
        @Override
        public String getSuffixChar() {
            return "t";
        }
        
        @Override
        public double getDefault(final double value) {
            return this.toTBs(value);
        }
        
        @Override
        public double fromBytes(final double value) {
            return divide(value, 1.099511627776E12);
        }
    }, 
    GB {
        @Override
        public double toBytes(final double value) {
            return multiply(value, 1.073741824E9);
        }
        
        @Override
        public double toKBs(final double value) {
            return multiply(value, 1048576.0);
        }
        
        @Override
        public double toMBs(final double value) {
            return multiply(value, 1024.0);
        }
        
        @Override
        public double toGBs(final double value) {
            return value;
        }
        
        @Override
        public double toTBs(final double value) {
            return divide(value, 1024.0);
        }
        
        @Override
        public double toPBs(final double value) {
            return divide(value, 1048576.0);
        }
        
        @Override
        public double toEBs(final double value) {
            return divide(value, 1.073741824E9);
        }
        
        @Override
        public String getLongName() {
            return "gigabytes";
        }
        
        @Override
        public String getShortName() {
            return "gb";
        }
        
        @Override
        public String getSuffixChar() {
            return "g";
        }
        
        @Override
        public double getDefault(final double value) {
            return this.toGBs(value);
        }
        
        @Override
        public double fromBytes(final double value) {
            return divide(value, 1.073741824E9);
        }
    }, 
    MB {
        @Override
        public double toBytes(final double value) {
            return multiply(value, 1048576.0);
        }
        
        @Override
        public double toKBs(final double value) {
            return multiply(value, 1024.0);
        }
        
        @Override
        public double toMBs(final double value) {
            return value;
        }
        
        @Override
        public double toGBs(final double value) {
            return divide(value, 1024.0);
        }
        
        @Override
        public double toTBs(final double value) {
            return divide(value, 1048576.0);
        }
        
        @Override
        public double toPBs(final double value) {
            return divide(value, 1.073741824E9);
        }
        
        @Override
        public double toEBs(final double value) {
            return divide(value, 1.099511627776E12);
        }
        
        @Override
        public String getLongName() {
            return "megabytes";
        }
        
        @Override
        public String getShortName() {
            return "mb";
        }
        
        @Override
        public String getSuffixChar() {
            return "m";
        }
        
        @Override
        public double fromBytes(final double value) {
            return divide(value, 1048576.0);
        }
        
        @Override
        public double getDefault(final double value) {
            return this.toMBs(value);
        }
    }, 
    KB {
        @Override
        public double toBytes(final double value) {
            return multiply(value, 1024.0);
        }
        
        @Override
        public double toKBs(final double value) {
            return value;
        }
        
        @Override
        public double toMBs(final double value) {
            return divide(value, 1024.0);
        }
        
        @Override
        public double toGBs(final double value) {
            return divide(value, 1048576.0);
        }
        
        @Override
        public double toTBs(final double value) {
            return divide(value, 1.073741824E9);
        }
        
        @Override
        public double toPBs(final double value) {
            return divide(value, 1.099511627776E12);
        }
        
        @Override
        public double toEBs(final double value) {
            return divide(value, 1.125899906842624E15);
        }
        
        @Override
        public String getLongName() {
            return "kilobytes";
        }
        
        @Override
        public String getShortName() {
            return "kb";
        }
        
        @Override
        public String getSuffixChar() {
            return "k";
        }
        
        @Override
        public double getDefault(final double value) {
            return this.toKBs(value);
        }
        
        @Override
        public double fromBytes(final double value) {
            return divide(value, 1024.0);
        }
    }, 
    BYTES {
        @Override
        public double toBytes(final double value) {
            return value;
        }
        
        @Override
        public double toKBs(final double value) {
            return divide(value, 1024.0);
        }
        
        @Override
        public double toMBs(final double value) {
            return divide(value, 1048576.0);
        }
        
        @Override
        public double toGBs(final double value) {
            return divide(value, 1.073741824E9);
        }
        
        @Override
        public double toTBs(final double value) {
            return divide(value, 1.099511627776E12);
        }
        
        @Override
        public double toPBs(final double value) {
            return divide(value, 1.125899906842624E15);
        }
        
        @Override
        public double toEBs(final double value) {
            return divide(value, 1.15292150460684698E18);
        }
        
        @Override
        public String getLongName() {
            return "bytes";
        }
        
        @Override
        public String getShortName() {
            return "b";
        }
        
        @Override
        public String getSuffixChar() {
            return "b";
        }
        
        @Override
        public double getDefault(final double value) {
            return this.toBytes(value);
        }
        
        @Override
        public double fromBytes(final double value) {
            return value;
        }
    };
    
    private static final double BYTE = 1.0;
    private static final double KILOBYTES = 1024.0;
    private static final double MEGABYTES = 1048576.0;
    private static final double GIGABYTES = 1.073741824E9;
    private static final double TERABYTES = 1.099511627776E12;
    private static final double PETABYTES = 1.125899906842624E15;
    private static final double EXABYTES = 1.15292150460684698E18;
    private static final int PRECISION = 4;
    
    private static double divide(final double value, final double divisor) {
        final BigDecimal val = new BigDecimal(value);
        final BigDecimal bDivisor = new BigDecimal(divisor);
        return val.divide(bDivisor).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }
    
    private static double multiply(final double first, final double second) {
        final BigDecimal firstVal = new BigDecimal(first);
        final BigDecimal secondVal = new BigDecimal(second);
        return firstVal.multiply(secondVal).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }
    
    public abstract double toBytes(final double p0);
    
    public abstract double toKBs(final double p0);
    
    public abstract double toMBs(final double p0);
    
    public abstract double toGBs(final double p0);
    
    public abstract double toTBs(final double p0);
    
    public abstract double toPBs(final double p0);
    
    public abstract double toEBs(final double p0);
    
    public abstract String getLongName();
    
    public abstract String getShortName();
    
    public abstract String getSuffixChar();
    
    public abstract double getDefault(final double p0);
    
    public abstract double fromBytes(final double p0);
    
    @Override
    public String toString() {
        return this.getLongName();
    }
}
