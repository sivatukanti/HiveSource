// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import org.apache.hadoop.util.PerformanceAdvisory;
import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class OpensslCipher
{
    private static final Logger LOG;
    public static final int ENCRYPT_MODE = 1;
    public static final int DECRYPT_MODE = 0;
    private long context;
    private final int alg;
    private final int padding;
    private static final String loadingFailureReason;
    
    public static String getLoadingFailureReason() {
        return OpensslCipher.loadingFailureReason;
    }
    
    private OpensslCipher(final long context, final int alg, final int padding) {
        this.context = 0L;
        this.context = context;
        this.alg = alg;
        this.padding = padding;
    }
    
    public static final OpensslCipher getInstance(final String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException {
        final Transform transform = tokenizeTransformation(transformation);
        final int algMode = AlgMode.get(transform.alg, transform.mode);
        final int padding = Padding.get(transform.padding);
        final long context = initContext(algMode, padding);
        return new OpensslCipher(context, algMode, padding);
    }
    
    private static Transform tokenizeTransformation(final String transformation) throws NoSuchAlgorithmException {
        if (transformation == null) {
            throw new NoSuchAlgorithmException("No transformation given.");
        }
        String[] parts;
        int count;
        StringTokenizer parser;
        for (parts = new String[3], count = 0, parser = new StringTokenizer(transformation, "/"); parser.hasMoreTokens() && count < 3; parts[count++] = parser.nextToken().trim()) {}
        if (count != 3 || parser.hasMoreTokens()) {
            throw new NoSuchAlgorithmException("Invalid transformation format: " + transformation);
        }
        return new Transform(parts[0], parts[1], parts[2]);
    }
    
    public void init(final int mode, final byte[] key, final byte[] iv) {
        this.context = this.init(this.context, mode, this.alg, this.padding, key, iv);
    }
    
    public int update(final ByteBuffer input, final ByteBuffer output) throws ShortBufferException {
        this.checkState();
        Preconditions.checkArgument(input.isDirect() && output.isDirect(), (Object)"Direct buffers are required.");
        final int len = this.update(this.context, input, input.position(), input.remaining(), output, output.position(), output.remaining());
        input.position(input.limit());
        output.position(output.position() + len);
        return len;
    }
    
    public int doFinal(final ByteBuffer output) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        this.checkState();
        Preconditions.checkArgument(output.isDirect(), (Object)"Direct buffer is required.");
        final int len = this.doFinal(this.context, output, output.position(), output.remaining());
        output.position(output.position() + len);
        return len;
    }
    
    public void clean() {
        if (this.context != 0L) {
            this.clean(this.context);
            this.context = 0L;
        }
    }
    
    private void checkState() {
        Preconditions.checkState(this.context != 0L);
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.clean();
    }
    
    private static native void initIDs();
    
    private static native long initContext(final int p0, final int p1);
    
    private native long init(final long p0, final int p1, final int p2, final int p3, final byte[] p4, final byte[] p5);
    
    private native int update(final long p0, final ByteBuffer p1, final int p2, final int p3, final ByteBuffer p4, final int p5, final int p6);
    
    private native int doFinal(final long p0, final ByteBuffer p1, final int p2, final int p3);
    
    private native void clean(final long p0);
    
    public static native String getLibraryName();
    
    static {
        LOG = LoggerFactory.getLogger(OpensslCipher.class.getName());
        String loadingFailure = null;
        try {
            if (!NativeCodeLoader.buildSupportsOpenssl()) {
                PerformanceAdvisory.LOG.debug("Build does not support openssl");
                loadingFailure = "build does not support openssl.";
            }
            else {
                initIDs();
            }
        }
        catch (Throwable t) {
            loadingFailure = t.getMessage();
            OpensslCipher.LOG.debug("Failed to load OpenSSL Cipher.", t);
        }
        finally {
            loadingFailureReason = loadingFailure;
        }
    }
    
    private enum AlgMode
    {
        AES_CTR;
        
        static int get(final String algorithm, final String mode) throws NoSuchAlgorithmException {
            try {
                return valueOf(algorithm + "_" + mode).ordinal();
            }
            catch (Exception e) {
                throw new NoSuchAlgorithmException("Doesn't support algorithm: " + algorithm + " and mode: " + mode);
            }
        }
    }
    
    private enum Padding
    {
        NoPadding;
        
        static int get(final String padding) throws NoSuchPaddingException {
            try {
                return valueOf(padding).ordinal();
            }
            catch (Exception e) {
                throw new NoSuchPaddingException("Doesn't support padding: " + padding);
            }
        }
    }
    
    private static class Transform
    {
        final String alg;
        final String mode;
        final String padding;
        
        public Transform(final String alg, final String mode, final String padding) {
            this.alg = alg;
            this.mode = mode;
            this.padding = padding;
        }
    }
}
