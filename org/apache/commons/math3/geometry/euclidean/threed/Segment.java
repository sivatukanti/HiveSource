// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.threed;

public class Segment
{
    private final Vector3D start;
    private final Vector3D end;
    private final Line line;
    
    public Segment(final Vector3D start, final Vector3D end, final Line line) {
        this.start = start;
        this.end = end;
        this.line = line;
    }
    
    public Vector3D getStart() {
        return this.start;
    }
    
    public Vector3D getEnd() {
        return this.end;
    }
    
    public Line getLine() {
        return this.line;
    }
}
