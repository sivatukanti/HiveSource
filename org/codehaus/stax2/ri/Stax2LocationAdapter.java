// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import javax.xml.stream.Location;
import org.codehaus.stax2.XMLStreamLocation2;

public class Stax2LocationAdapter implements XMLStreamLocation2
{
    protected final Location mWrappedLocation;
    protected final Location mParentLocation;
    
    public Stax2LocationAdapter(final Location location) {
        this(location, null);
    }
    
    public Stax2LocationAdapter(final Location mWrappedLocation, final Location mParentLocation) {
        this.mWrappedLocation = mWrappedLocation;
        this.mParentLocation = mParentLocation;
    }
    
    public int getCharacterOffset() {
        return this.mWrappedLocation.getCharacterOffset();
    }
    
    public int getColumnNumber() {
        return this.mWrappedLocation.getColumnNumber();
    }
    
    public int getLineNumber() {
        return this.mWrappedLocation.getLineNumber();
    }
    
    public String getPublicId() {
        return this.mWrappedLocation.getPublicId();
    }
    
    public String getSystemId() {
        return this.mWrappedLocation.getSystemId();
    }
    
    public XMLStreamLocation2 getContext() {
        if (this.mParentLocation == null) {
            return null;
        }
        if (this.mParentLocation instanceof XMLStreamLocation2) {
            return (XMLStreamLocation2)this.mParentLocation;
        }
        return new Stax2LocationAdapter(this.mParentLocation);
    }
}
