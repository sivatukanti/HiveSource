// 
// Decompiled by Procyon v0.5.36
// 

package parquet;

public final class Ints
{
    private Ints() {
    }
    
    public static int checkedCast(final long value) {
        final int valueI = (int)value;
        if (valueI != value) {
            throw new IllegalArgumentException(String.format("Overflow casting %d to an int", value));
        }
        return valueI;
    }
}
