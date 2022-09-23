// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.sink.ganglia;

class GangliaConf
{
    private String units;
    private AbstractGangliaSink.GangliaSlope slope;
    private int dmax;
    private int tmax;
    
    GangliaConf() {
        this.units = "";
        this.dmax = 0;
        this.tmax = 60;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("unit=").append(this.units).append(", slope=").append(this.slope).append(", dmax=").append(this.dmax).append(", tmax=").append(this.tmax);
        return buf.toString();
    }
    
    String getUnits() {
        return this.units;
    }
    
    void setUnits(final String units) {
        this.units = units;
    }
    
    AbstractGangliaSink.GangliaSlope getSlope() {
        return this.slope;
    }
    
    void setSlope(final AbstractGangliaSink.GangliaSlope slope) {
        this.slope = slope;
    }
    
    int getDmax() {
        return this.dmax;
    }
    
    void setDmax(final int dmax) {
        this.dmax = dmax;
    }
    
    int getTmax() {
        return this.tmax;
    }
    
    void setTmax(final int tmax) {
        this.tmax = tmax;
    }
}
