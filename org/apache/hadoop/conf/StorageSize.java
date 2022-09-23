// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class StorageSize
{
    private final StorageUnit unit;
    private final double value;
    
    public StorageSize(final StorageUnit unit, final double value) {
        this.unit = unit;
        this.value = value;
    }
    
    private static void checkState(final boolean state, final String errorString) {
        if (!state) {
            throw new IllegalStateException(errorString);
        }
    }
    
    public static StorageSize parse(final String value) {
        checkState(StringUtils.isNotBlank(value), "value cannot be blank");
        final String sanitizedValue = value.trim().toLowerCase(Locale.ENGLISH);
        StorageUnit parsedUnit = null;
        for (final StorageUnit unit : StorageUnit.values()) {
            if (sanitizedValue.endsWith(unit.getShortName()) || sanitizedValue.endsWith(unit.getLongName()) || sanitizedValue.endsWith(unit.getSuffixChar())) {
                parsedUnit = unit;
                break;
            }
        }
        if (parsedUnit == null) {
            throw new IllegalArgumentException(value + " is not in expected format.Expected format is <number><unit>. e.g. 1000MB");
        }
        String suffix = "";
        boolean found = false;
        if (!found && sanitizedValue.endsWith(parsedUnit.getLongName())) {
            found = true;
            suffix = parsedUnit.getLongName();
        }
        if (!found && sanitizedValue.endsWith(parsedUnit.getShortName())) {
            found = true;
            suffix = parsedUnit.getShortName();
        }
        if (!found && sanitizedValue.endsWith(parsedUnit.getSuffixChar())) {
            found = true;
            suffix = parsedUnit.getSuffixChar();
        }
        checkState(found, "Something is wrong, we have to find a match. Internal error.");
        final String valString = sanitizedValue.substring(0, value.length() - suffix.length());
        return new StorageSize(parsedUnit, Double.parseDouble(valString));
    }
    
    public StorageUnit getUnit() {
        return this.unit;
    }
    
    public double getValue() {
        return this.value;
    }
}
