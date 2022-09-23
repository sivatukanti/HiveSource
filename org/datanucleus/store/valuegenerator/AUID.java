// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import java.util.GregorianCalendar;
import org.datanucleus.util.NucleusLogger;
import java.util.Date;
import java.util.Random;

class AUID implements Comparable
{
    private static final int VERSION_RANDOM_NODE = 3;
    private static final int VARIANT_NCS = 0;
    private static final int VARIANT_DCE = 32768;
    private static final int VARIANT_MICROSOFT = 49152;
    private static final int VARIANT_RESERVED = 57344;
    private static final int CS_MASK_NCS = 32767;
    private static final int CS_MASK_DCE = 16383;
    private static final int CS_MASK_MICROSOFT = 8191;
    private static final int CS_MASK_RESERVED = 8191;
    private static final long MAXIMUM_ENTROPIC_TIME_MS = 5000L;
    private static final long TIME_SCALE = 10000L;
    private static final long UTC_OFFSET;
    private static final char[] HEX_CHARS;
    private static State auidState;
    private long firstHalf;
    private long secondHalf;
    
    public AUID() {
        this.makeUnique(0, false);
    }
    
    protected AUID(final int securityAttributes) {
        this.makeUnique(securityAttributes, true);
    }
    
    protected AUID(final long time, final int version, final int clockSeq, final int variant, final long node) {
        this.packFirstHalf(time, version);
        this.packSecondHalf(clockSeq, variant, node);
    }
    
    protected AUID(final long timeLow, final long timeMid, final long timeHiAndVersion, final int clockSeqHiAndVariant, final int clockSeqLow, final long node) {
        this.packDCEFieldsFirstHalf(timeLow, timeMid, timeHiAndVersion);
        this.packDCEFieldsSecondHalf(clockSeqHiAndVariant, clockSeqLow, node);
    }
    
    protected AUID(final AUID auid) {
        this.firstHalf = auid.firstHalf;
        this.secondHalf = auid.secondHalf;
    }
    
    public AUID(final String auid) {
        try {
            this.firstHalf = this.parseFirstHalf(auid.subSequence(0, 18));
            this.secondHalf = this.parseSecondHalf(auid.subSequence(18, 36));
        }
        catch (IndexOutOfBoundsException ioobe) {
            throw new NumberFormatException();
        }
        catch (NumberFormatException nfe) {
            throw new NumberFormatException();
        }
    }
    
    public AUID(final CharSequence auid) {
        try {
            this.firstHalf = this.parseFirstHalf(auid.subSequence(0, 18));
            this.secondHalf = this.parseSecondHalf(auid.subSequence(18, 36));
        }
        catch (IndexOutOfBoundsException ioobe) {
            throw new NumberFormatException();
        }
        catch (NumberFormatException nfe) {
            throw new NumberFormatException();
        }
    }
    
    public AUID(final byte[] bytes) {
        this(bytes, 0);
    }
    
    public AUID(final byte[] bytes, final int offset) {
        final long timeLow = getOctets(4, bytes, 0 + offset, true);
        final long timeMid = getOctets(2, bytes, 4 + offset, true);
        final long timeHAV = getOctets(2, bytes, 6 + offset, true);
        final int csHAV = (int)getOctets(1, bytes, 8 + offset, true);
        final int csLow = (int)getOctets(1, bytes, 9 + offset, true);
        final long node = getOctets(6, bytes, 10 + offset, true);
        this.packDCEFieldsFirstHalf(timeLow, timeMid, timeHAV);
        this.packDCEFieldsSecondHalf(csHAV, csLow, node);
    }
    
    public static AUID parse(final String auid) {
        return new AUID(auid);
    }
    
    public static AUID parse(final CharSequence auid) {
        return new AUID(auid);
    }
    
