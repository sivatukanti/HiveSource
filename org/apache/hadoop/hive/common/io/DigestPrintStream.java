// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.io;

import org.apache.commons.codec.binary.Base64;
import java.io.OutputStream;
import java.security.MessageDigest;

public class DigestPrintStream extends FetchConverter
{
    private final MessageDigest digest;
    
    public DigestPrintStream(final OutputStream out, final String encoding) throws Exception {
        super(out, false, encoding);
        this.digest = MessageDigest.getInstance("MD5");
    }
    
    @Override
    protected void process(final String out) {
        this.digest.update(out.getBytes());
    }
    
    public void processFinal() {
        this.printDirect(new String(Base64.encodeBase64(this.digest.digest())));
        this.digest.reset();
    }
}
