// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.awt.Color;

public class ColorStringConverter implements TypeConverter<Color, String>
{
    @Override
    public Color toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        final int componentLength = (str.length() - 1) / 4;
        final String rStr = str.substring(1, 1 + componentLength);
        final String gStr = str.substring(1 + componentLength, 1 + 2 * componentLength);
        final String bStr = str.substring(1 + 2 * componentLength, 1 + 3 * componentLength);
        final String aStr = str.substring(1 + 3 * componentLength);
        final int r = Integer.parseInt(rStr, 16);
        final int g = Integer.parseInt(gStr, 16);
        final int b = Integer.parseInt(bStr, 16);
        final int a = Integer.parseInt(aStr, 16);
        return new Color(r, g, b, a);
    }
    
    @Override
    public String toDatastoreType(final Color c) {
        if (c == null) {
            return null;
        }
        final String r = (c.getRed() < 16) ? ("0" + Integer.toHexString(c.getRed())) : Integer.toHexString(c.getRed());
        final String g = (c.getGreen() < 16) ? ("0" + Integer.toHexString(c.getGreen())) : Integer.toHexString(c.getGreen());
        final String b = (c.getBlue() < 16) ? ("0" + Integer.toHexString(c.getBlue())) : Integer.toHexString(c.getBlue());
        final String a = (c.getAlpha() < 16) ? ("0" + Integer.toHexString(c.getAlpha())) : Integer.toHexString(c.getAlpha());
        return "#" + r + g + b + a;
    }
}
