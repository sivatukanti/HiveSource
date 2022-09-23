// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

class JdbcColumnAttributes
{
    public int precision;
    public int scale;
    
    public JdbcColumnAttributes() {
        this.precision = 0;
        this.scale = 0;
    }
    
    public JdbcColumnAttributes(final int precision, final int scale) {
        this.precision = 0;
        this.scale = 0;
        this.precision = precision;
        this.scale = scale;
    }
    
    @Override
    public String toString() {
        return "(" + this.precision + "," + this.scale + ")";
    }
}
