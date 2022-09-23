// 
// Decompiled by Procyon v0.5.36
// 

package parquet;

public final class Preconditions
{
    private Preconditions() {
    }
    
    public static <T> T checkNotNull(final T o, final String name) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException(name + " should not be null");
        }
        return o;
    }
    
    public static void checkArgument(final boolean isValid, final String message) throws IllegalArgumentException {
        if (!isValid) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void checkArgument(final boolean isValid, final String message, final Object... args) throws IllegalArgumentException {
        if (!isValid) {
            throw new IllegalArgumentException(String.format(String.valueOf(message), (Object[])strings(args)));
        }
    }
    
    public static void checkState(final boolean isValid, final String message) throws IllegalStateException {
        if (!isValid) {
            throw new IllegalStateException(message);
        }
    }
    
    public static void checkState(final boolean isValid, final String message, final Object... args) throws IllegalStateException {
        if (!isValid) {
            throw new IllegalStateException(String.format(String.valueOf(message), (Object[])strings(args)));
        }
    }
    
    private static String[] strings(final Object[] objects) {
        final String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; ++i) {
            strings[i] = String.valueOf(objects[i]);
        }
        return strings;
    }
}
