// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.Base64;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class SerializableStringConverter implements TypeConverter<Serializable, String>
{
    @Override
    public String toDatastoreType(final Serializable memberValue) {
        String str = null;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(memberValue);
                str = new String(Base64.encode(baos.toByteArray()));
            }
            finally {
                try {
                    baos.close();
                }
                finally {
                    if (oos != null) {
                        oos.close();
                    }
                }
            }
        }
        catch (IOException ioe) {
            throw new NucleusException("Error serialising object of type " + memberValue.getClass().getName() + " to String", ioe);
        }
        return str;
    }
    
    @Override
    public Serializable toMemberType(final String datastoreValue) {
        final byte[] bytes = Base64.decode(datastoreValue);
        Serializable obj = null;
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            try {
                ois = new ObjectInputStream(bais);
                obj = (Serializable)ois.readObject();
            }
            finally {
                try {
                    bais.close();
                }
                finally {
                    if (ois != null) {
                        ois.close();
                    }
                }
            }
        }
        catch (Exception e) {
            throw new NucleusException("Error deserialising " + datastoreValue, e);
        }
        return obj;
    }
}
