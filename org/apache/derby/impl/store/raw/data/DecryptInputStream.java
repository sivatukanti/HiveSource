// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.IOException;
import java.io.InputStream;
import org.apache.derby.iapi.store.raw.data.DataFactory;

public class DecryptInputStream extends BufferedByteHolderInputStream
{
    protected DataFactory dataFactory;
    protected InputStream in;
    
    public DecryptInputStream(final InputStream in, final ByteHolder byteHolder, final DataFactory dataFactory) throws IOException {
        super(byteHolder);
        this.in = in;
        this.dataFactory = dataFactory;
        this.fillByteHolder();
    }
    
    public void fillByteHolder() throws IOException {
        if (this.bh.available() == 0) {
            this.bh.clear();
            try {
                final int int1 = CompressedNumber.readInt(this.in);
                if (int1 == -1) {
                    return;
                }
                final int n = int1 % this.dataFactory.getEncryptionBlockSize();
                final int n2 = (n == 0) ? 0 : (this.dataFactory.getEncryptionBlockSize() - n);
                final int len = int1 + n2;
                final byte[] b = new byte[len];
                this.in.read(b, 0, len);
                final byte[] array = new byte[len];
                this.dataFactory.decrypt(b, 0, len, array, 0);
                this.bh.write(array, n2, int1);
            }
            catch (StandardException ex) {
                throw new IOException();
            }
            this.bh.startReading();
        }
    }
}
