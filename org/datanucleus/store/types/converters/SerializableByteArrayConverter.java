// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.datanucleus.exceptions.NucleusException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class SerializableByteArrayConverter implements TypeConverter<Serializable, byte[]>
{
    @Override
    public byte[] toDatastoreType(final Serializable memberValue) {
        byte[] bytes = null;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(memberValue);
                bytes = baos.toByteArray();
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
            throw new NucleusException("Error serialising object of type " + memberValue.getClass().getName() + " to byte[]", ioe);
        }
        return bytes;
    }
    
    @Override
    public Serializable toMemberType(final byte[] datastoreValue) {
        Serializable obj = null;
        final ByteArrayInputStream bais = new ByteArrayInputStream(datastoreValue);
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
