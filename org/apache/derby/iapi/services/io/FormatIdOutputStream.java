// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.ObjectOutput;
import java.io.DataOutputStream;

public class FormatIdOutputStream extends DataOutputStream implements ObjectOutput, ErrorInfo
{
    public FormatIdOutputStream(final OutputStream out) {
        super(out);
    }
    
    public void writeObject(final Object obj) throws IOException {
        if (obj == null) {
            FormatIdUtil.writeFormatIdInteger(this, 0);
            return;
        }
        if (obj instanceof String && ((String)obj).length() <= 20000) {
            FormatIdUtil.writeFormatIdInteger(this, 1);
            this.writeUTF((String)obj);
            return;
        }
        if (obj instanceof Storable) {
            final Storable storable = (Storable)obj;
            final int typeFormatId = storable.getTypeFormatId();
            if (typeFormatId != 2) {
                FormatIdUtil.writeFormatIdInteger(this, typeFormatId);
                final boolean null = storable.isNull();
                this.writeBoolean(null);
                if (!null) {
                    storable.writeExternal(this);
                }
                return;
            }
        }
        else if (obj instanceof Formatable) {
            final Formatable formatable = (Formatable)obj;
            final int typeFormatId2 = formatable.getTypeFormatId();
            if (typeFormatId2 != 2) {
                FormatIdUtil.writeFormatIdInteger(this, typeFormatId2);
                formatable.writeExternal(this);
                return;
            }
        }
        FormatIdUtil.writeFormatIdInteger(this, 2);
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(this);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
    }
    
    public void setOutput(final OutputStream out) {
        this.out = out;
        this.written = 0;
    }
    
    public String getErrorInfo() {
        return null;
    }
    
    public Exception getNestedException() {
        return null;
    }
}
