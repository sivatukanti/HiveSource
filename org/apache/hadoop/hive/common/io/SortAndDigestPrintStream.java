// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.io;

import org.apache.commons.codec.binary.Base64;
import java.io.OutputStream;
import java.security.MessageDigest;

public class SortAndDigestPrintStream extends SortPrintStream
{
    private final MessageDigest digest;
    
    public SortAndDigestPrintStream(final OutputStream out, final String encoding) throws Exception {
        super(out, encoding);
        this.digest = MessageDigest.getInstance("MD5");
    }
    
    @Override
    public void processFinal() {
        while (!this.outputs.isEmpty()) {
            final String row = this.outputs.removeFirst();
            this.digest.update(row.getBytes());
            this.printDirect(row);
        }
        this.printDirect(new String(Base64.encodeBase64(this.digest.digest())));
        this.digest.reset();
    }
}
