// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell.find;

public final class Result
{
    public static final Result PASS;
    public static final Result FAIL;
    public static final Result STOP;
    private boolean descend;
    private boolean success;
    
    private Result(final boolean success, final boolean recurse) {
        this.success = success;
        this.descend = recurse;
    }
    
    public boolean isDescend() {
        return this.descend;
    }
    
    public boolean isPass() {
        return this.success;
    }
    
    public Result combine(final Result other) {
        return new Result(this.isPass() && other.isPass(), this.isDescend() && other.isDescend());
    }
    
    public Result negate() {
        return new Result(!this.isPass(), this.isDescend());
    }
    
    @Override
    public String toString() {
        return "success=" + this.isPass() + "; recurse=" + this.isDescend();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.descend ? 1231 : 1237);
        result = 31 * result + (this.success ? 1231 : 1237);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Result other = (Result)obj;
        return this.descend == other.descend && this.success == other.success;
    }
    
    static {
        PASS = new Result(true, true);
        FAIL = new Result(false, true);
        STOP = new Result(true, false);
    }
}
