// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import parquet.org.apache.thrift.TEnum;

public enum PageType implements TEnum
{
    DATA_PAGE(0), 
    INDEX_PAGE(1), 
    DICTIONARY_PAGE(2), 
    DATA_PAGE_V2(3);
    
    private final int value;
    
    private PageType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static PageType findByValue(final int value) {
        switch (value) {
            case 0: {
                return PageType.DATA_PAGE;
            }
            case 1: {
                return PageType.INDEX_PAGE;
            }
            case 2: {
                return PageType.DICTIONARY_PAGE;
            }
            case 3: {
                return PageType.DATA_PAGE_V2;
            }
            default: {
                return null;
            }
        }
    }
}
