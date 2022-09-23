// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

public enum Packer
{
    BIG_ENDIAN {
        @Override
        public IntPacker newIntPacker(final int width) {
            return Packer$1.beIntPackerFactory.newIntPacker(width);
        }
        
        @Override
        public BytePacker newBytePacker(final int width) {
            return Packer$1.beBytePackerFactory.newBytePacker(width);
        }
    }, 
    LITTLE_ENDIAN {
        @Override
        public IntPacker newIntPacker(final int width) {
            return Packer$2.leIntPackerFactory.newIntPacker(width);
        }
        
        @Override
        public BytePacker newBytePacker(final int width) {
            return Packer$2.leBytePackerFactory.newBytePacker(width);
        }
    };
    
    static BytePackerFactory beBytePackerFactory;
    static IntPackerFactory beIntPackerFactory;
    static BytePackerFactory leBytePackerFactory;
    static IntPackerFactory leIntPackerFactory;
    
    private static IntPackerFactory getIntPackerFactory(final String name) {
        return (IntPackerFactory)getStaticField("parquet.column.values.bitpacking." + name, "factory");
    }
    
    private static BytePackerFactory getBytePackerFactory(final String name) {
        return (BytePackerFactory)getStaticField("parquet.column.values.bitpacking." + name, "factory");
    }
    
    private static Object getStaticField(final String className, final String fieldName) {
        try {
            return Class.forName(className).getField(fieldName).get(null);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        }
        catch (NoSuchFieldException e3) {
            throw new RuntimeException(e3);
        }
        catch (SecurityException e4) {
            throw new RuntimeException(e4);
        }
        catch (ClassNotFoundException e5) {
            throw new RuntimeException(e5);
        }
    }
    
    public abstract IntPacker newIntPacker(final int p0);
    
    public abstract BytePacker newBytePacker(final int p0);
    
    static {
        Packer.beBytePackerFactory = getBytePackerFactory("ByteBitPackingBE");
        Packer.beIntPackerFactory = getIntPackerFactory("LemireBitPackingBE");
        Packer.leBytePackerFactory = getBytePackerFactory("ByteBitPackingLE");
        Packer.leIntPackerFactory = getIntPackerFactory("LemireBitPackingLE");
    }
}
