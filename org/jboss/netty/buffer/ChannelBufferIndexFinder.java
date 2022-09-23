// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

public interface ChannelBufferIndexFinder
{
    public static final ChannelBufferIndexFinder NUL = new ChannelBufferIndexFinder() {
        public boolean find(final ChannelBuffer buffer, final int guessedIndex) {
            return buffer.getByte(guessedIndex) == 0;
        }
    };
    public static final ChannelBufferIndexFinder NOT_NUL = new ChannelBufferIndexFinder() {
        public boolean find(final ChannelBuffer buffer, final int guessedIndex) {
            return buffer.getByte(guessedIndex) != 0;
        }
    };
    public static final ChannelBufferIndexFinder CR = new ChannelBufferIndexFinder() {
        public boolean find(final ChannelBuffer buffer, final int guessedIndex) {
            return buffer.getByte(guessedIndex) == 13;
        }
    };
    public static final ChannelBufferIndexFinder NOT_CR = new ChannelBufferIndexFinder() {
        public boolean find(final ChannelBuffer buffer, final int guessedIndex) {
            return buffer.getByte(guessedIndex) != 13;
        }
    };
    public static final ChannelBufferIndexFinder LF = new ChannelBufferIndexFinder() {
        public boolean find(final ChannelBuffer buffer, final int guessedIndex) {
            return buffer.getByte(guessedIndex) == 10;
        }
    };
    public static final ChannelBufferIndexFinder NOT_LF = new ChannelBufferIndexFinder() {
        public boolean find(final ChannelBuffer buffer, final int guessedIndex) {
            return buffer.getByte(guessedIndex) != 10;
        }
    };
    public static final ChannelBufferIndexFinder CRLF = new ChannelBufferIndexFinder() {
        public boolean find(final ChannelBuffer buffer, final int guessedIndex) {
            final byte b = buffer.getByte(guessedIndex);
            return b == 13 || b == 10;
        }
    };
    public static final ChannelBufferIndexFinder NOT_CRLF = new ChannelBufferIndexFinder() {
        public boolean find(final ChannelBuffer buffer, final int guessedIndex) {
            final byte b = buffer.getByte(guessedIndex);
            return b != 13 && b != 10;
        }
    };
    public static final ChannelBufferIndexFinder LINEAR_WHITESPACE = new ChannelBufferIndexFinder() {
        public boolean find(final ChannelBuffer buffer, final int guessedIndex) {
            final byte b = buffer.getByte(guessedIndex);
            return b == 32 || b == 9;
        }
    };
    public static final ChannelBufferIndexFinder NOT_LINEAR_WHITESPACE = new ChannelBufferIndexFinder() {
        public boolean find(final ChannelBuffer buffer, final int guessedIndex) {
            final byte b = buffer.getByte(guessedIndex);
            return b != 32 && b != 9;
        }
    };
    
    boolean find(final ChannelBuffer p0, final int p1);
}