    protected int identifyVariant(final int clockSeqAndVariant) {
        if ((clockSeqAndVariant & 0xFFFF8000) == 0x0) {
            return 0;
        }
        if ((clockSeqAndVariant & 0xFFFFC000) == 0x8000) {
            return 32768;
        }
        if ((clockSeqAndVariant & 0xFFFFE000) == 0xC000) {
            return 0;
        }
        if ((clockSeqAndVariant & 0xFFFFE000) == 0xE000) {
            return 57344;
        }
        throw new IllegalArgumentException();
    }
    
    public int getClockSeqMaskForVariant(final int variant) {
        switch (variant) {
            case 0: {
                return 32767;
            }
            case 32768: {
                return 16383;
            }
            case 49152: {
                return 8191;
            }
            case 57344: {
                return 8191;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    protected long getCurrentTime() {
        return System.currentTimeMillis() * 10000L - AUID.UTC_OFFSET;
    }
    
    protected State loadState(State state) {
        State loadInto = state;
        if (loadInto == null) {
            if (AUID.auidState == null) {
                loadInto = (AUID.auidState = new State());
            }
            state = AUID.auidState;
        }
        if (loadInto != null) {
            if (loadInto.getRandom() == null) {
                loadInto.setRandom(new Random(entropicSeed(32, System.currentTimeMillis())));
            }
            loadInto.setLastTime(this.getCurrentTime());
            loadInto.setAdjustTime(0L);
            loadInto.setClockSequence(loadInto.getRandom().nextInt());
            loadInto.setNode(loadInto.getRandom().nextLong() & 0xFFFFFFFFFFFFL);
            loadInto.setVersion(3);
            loadInto.setVariant(32768);
            loadInto.setIncludeSecurityAttributes(false);
        }
        return state;
    }
    
    protected void saveState(final State state) {
    }
    
    protected byte[] getBytes(byte[] dst, int dstBegin, final boolean bigendian) {
        if (dst == null) {
            dst = new byte[16];
            dstBegin = 0;
        }
        putOctets(this.getTimeLow(), 4, dst, dstBegin, bigendian);
        putOctets(this.getTimeMid(), 2, dst, dstBegin + 4, bigendian);
        putOctets(this.getTimeHighAndVersion(), 2, dst, dstBegin + 6, bigendian);
        putOctets(this.getClockSeqHighAndVariant(), 1, dst, dstBegin + 8, bigendian);
        putOctets(this.getClockSeqLow(), 1, dst, dstBegin + 9, bigendian);
        putOctets(this.getNode(), 6, dst, dstBegin + 10, bigendian);
        return dst;
    }
    
    public byte[] getBytes(final byte[] dst, final int dstBegin) {
        return this.getBytes(dst, dstBegin, true);
    }
    
    public StringBuffer toStringBuffer(StringBuffer sb) {
        if (sb == null) {
            sb = new StringBuffer();
        }
        this.toHex(sb, this.getTimeLow(), 8);
        sb.append('-');
        this.toHex(sb, this.getTimeMid(), 4);
        sb.append('-');
        this.toHex(sb, this.getTimeHighAndVersion(), 4);
        sb.append('-');
        this.toHex(sb, this.getClockSeqAndVariant(), 4);
        sb.append('-');
        this.toHex(sb, this.getNode(), 12);
        return sb;
    }
    
    private void packFirstHalf(final long time, final int version) {
        this.firstHalf = ((long)version << 60 | (time & 0xFFFFFFFFFFFFFFFL));
    }
    
    private void packDCEFieldsFirstHalf(final long timeLow, final long timeMid, final long timeHiAndVersion) {
        this.firstHalf = (timeHiAndVersion << 48 | timeMid << 32 | timeLow);
    }
    
    private void packSecondHalf(final int clockSeq, final int variant, final long node) {
        final int csMasked = clockSeq & this.getClockSeqMaskForVariant(variant);
        final int csLow = csMasked & 0xFF;
        final int csHigh = (variant | csMasked) >>> 8;
        this.secondHalf = (node << 16 | (long)(csLow << 8) | (long)csHigh);
    }
    
    private void packDCEFieldsSecondHalf(final int clockSeqHiAndVariant, final int clockSeqLow, final long node) {
        this.secondHalf = (node << 16 | (long)(clockSeqLow << 8) | (long)clockSeqHiAndVariant);
    }
    
    private void makeUnique(final int securityAttributes, final boolean hasSecurityAttributes) {
        synchronized (AUID.class) {
            final State state = this.loadState(null);
            long now = this.getCurrentTime();
            if (now < state.getLastTime()) {
                state.setClockSequence(state.getClockSequence() + 1);
                state.setAdjustTime(0L);
                state.setLastTime(now);
            }
            else if (now != state.getLastTime()) {
                if (now < state.getLastTime() + state.getAdjustTime()) {
                    throw new IllegalStateException("Clock overrun occured.");
                }
                state.setAdjustTime(0L);
                state.setLastTime(now);
            }
            now += state.incrementAdjustTime();
            if (state.getIncludeSecurityAttributes()) {
                if (!hasSecurityAttributes) {
                    throw new IllegalArgumentException("Required to include security attributes as declared in state.");
                }
                now = ((now & 0xFFFFFFFF00000000L) | (long)securityAttributes);
            }
            else if (hasSecurityAttributes) {
                throw new IllegalArgumentException("Cannot include security attributes if not declared in state.");
            }
            this.packFirstHalf(now, state.getVersion());
            this.packSecondHalf(state.getClockSequence(), state.getVariant(), state.getNode());
            this.saveState(state);
        }
    }
    
    private void toHex(final StringBuffer result, final long value, final int nibbles) {
        if (nibbles > 0) {
            this.toHex(result, value >>> 4, nibbles - 1);
            result.append(AUID.HEX_CHARS[(int)value & 0xF]);
        }
    }
    
    private long parseNibble(final char c) {
        switch (c) {
            case '0': {
                return 0L;
            }
            case '1': {
                return 1L;
            }
            case '2': {
                return 2L;
            }
            case '3': {
                return 3L;
            }
            case '4': {
                return 4L;
            }
            case '5': {
                return 5L;
            }
            case '6': {
                return 6L;
            }
            case '7': {
                return 7L;
            }
            case '8': {
                return 8L;
            }
            case '9': {
                return 9L;
            }
            case 'A':
            case 'a': {
                return 10L;
            }
            case 'B':
            case 'b': {
                return 11L;
            }
            case 'C':
            case 'c': {
                return 12L;
            }
            case 'D':
            case 'd': {
                return 13L;
            }
            case 'E':
            case 'e': {
                return 14L;
            }
            case 'F':
            case 'f': {
                return 15L;
            }
            default: {
                throw new NumberFormatException();
            }
        }
    }
    
    private void parseHyphen(final char c) {
        if (c != '-') {
            throw new NumberFormatException();
        }
    }
    
    private long parseHex(final CharSequence cs) {
        long retval = 0L;
        for (int i = 0; i < cs.length(); ++i) {
            retval = (retval << 4) + this.parseNibble(cs.charAt(i));
        }
        return retval;
    }
    
    private long parseFirstHalf(final CharSequence charSequence) {
        final long timeLow = this.parseHex(charSequence.subSequence(0, 8));
        this.parseHyphen(charSequence.charAt(8));
        final long timeMid = this.parseHex(charSequence.subSequence(9, 13));
        this.parseHyphen(charSequence.charAt(13));
        final long timeHi = this.parseHex(charSequence.subSequence(14, 18));
        return timeHi << 48 | timeMid << 32 | timeLow;
    }
    
    private long parseSecondHalf(final CharSequence charSequence) {
        this.parseHyphen(charSequence.charAt(0));
        final long clockSeq = this.parseHex(charSequence.subSequence(1, 5));
        this.parseHyphen(charSequence.charAt(5));
        final long node = this.parseHex(charSequence.subSequence(6, 18));
        return node << 16 | (clockSeq & 0xFFL) << 8 | clockSeq >>> 8;
    }
    
    protected static final long getOctets(final int octets, final byte[] bytes, final int begin, final boolean bigendian) {
        if (octets <= 1) {
            return (long)bytes[begin] & 0xFFL;
        }
        if (bigendian) {
            return ((long)bytes[begin] & 0xFFL) << 8 * (octets - 1) | getOctets(octets - 1, bytes, begin + 1, bigendian);
        }
        return getOctets(octets - 1, bytes, begin, bigendian) | ((long)bytes[begin + octets - 1] & 0xFFL) << 8 * (octets - 1);
    }
    
    protected static final void putOctets(final long value, final int octets, final byte[] dst, final int dstBegin, final boolean bigendian) {
        if (bigendian) {
            if (octets > 1) {
                putOctets(value >>> 8, octets - 1, dst, dstBegin, bigendian);
            }
            dst[dstBegin + octets - 1] = (byte)(value & 0xFFL);
        }
        else {
            dst[dstBegin] = (byte)(value & 0xFFL);
            if (octets > 1) {
                putOctets(value >>> 8, octets - 1, dst, dstBegin + 1, bigendian);
            }
        }
    }
    
    public final long getTimeLow() {
        return this.firstHalf & -1L;
    }
    
    public final long getTimeMid() {
        return this.firstHalf >>> 32 & 0xFFFFL;
    }
    
    public final long getTimeHigh() {
        return this.firstHalf >>> 48 & 0xFFFL;
    }
    
    public final long getTimeHighAndVersion() {
        return this.firstHalf >>> 48;
    }
    
    public final long getTime() {
        return this.firstHalf & 0xFFFFFFFFFFFFFFFL;
    }
    
    public final Date getDate() {
        return new Date((this.getTime() + AUID.UTC_OFFSET) / 10000L);
    }
    
    public final long getNanos() {
        return (this.getTime() + AUID.UTC_OFFSET) % 10000L;
    }
    
    public final int getVersion() {
        return (int)(this.firstHalf >>> 60);
    }
    
    public final int getClockSeqHighAndVariant() {
        return (int)(this.secondHalf & 0xFFL);
    }
    
    public final int getClockSeqLow() {
        return (int)(this.secondHalf >>> 8 & 0xFFL);
    }
    
    public final int getClockSeqAndVariant() {
        return this.getClockSeqHighAndVariant() << 8 | this.getClockSeqLow();
    }
    
    public final int getClockSeq() {
        final int csv = this.getClockSeqAndVariant();
        return csv & this.getClockSeqMaskForVariant(this.identifyVariant(csv));
    }
    
    public final int getVariant() {
        return this.identifyVariant(this.getClockSeqAndVariant());
    }
    
    public final long getNode() {
        return this.secondHalf >>> 16;
    }
    
    public final byte[] getBytes() {
        return this.getBytes(null, 0);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof AUID) {
            final AUID other = (AUID)obj;
            return this.firstHalf == other.firstHalf && this.secondHalf == other.secondHalf;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.firstHalf ^ this.firstHalf >>> 32 ^ this.secondHalf ^ this.secondHalf >>> 32);
    }
    
    @Override
    public String toString() {
        return this.toStringBuffer(null).toString();
    }
    
    @Override
    public int compareTo(final Object o) {
        final AUID other = (AUID)o;
        long cmp = this.getTimeLow() - other.getTimeLow();
        if (cmp != 0L) {
            cmp = this.getTimeMid() - other.getTimeMid();
            if (cmp != 0L) {
                cmp = this.getTimeHighAndVersion() - other.getTimeHighAndVersion();
                if (cmp != 0L) {
                    cmp = this.getClockSeqHighAndVariant() - other.getClockSeqHighAndVariant();
                    if (cmp != 0L) {
                        cmp = this.getClockSeqLow() - other.getClockSeqLow();
                        if (cmp != 0L) {
                            cmp = this.getNode() - other.getNode();
                        }
                    }
                }
            }
        }
        return (cmp == 0L) ? 0 : ((cmp < 0L) ? -1 : 1);
    }
    
    private static long entropicSeed(int bits, final long initialSeed) {
        if (bits > 63) {
            bits = 63;
        }
        else if (bits < 1) {
            bits = 1;
        }
        final long startTime = System.currentTimeMillis();
        final int[] counters = new int[bits + 1];
        final Random[] randoms = new Random[bits];
        final Thread[] threads = new Thread[bits];
        final int endvalue = bits * 128;
        final int lastindex = bits;
        final Random random = new Random(initialSeed);
        for (int i = 0; i < bits; ++i) {
            final int thisindex = i;
            final long nextSeed = random.nextLong();
            randoms[i] = new Random(nextSeed);
            (threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        while (counters[lastindex] < endvalue) {
                            final long value = randoms[thisindex].nextLong();
                            for (int loop = (int)(value & 0xFFL) + 16, a = 0; a < loop; ++a) {
                                randoms[thisindex].nextLong();
                                if (System.currentTimeMillis() - startTime > 5000L) {
                                    break;
                                }
                            }
                            final int[] val$counters = counters;
                            final int val$thisindex = thisindex;
                            ++val$counters[val$thisindex];
                            if (System.currentTimeMillis() - startTime > 5000L) {
                                break;
                            }
                        }
                    }
                    catch (Throwable t) {
                        NucleusLogger.VALUEGENERATION.error(t);
                        counters[thisindex] = endvalue;
                    }
                    finally {
                        threads[thisindex] = null;
                    }
                }
            }).start();
        }
        for (int i = 0; i < bits; ++i) {
            while (counters[i] < bits) {
                Thread.yield();
                if (System.currentTimeMillis() - startTime > 5000L) {
                    break;
                }
            }
        }
        while (counters[lastindex] < endvalue) {
            Thread.yield();
            int sum = 0;
            for (int j = 0; j < bits; ++j) {
                sum += counters[j];
            }
            counters[lastindex] = sum;
            if (System.currentTimeMillis() - startTime > 5000L) {
                break;
            }
        }
        for (int i = 0; i < bits; ++i) {
            while (threads[i] != null) {
                Thread.yield();
            }
        }
        long seed = 0L;
        for (int k = 0; k < bits; ++k) {
            seed += randoms[k].nextLong();
        }
        return seed;
    }
    
    static {
        UTC_OFFSET = new GregorianCalendar().getGregorianChange().getTime() * 10000L;
        HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        AUID.auidState = null;
    }
    
    protected static class State
    {
        private long lastTime;
        private long adjustTime;
        private int clockSequence;
        private long node;
        private int version;
        private int variant;
        private Random random;
        private boolean includeSecurityAttributes;
        
        public void setLastTime(final long lastTime) {
            this.lastTime = lastTime;
        }
        
        public long getLastTime() {
            return this.lastTime;
        }
        
        public void setAdjustTime(final long adjustTime) {
            this.adjustTime = adjustTime;
        }
        
        public long getAdjustTime() {
            return this.adjustTime;
        }
        
        public long incrementAdjustTime() {
            return this.adjustTime++;
        }
        
        public void setClockSequence(final int clockSequence) {
            this.clockSequence = clockSequence;
        }
        
        public int getClockSequence() {
            return this.clockSequence;
        }
        
        public void setNode(final long node) {
            this.node = node;
        }
        
        public long getNode() {
            return this.node;
        }
        
        public void setVersion(final int version) {
            this.version = version;
        }
        
        public int getVersion() {
            return this.version;
        }
        
        public void setVariant(final int variant) {
            this.variant = variant;
        }
        
        public int getVariant() {
            return this.variant;
        }
        
        public void setRandom(final Random random) {
            this.random = random;
        }
        
        public Random getRandom() {
            return this.random;
        }
        
        public void setIncludeSecurityAttributes(final boolean includeSecurityAttributes) {
            this.includeSecurityAttributes = includeSecurityAttributes;
        }
        
        public boolean getIncludeSecurityAttributes() {
            return this.includeSecurityAttributes;
        }
    }
}
