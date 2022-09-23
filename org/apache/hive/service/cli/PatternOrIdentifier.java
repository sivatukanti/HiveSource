// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

public class PatternOrIdentifier
{
    boolean isPattern;
    String text;
    
    public PatternOrIdentifier(final String tpoi) {
        this.isPattern = false;
        this.text = tpoi;
        this.isPattern = false;
    }
    
    public boolean isPattern() {
        return this.isPattern;
    }
    
    public boolean isIdentifier() {
        return !this.isPattern;
    }
    
    @Override
    public String toString() {
        return this.text;
    }
}
