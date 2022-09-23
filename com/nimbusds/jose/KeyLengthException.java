// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

public class KeyLengthException extends KeyException
{
    private final int expectedLength;
    private final Algorithm alg;
    
    public KeyLengthException(final String message) {
        super(message);
        this.expectedLength = 0;
        this.alg = null;
    }
    
    public KeyLengthException(final Algorithm alg) {
        this(0, alg);
    }
    
    public KeyLengthException(final int expectedLength, final Algorithm alg) {
        super(String.valueOf((expectedLength > 0) ? new StringBuilder("The expected key length is ").append(expectedLength).append(" bits").toString() : "Unexpected key length") + ((alg != null) ? (" (for " + alg + " algorithm)") : ""));
        this.expectedLength = expectedLength;
        this.alg = alg;
    }
    
    public int getExpectedKeyLength() {
        return this.expectedLength;
    }
    
    public Algorithm getAlgorithm() {
        return this.alg;
    }
}
