// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.asm;

final class $FieldWriter implements $FieldVisitor
{
    $FieldWriter a;
    private final $ClassWriter b;
    private final int c;
    private final int d;
    private final int e;
    private int f;
    private int g;
    private $AnnotationWriter h;
    private $AnnotationWriter i;
    private $Attribute j;
    
    $FieldWriter(final $ClassWriter b, final int c, final String s, final String s2, final String s3, final Object o) {
        if (b.y == null) {
            b.y = this;
        }
        else {
            b.z.a = this;
        }
        b.z = this;
        this.b = b;
        this.c = c;
        this.d = b.newUTF8(s);
        this.e = b.newUTF8(s2);
        if (s3 != null) {
            this.f = b.newUTF8(s3);
        }
        if (o != null) {
            this.g = b.a(o).a;
        }
    }
    
    public $AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        final $ByteVector $ByteVector = new $ByteVector();
        $ByteVector.putShort(this.b.newUTF8(s)).putShort(0);
        final $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this.b, true, $ByteVector, $ByteVector, 2);
        if (b) {
            $AnnotationWriter.g = this.h;
            this.h = $AnnotationWriter;
        }
        else {
            $AnnotationWriter.g = this.i;
            this.i = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }
    
    public void visitAttribute(final $Attribute j) {
        j.a = this.j;
        this.j = j;
    }
    
    public void visitEnd() {
    }
    
    int a() {
        int n = 8;
        if (this.g != 0) {
            this.b.newUTF8("ConstantValue");
            n += 8;
        }
        if ((this.c & 0x1000) != 0x0 && (this.b.b & 0xFFFF) < 49) {
            this.b.newUTF8("Synthetic");
            n += 6;
        }
        if ((this.c & 0x20000) != 0x0) {
            this.b.newUTF8("Deprecated");
            n += 6;
        }
        if (this.f != 0) {
            this.b.newUTF8("Signature");
            n += 8;
        }
        if (this.h != null) {
            this.b.newUTF8("RuntimeVisibleAnnotations");
            n += 8 + this.h.a();
        }
        if (this.i != null) {
            this.b.newUTF8("RuntimeInvisibleAnnotations");
            n += 8 + this.i.a();
        }
        if (this.j != null) {
            n += this.j.a(this.b, null, 0, -1, -1);
        }
        return n;
    }
    
    void a(final $ByteVector $ByteVector) {
        $ByteVector.putShort(this.c).putShort(this.d).putShort(this.e);
        int n = 0;
        if (this.g != 0) {
            ++n;
        }
        if ((this.c & 0x1000) != 0x0 && (this.b.b & 0xFFFF) < 49) {
            ++n;
        }
        if ((this.c & 0x20000) != 0x0) {
            ++n;
        }
        if (this.f != 0) {
            ++n;
        }
        if (this.h != null) {
            ++n;
        }
        if (this.i != null) {
            ++n;
        }
        if (this.j != null) {
            n += this.j.a();
        }
        $ByteVector.putShort(n);
        if (this.g != 0) {
            $ByteVector.putShort(this.b.newUTF8("ConstantValue"));
            $ByteVector.putInt(2).putShort(this.g);
        }
        if ((this.c & 0x1000) != 0x0 && (this.b.b & 0xFFFF) < 49) {
            $ByteVector.putShort(this.b.newUTF8("Synthetic")).putInt(0);
        }
        if ((this.c & 0x20000) != 0x0) {
            $ByteVector.putShort(this.b.newUTF8("Deprecated")).putInt(0);
        }
        if (this.f != 0) {
            $ByteVector.putShort(this.b.newUTF8("Signature"));
            $ByteVector.putInt(2).putShort(this.f);
        }
        if (this.h != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeVisibleAnnotations"));
            this.h.a($ByteVector);
        }
        if (this.i != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeInvisibleAnnotations"));
            this.i.a($ByteVector);
        }
        if (this.j != null) {
            this.j.a(this.b, null, 0, -1, -1, $ByteVector);
        }
    }
}
