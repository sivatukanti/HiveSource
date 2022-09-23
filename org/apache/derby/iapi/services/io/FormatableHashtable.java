// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.ObjectInput;
import java.io.IOException;
import java.util.Enumeration;
import java.io.ObjectOutput;
import java.util.Hashtable;

public class FormatableHashtable extends Hashtable implements Formatable
{
    public Object put(final Object o, final Object value) {
        if (value == null) {
            return this.remove(o);
        }
        return super.put(o, value);
    }
    
    public void putInt(final Object key, final int n) {
        super.put(key, new FormatableIntHolder(n));
    }
    
    public int getInt(final Object key) {
        return this.get(key).getInt();
    }
    
    public void putLong(final Object key, final long n) {
        super.put(key, new FormatableLongHolder(n));
    }
    
    public long getLong(final Object key) {
        return this.get(key).getLong();
    }
    
    public void putBoolean(final Object o, final boolean b) {
        this.putInt(o, b ? 1 : 0);
    }
    
    public boolean getBoolean(final Object o) {
        return this.getInt(o) != 0;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.size());
        final Enumeration<Object> keys = this.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            objectOutput.writeObject(nextElement);
            objectOutput.writeObject(this.get(nextElement));
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        for (int i = objectInput.readInt(); i > 0; --i) {
            super.put(objectInput.readObject(), objectInput.readObject());
        }
    }
    
    public int getTypeFormatId() {
        return 313;
    }
}
