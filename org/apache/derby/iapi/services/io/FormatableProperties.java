// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.ObjectInput;
import java.io.IOException;
import java.util.Enumeration;
import java.io.ObjectOutput;
import java.util.Properties;

public class FormatableProperties extends Properties implements Formatable
{
    public FormatableProperties() {
        this((Properties)null);
    }
    
    public FormatableProperties(final Properties defaults) {
        super(defaults);
    }
    
    public void clearDefaults() {
        this.defaults = null;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.size());
        final Enumeration<Object> keys = this.keys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            objectOutput.writeUTF(key);
            objectOutput.writeUTF(this.getProperty(key));
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        for (int i = objectInput.readInt(); i > 0; --i) {
            this.put(objectInput.readUTF(), objectInput.readUTF());
        }
    }
    
    public int getTypeFormatId() {
        return 271;
    }
}
