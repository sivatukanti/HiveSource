// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "Common", "HDFS", "MapReduce", "Yarn" })
@InterfaceStability.Unstable
public class CrcComposer
{
    private static final int CRC_SIZE_BYTES = 4;
    private static final Logger LOG;
    private final int crcPolynomial;
    private final int precomputedMonomialForHint;
    private final long bytesPerCrcHint;
    private final long stripeLength;
    private int curCompositeCrc;
    private long curPositionInStripe;
    private ByteArrayOutputStream digestOut;
    
    public static CrcComposer newCrcComposer(final DataChecksum.Type type, final long bytesPerCrcHint) throws IOException {
        return newStripedCrcComposer(type, bytesPerCrcHint, Long.MAX_VALUE);
    }
    
    public static CrcComposer newStripedCrcComposer(final DataChecksum.Type type, final long bytesPerCrcHint, final long stripeLength) throws IOException {
        final int polynomial = DataChecksum.getCrcPolynomialForType(type);
        return new CrcComposer(polynomial, CrcUtil.getMonomial(bytesPerCrcHint, polynomial), bytesPerCrcHint, stripeLength);
    }
    
    CrcComposer(final int crcPolynomial, final int precomputedMonomialForHint, final long bytesPerCrcHint, final long stripeLength) {
        this.curCompositeCrc = 0;
        this.curPositionInStripe = 0L;
        this.digestOut = new ByteArrayOutputStream();
        CrcComposer.LOG.debug("crcPolynomial=0x{}, precomputedMonomialForHint=0x{}, bytesPerCrcHint={}, stripeLength={}", Integer.toString(crcPolynomial, 16), Integer.toString(precomputedMonomialForHint, 16), bytesPerCrcHint, stripeLength);
        this.crcPolynomial = crcPolynomial;
        this.precomputedMonomialForHint = precomputedMonomialForHint;
        this.bytesPerCrcHint = bytesPerCrcHint;
        this.stripeLength = stripeLength;
    }
    
    public void update(final byte[] crcBuffer, int offset, final int length, final long bytesPerCrc) throws IOException {
        if (length % 4 != 0) {
            throw new IOException(String.format("Trying to update CRC from byte array with length '%d' at offset '%d' which is not a multiple of %d!", length, offset, 4));
        }
        for (int limit = offset + length; offset < limit; offset += 4) {
            final int crcB = CrcUtil.readInt(crcBuffer, offset);
            this.update(crcB, bytesPerCrc);
        }
    }
    
    public void update(final DataInputStream checksumIn, final long numChecksumsToRead, final long bytesPerCrc) throws IOException {
        for (long i = 0L; i < numChecksumsToRead; ++i) {
            final int crcB = checksumIn.readInt();
            this.update(crcB, bytesPerCrc);
        }
    }
    
    public void update(final int crcB, final long bytesPerCrc) throws IOException {
        if (this.curCompositeCrc == 0) {
            this.curCompositeCrc = crcB;
        }
        else if (bytesPerCrc == this.bytesPerCrcHint) {
            this.curCompositeCrc = CrcUtil.composeWithMonomial(this.curCompositeCrc, crcB, this.precomputedMonomialForHint, this.crcPolynomial);
        }
        else {
            this.curCompositeCrc = CrcUtil.compose(this.curCompositeCrc, crcB, bytesPerCrc, this.crcPolynomial);
        }
        this.curPositionInStripe += bytesPerCrc;
        if (this.curPositionInStripe > this.stripeLength) {
            throw new IOException(String.format("Current position in stripe '%d' after advancing by bytesPerCrc '%d' exceeds stripeLength '%d' without stripe alignment.", this.curPositionInStripe, bytesPerCrc, this.stripeLength));
        }
        if (this.curPositionInStripe == this.stripeLength) {
            this.digestOut.write(CrcUtil.intToBytes(this.curCompositeCrc), 0, 4);
            this.curCompositeCrc = 0;
            this.curPositionInStripe = 0L;
        }
    }
    
    public byte[] digest() {
        if (this.curPositionInStripe > 0L) {
            this.digestOut.write(CrcUtil.intToBytes(this.curCompositeCrc), 0, 4);
            this.curCompositeCrc = 0;
            this.curPositionInStripe = 0L;
        }
        final byte[] digestValue = this.digestOut.toByteArray();
        this.digestOut.reset();
        return digestValue;
    }
    
    static {
        LOG = LoggerFactory.getLogger(CrcComposer.class);
    }
}
