// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.asm;

final class $Item
{
    int a;
    int b;
    int c;
    long d;
    String g;
    String h;
    String i;
    int j;
    $Item k;
    
    $Item() {
    }
    
    $Item(final int a) {
        this.a = a;
    }
    
    $Item(final int a, final $Item $Item) {
        this.a = a;
        this.b = $Item.b;
        this.c = $Item.c;
        this.d = $Item.d;
        this.g = $Item.g;
        this.h = $Item.h;
        this.i = $Item.i;
        this.j = $Item.j;
    }
    
    void a(final int c) {
        this.b = 3;
        this.c = c;
        this.j = (Integer.MAX_VALUE & this.b + c);
    }
    
    void a(final long d) {
        this.b = 5;
        this.d = d;
        this.j = (Integer.MAX_VALUE & this.b + (int)d);
    }
    
    void a(final float n) {
        this.b = 4;
        this.c = Float.floatToRawIntBits(n);
        this.j = (Integer.MAX_VALUE & this.b + (int)n);
    }
    
    void a(final double n) {
        this.b = 6;
        this.d = Double.doubleToRawLongBits(n);
        this.j = (Integer.MAX_VALUE & this.b + (int)n);
    }
    
    void a(final int b, final String g, final String h, final String i) {
        this.b = b;
        this.g = g;
        this.h = h;
        this.i = i;
        switch (b) {
            case 1:
            case 7:
            case 8:
            case 13: {
                this.j = (Integer.MAX_VALUE & b + g.hashCode());
            }
            case 12: {
                this.j = (Integer.MAX_VALUE & b + g.hashCode() * h.hashCode());
            }
            default: {
                this.j = (Integer.MAX_VALUE & b + g.hashCode() * h.hashCode() * i.hashCode());
            }
        }
    }
    
    boolean a(final $Item $Item) {
        if ($Item.b != this.b) {
            return false;
        }
        switch (this.b) {
            case 3:
            case 4: {
                return $Item.c == this.c;
            }
            case 5:
            case 6:
            case 15: {
                return $Item.d == this.d;
            }
            case 1:
            case 7:
            case 8:
            case 13: {
                return $Item.g.equals(this.g);
            }
            case 14: {
                return $Item.c == this.c && $Item.g.equals(this.g);
            }
            case 12: {
                return $Item.g.equals(this.g) && $Item.h.equals(this.h);
            }
            default: {
                return $Item.g.equals(this.g) && $Item.h.equals(this.h) && $Item.i.equals(this.i);
            }
        }
    }
}
