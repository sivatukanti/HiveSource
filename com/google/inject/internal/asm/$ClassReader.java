// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.asm;

import java.io.IOException;
import java.io.InputStream;

public class $ClassReader
{
    public static final int SKIP_CODE = 1;
    public static final int SKIP_DEBUG = 2;
    public static final int SKIP_FRAMES = 4;
    public static final int EXPAND_FRAMES = 8;
    public final byte[] b;
    private final int[] a;
    private final String[] c;
    private final int d;
    public final int header;
    
    public $ClassReader(final byte[] array) {
        this(array, 0, array.length);
    }
    
    public $ClassReader(final byte[] b, final int n, final int n2) {
        this.b = b;
        this.a = new int[this.readUnsignedShort(n + 8)];
        final int length = this.a.length;
        this.c = new String[length];
        int d = 0;
        int header = n + 10;
        for (int i = 1; i < length; ++i) {
            this.a[i] = header + 1;
            int n3 = 0;
            switch (b[header]) {
                case 3:
                case 4:
                case 9:
                case 10:
                case 11:
                case 12: {
                    n3 = 5;
                    break;
                }
                case 5:
                case 6: {
                    n3 = 9;
                    ++i;
                    break;
                }
                case 1: {
                    n3 = 3 + this.readUnsignedShort(header + 1);
                    if (n3 > d) {
                        d = n3;
                        break;
                    }
                    break;
                }
                default: {
                    n3 = 3;
                    break;
                }
            }
            header += n3;
        }
        this.d = d;
        this.header = header;
    }
    
    public int getAccess() {
        return this.readUnsignedShort(this.header);
    }
    
    public String getClassName() {
        return this.readClass(this.header + 2, new char[this.d]);
    }
    
    public String getSuperName() {
        final int n = this.a[this.readUnsignedShort(this.header + 4)];
        return (n == 0) ? null : this.readUTF8(n, new char[this.d]);
    }
    
    public String[] getInterfaces() {
        int n = this.header + 6;
        final int unsignedShort = this.readUnsignedShort(n);
        final String[] array = new String[unsignedShort];
        if (unsignedShort > 0) {
            final char[] array2 = new char[this.d];
            for (int i = 0; i < unsignedShort; ++i) {
                n += 2;
                array[i] = this.readClass(n, array2);
            }
        }
        return array;
    }
    
    void a(final $ClassWriter $ClassWriter) {
        final char[] array = new char[this.d];
        final int length = this.a.length;
        final $Item[] e = new $Item[length];
        for (int i = 1; i < length; ++i) {
            final int n = this.a[i];
            final byte b = this.b[n - 1];
            final $Item $Item = new $Item(i);
            switch (b) {
                case 9:
                case 10:
                case 11: {
                    final int n2 = this.a[this.readUnsignedShort(n + 2)];
                    $Item.a(b, this.readClass(n, array), this.readUTF8(n2, array), this.readUTF8(n2 + 2, array));
                    break;
                }
                case 3: {
                    $Item.a(this.readInt(n));
                    break;
                }
                case 4: {
                    $Item.a(Float.intBitsToFloat(this.readInt(n)));
                    break;
                }
                case 12: {
                    $Item.a(b, this.readUTF8(n, array), this.readUTF8(n + 2, array), null);
                    break;
                }
                case 5: {
                    $Item.a(this.readLong(n));
                    ++i;
                    break;
                }
                case 6: {
                    $Item.a(Double.longBitsToDouble(this.readLong(n)));
                    ++i;
                    break;
                }
                case 1: {
                    String s = this.c[i];
                    if (s == null) {
                        final int n3 = this.a[i];
                        final String[] c = this.c;
                        final int n4 = i;
                        final String a = this.a(n3 + 2, this.readUnsignedShort(n3), array);
                        c[n4] = a;
                        s = a;
                    }
                    $Item.a(b, s, null, null);
                    break;
                }
                default: {
                    $Item.a(b, this.readUTF8(n, array), null, null);
                    break;
                }
            }
            final int n5 = $Item.j % e.length;
            $Item.k = e[n5];
            e[n5] = $Item;
        }
        final int n6 = this.a[1] - 1;
        $ClassWriter.d.putByteArray(this.b, n6, this.header - n6);
        $ClassWriter.e = e;
        $ClassWriter.f = (int)(0.75 * length);
        $ClassWriter.c = length;
    }
    
    public $ClassReader(final InputStream inputStream) throws IOException {
        this(a(inputStream));
    }
    
    public $ClassReader(final String s) throws IOException {
        this(ClassLoader.getSystemResourceAsStream(s.replace('.', '/') + ".class"));
    }
    
    private static byte[] a(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("Class not found");
        }
        byte[] b = new byte[inputStream.available()];
        int off = 0;
        while (true) {
            final int read = inputStream.read(b, off, b.length - off);
            if (read == -1) {
                break;
            }
            off += read;
            if (off != b.length) {
                continue;
            }
            final byte[] array = new byte[b.length + 1000];
            System.arraycopy(b, 0, array, 0, off);
            b = array;
        }
        if (off < b.length) {
            final byte[] array2 = new byte[off];
            System.arraycopy(b, 0, array2, 0, off);
            b = array2;
        }
        return b;
    }
    
    public void accept(final $ClassVisitor $ClassVisitor, final int n) {
        this.accept($ClassVisitor, new $Attribute[0], n);
    }
    
    public void accept(final $ClassVisitor $ClassVisitor, final $Attribute[] array, final int n) {
        final byte[] b = this.b;
        final char[] array2 = new char[this.d];
        int n2 = 0;
        int n3 = 0;
        $Attribute a = null;
        int header = this.header;
        int unsignedShort = this.readUnsignedShort(header);
        final String class1 = this.readClass(header + 2, array2);
        final int n4 = this.a[this.readUnsignedShort(header + 4)];
        final String s = (n4 == 0) ? null : this.readUTF8(n4, array2);
        final String[] array3 = new String[this.readUnsignedShort(header + 6)];
        int n5 = 0;
        header += 8;
        for (int i = 0; i < array3.length; ++i) {
            array3[i] = this.readClass(header, array2);
            header += 2;
        }
        final boolean b2 = (n & 0x1) != 0x0;
        final boolean b3 = (n & 0x2) != 0x0;
        final int n6 = ((n & 0x8) != 0x0) ? 1 : 0;
        int n7 = header;
        int j = this.readUnsignedShort(n7);
        n7 += 2;
        while (j > 0) {
            int k = this.readUnsignedShort(n7 + 6);
            n7 += 8;
            while (k > 0) {
                n7 += 6 + this.readInt(n7 + 2);
                --k;
            }
            --j;
        }
        int l = this.readUnsignedShort(n7);
        n7 += 2;
        while (l > 0) {
            int unsignedShort2 = this.readUnsignedShort(n7 + 6);
            n7 += 8;
            while (unsignedShort2 > 0) {
                n7 += 6 + this.readInt(n7 + 2);
                --unsignedShort2;
            }
            --l;
        }
        String utf8 = null;
        String utf9 = null;
        String a2 = null;
        String class2 = null;
        String utf10 = null;
        String utf11 = null;
        int unsignedShort3 = this.readUnsignedShort(n7);
        n7 += 2;
        while (unsignedShort3 > 0) {
            final String utf12 = this.readUTF8(n7, array2);
            if ("SourceFile".equals(utf12)) {
                utf9 = this.readUTF8(n7 + 6, array2);
            }
            else if ("InnerClasses".equals(utf12)) {
                n5 = n7 + 6;
            }
            else if ("EnclosingMethod".equals(utf12)) {
                class2 = this.readClass(n7 + 6, array2);
                final int unsignedShort4 = this.readUnsignedShort(n7 + 8);
                if (unsignedShort4 != 0) {
                    utf10 = this.readUTF8(this.a[unsignedShort4], array2);
                    utf11 = this.readUTF8(this.a[unsignedShort4] + 2, array2);
                }
            }
            else if ("Signature".equals(utf12)) {
                utf8 = this.readUTF8(n7 + 6, array2);
            }
            else if ("RuntimeVisibleAnnotations".equals(utf12)) {
                n2 = n7 + 6;
            }
            else if ("Deprecated".equals(utf12)) {
                unsignedShort |= 0x20000;
            }
            else if ("Synthetic".equals(utf12)) {
                unsignedShort |= 0x1000;
            }
            else if ("SourceDebugExtension".equals(utf12)) {
                final int int1 = this.readInt(n7 + 2);
                a2 = this.a(n7 + 6, int1, new char[int1]);
            }
            else if ("RuntimeInvisibleAnnotations".equals(utf12)) {
                n3 = n7 + 6;
            }
            else {
                final $Attribute a3 = this.a(array, utf12, n7 + 6, this.readInt(n7 + 2), array2, -1, null);
                if (a3 != null) {
                    a3.a = a;
                    a = a3;
                }
            }
            n7 += 6 + this.readInt(n7 + 2);
            --unsignedShort3;
        }
        $ClassVisitor.visit(this.readInt(4), unsignedShort, class1, utf8, s, array3);
        if (!b3 && (utf9 != null || a2 != null)) {
            $ClassVisitor.visitSource(utf9, a2);
        }
        if (class2 != null) {
            $ClassVisitor.visitOuterClass(class2, utf10, utf11);
        }
        for (int n8 = 1; n8 >= 0; --n8) {
            int a4 = (n8 == 0) ? n3 : n2;
            if (a4 != 0) {
                int unsignedShort5 = this.readUnsignedShort(a4);
                a4 += 2;
                while (unsignedShort5 > 0) {
                    a4 = this.a(a4 + 2, array2, true, $ClassVisitor.visitAnnotation(this.readUTF8(a4, array2), n8 != 0));
                    --unsignedShort5;
                }
            }
        }
        while (a != null) {
            final $Attribute a5 = a.a;
            a.a = null;
            $ClassVisitor.visitAttribute(a);
            a = a5;
        }
        if (n5 != 0) {
            int unsignedShort6 = this.readUnsignedShort(n5);
            n5 += 2;
            while (unsignedShort6 > 0) {
                $ClassVisitor.visitInnerClass((this.readUnsignedShort(n5) == 0) ? null : this.readClass(n5, array2), (this.readUnsignedShort(n5 + 2) == 0) ? null : this.readClass(n5 + 2, array2), (this.readUnsignedShort(n5 + 4) == 0) ? null : this.readUTF8(n5 + 4, array2), this.readUnsignedShort(n5 + 6));
                n5 += 8;
                --unsignedShort6;
            }
        }
        int unsignedShort7 = this.readUnsignedShort(header);
        header += 2;
        while (unsignedShort7 > 0) {
            int unsignedShort8 = this.readUnsignedShort(header);
            final String utf13 = this.readUTF8(header + 2, array2);
            final String utf14 = this.readUTF8(header + 4, array2);
            int unsignedShort9 = 0;
            String utf15 = null;
            int n9 = 0;
            int n10 = 0;
            $Attribute a6 = null;
            int unsignedShort10 = this.readUnsignedShort(header + 6);
            header += 8;
            while (unsignedShort10 > 0) {
                final String utf16 = this.readUTF8(header, array2);
                if ("ConstantValue".equals(utf16)) {
                    unsignedShort9 = this.readUnsignedShort(header + 6);
                }
                else if ("Signature".equals(utf16)) {
                    utf15 = this.readUTF8(header + 6, array2);
                }
                else if ("Deprecated".equals(utf16)) {
                    unsignedShort8 |= 0x20000;
                }
                else if ("Synthetic".equals(utf16)) {
                    unsignedShort8 |= 0x1000;
                }
                else if ("RuntimeVisibleAnnotations".equals(utf16)) {
                    n9 = header + 6;
                }
                else if ("RuntimeInvisibleAnnotations".equals(utf16)) {
                    n10 = header + 6;
                }
                else {
                    final $Attribute a7 = this.a(array, utf16, header + 6, this.readInt(header + 2), array2, -1, null);
                    if (a7 != null) {
                        a7.a = a6;
                        a6 = a7;
                    }
                }
                header += 6 + this.readInt(header + 2);
                --unsignedShort10;
            }
            final $FieldVisitor visitField = $ClassVisitor.visitField(unsignedShort8, utf13, utf14, utf15, (unsignedShort9 == 0) ? null : this.readConst(unsignedShort9, array2));
            if (visitField != null) {
                for (int n11 = 1; n11 >= 0; --n11) {
                    int a8 = (n11 == 0) ? n10 : n9;
                    if (a8 != 0) {
                        int unsignedShort11 = this.readUnsignedShort(a8);
                        a8 += 2;
                        while (unsignedShort11 > 0) {
                            a8 = this.a(a8 + 2, array2, true, visitField.visitAnnotation(this.readUTF8(a8, array2), n11 != 0));
                            --unsignedShort11;
                        }
                    }
                }
                while (a6 != null) {
                    final $Attribute a9 = a6.a;
                    a6.a = null;
                    visitField.visitAttribute(a6);
                    a6 = a9;
                }
                visitField.visitEnd();
            }
            --unsignedShort7;
        }
        int unsignedShort12 = this.readUnsignedShort(header);
        header += 2;
        while (unsignedShort12 > 0) {
            final int h = header + 6;
            int unsignedShort13 = this.readUnsignedShort(header);
            final String utf17 = this.readUTF8(header + 2, array2);
            final String utf18 = this.readUTF8(header + 4, array2);
            String utf19 = null;
            int n12 = 0;
            int n13 = 0;
            int n14 = 0;
            int n15 = 0;
            int n16 = 0;
            $Attribute a10 = null;
            int n17 = 0;
            int n18 = 0;
            int unsignedShort14 = this.readUnsignedShort(header + 6);
            header += 8;
            while (unsignedShort14 > 0) {
                final String utf20 = this.readUTF8(header, array2);
                final int int2 = this.readInt(header + 2);
                header += 6;
                if ("Code".equals(utf20)) {
                    if (!b2) {
                        n17 = header;
                    }
                }
                else if ("Exceptions".equals(utf20)) {
                    n18 = header;
                }
                else if ("Signature".equals(utf20)) {
                    utf19 = this.readUTF8(header, array2);
                }
                else if ("Deprecated".equals(utf20)) {
                    unsignedShort13 |= 0x20000;
                }
                else if ("RuntimeVisibleAnnotations".equals(utf20)) {
                    n12 = header;
                }
                else if ("AnnotationDefault".equals(utf20)) {
                    n14 = header;
                }
                else if ("Synthetic".equals(utf20)) {
                    unsignedShort13 |= 0x1000;
                }
                else if ("RuntimeInvisibleAnnotations".equals(utf20)) {
                    n13 = header;
                }
                else if ("RuntimeVisibleParameterAnnotations".equals(utf20)) {
                    n15 = header;
                }
                else if ("RuntimeInvisibleParameterAnnotations".equals(utf20)) {
                    n16 = header;
                }
                else {
                    final $Attribute a11 = this.a(array, utf20, header, int2, array2, -1, null);
                    if (a11 != null) {
                        a11.a = a10;
                        a10 = a11;
                    }
                }
                header += int2;
                --unsignedShort14;
            }
            String[] array4;
            if (n18 == 0) {
                array4 = null;
            }
            else {
                array4 = new String[this.readUnsignedShort(n18)];
                n18 += 2;
                for (int n19 = 0; n19 < array4.length; ++n19) {
                    array4[n19] = this.readClass(n18, array2);
                    n18 += 2;
                }
            }
            final $MethodVisitor visitMethod = $ClassVisitor.visitMethod(unsignedShort13, utf17, utf18, utf19, array4);
            Label_5484: {
                if (visitMethod != null) {
                    if (visitMethod instanceof $MethodWriter) {
                        final $MethodWriter $MethodWriter = ($MethodWriter)visitMethod;
                        if ($MethodWriter.b.J == this && utf19 == $MethodWriter.g) {
                            int n20 = 0;
                            if (array4 == null) {
                                n20 = (($MethodWriter.j == 0) ? 1 : 0);
                            }
                            else if (array4.length == $MethodWriter.j) {
                                n20 = 1;
                                for (int n21 = array4.length - 1; n21 >= 0; --n21) {
                                    n18 -= 2;
                                    if ($MethodWriter.k[n21] != this.readUnsignedShort(n18)) {
                                        n20 = 0;
                                        break;
                                    }
                                }
                            }
                            if (n20 != 0) {
                                $MethodWriter.h = h;
                                $MethodWriter.i = header - h;
                                break Label_5484;
                            }
                        }
                    }
                    if (n14 != 0) {
                        final $AnnotationVisitor visitAnnotationDefault = visitMethod.visitAnnotationDefault();
                        this.a(n14, array2, null, visitAnnotationDefault);
                        if (visitAnnotationDefault != null) {
                            visitAnnotationDefault.visitEnd();
                        }
                    }
                    for (int n22 = 1; n22 >= 0; --n22) {
                        int a12 = (n22 == 0) ? n13 : n12;
                        if (a12 != 0) {
                            int unsignedShort15 = this.readUnsignedShort(a12);
                            a12 += 2;
                            while (unsignedShort15 > 0) {
                                a12 = this.a(a12 + 2, array2, true, visitMethod.visitAnnotation(this.readUTF8(a12, array2), n22 != 0));
                                --unsignedShort15;
                            }
                        }
                    }
                    if (n15 != 0) {
                        this.a(n15, utf18, array2, true, visitMethod);
                    }
                    if (n16 != 0) {
                        this.a(n16, utf18, array2, false, visitMethod);
                    }
                    while (a10 != null) {
                        final $Attribute a13 = a10.a;
                        a10.a = null;
                        visitMethod.visitAttribute(a10);
                        a10 = a13;
                    }
                }
                if (visitMethod != null && n17 != 0) {
                    final int unsignedShort16 = this.readUnsignedShort(n17);
                    final int unsignedShort17 = this.readUnsignedShort(n17 + 2);
                    final int int3 = this.readInt(n17 + 4);
                    n17 += 8;
                    final int n23 = n17;
                    final int n24 = n17 + int3;
                    visitMethod.visitCode();
                    final $Label[] array5 = new $Label[int3 + 2];
                    this.readLabel(int3 + 1, array5);
                    while (n17 < n24) {
                        final int n25 = n17 - n23;
                        switch ($ClassWriter.a[b[n17] & 0xFF]) {
                            case 0:
                            case 4: {
                                ++n17;
                                continue;
                            }
                            case 8: {
                                this.readLabel(n25 + this.readShort(n17 + 1), array5);
                                n17 += 3;
                                continue;
                            }
                            case 9: {
                                this.readLabel(n25 + this.readInt(n17 + 1), array5);
                                n17 += 5;
                                continue;
                            }
                            case 16: {
                                if ((b[n17 + 1] & 0xFF) == 0x84) {
                                    n17 += 6;
                                    continue;
                                }
                                n17 += 4;
                                continue;
                            }
                            case 13: {
                                n17 = n17 + 4 - (n25 & 0x3);
                                this.readLabel(n25 + this.readInt(n17), array5);
                                int n26 = this.readInt(n17 + 8) - this.readInt(n17 + 4) + 1;
                                n17 += 12;
                                while (n26 > 0) {
                                    this.readLabel(n25 + this.readInt(n17), array5);
                                    n17 += 4;
                                    --n26;
                                }
                                continue;
                            }
                            case 14: {
                                n17 = n17 + 4 - (n25 & 0x3);
                                this.readLabel(n25 + this.readInt(n17), array5);
                                int int4 = this.readInt(n17 + 4);
                                n17 += 8;
                                while (int4 > 0) {
                                    this.readLabel(n25 + this.readInt(n17 + 4), array5);
                                    n17 += 8;
                                    --int4;
                                }
                                continue;
                            }
                            case 1:
                            case 3:
                            case 10: {
                                n17 += 2;
                                continue;
                            }
                            case 2:
                            case 5:
                            case 6:
                            case 11:
                            case 12: {
                                n17 += 3;
                                continue;
                            }
                            case 7: {
                                n17 += 5;
                                continue;
                            }
                            default: {
                                n17 += 4;
                                continue;
                            }
                        }
                    }
                    int unsignedShort18 = this.readUnsignedShort(n17);
                    n17 += 2;
                    while (unsignedShort18 > 0) {
                        final $Label label = this.readLabel(this.readUnsignedShort(n17), array5);
                        final $Label label2 = this.readLabel(this.readUnsignedShort(n17 + 2), array5);
                        final $Label label3 = this.readLabel(this.readUnsignedShort(n17 + 4), array5);
                        final int unsignedShort19 = this.readUnsignedShort(n17 + 6);
                        if (unsignedShort19 == 0) {
                            visitMethod.visitTryCatchBlock(label, label2, label3, null);
                        }
                        else {
                            visitMethod.visitTryCatchBlock(label, label2, label3, this.readUTF8(this.a[unsignedShort19], array2));
                        }
                        n17 += 8;
                        --unsignedShort18;
                    }
                    int n27 = 0;
                    int n28 = 0;
                    int n29 = 0;
                    int n30 = 0;
                    int n31 = 0;
                    int n32 = 0;
                    int unsignedShort20 = 0;
                    int n33 = 0;
                    int n34 = 0;
                    Object[] array6 = null;
                    Object[] array7 = null;
                    boolean b4 = true;
                    $Attribute a14 = null;
                    int unsignedShort21 = this.readUnsignedShort(n17);
                    n17 += 2;
                    while (unsignedShort21 > 0) {
                        final String utf21 = this.readUTF8(n17, array2);
                        if ("LocalVariableTable".equals(utf21)) {
                            if (!b3) {
                                n27 = n17 + 6;
                                int unsignedShort22 = this.readUnsignedShort(n17 + 6);
                                int n35 = n17 + 8;
                                while (unsignedShort22 > 0) {
                                    final int unsignedShort23 = this.readUnsignedShort(n35);
                                    if (array5[unsignedShort23] == null) {
                                        final $Label label4 = this.readLabel(unsignedShort23, array5);
                                        label4.a |= 0x1;
                                    }
                                    final int n36 = unsignedShort23 + this.readUnsignedShort(n35 + 2);
                                    if (array5[n36] == null) {
                                        final $Label label5 = this.readLabel(n36, array5);
                                        label5.a |= 0x1;
                                    }
                                    n35 += 10;
                                    --unsignedShort22;
                                }
                            }
                        }
                        else if ("LocalVariableTypeTable".equals(utf21)) {
                            n28 = n17 + 6;
                        }
                        else if ("LineNumberTable".equals(utf21)) {
                            if (!b3) {
                                int unsignedShort24 = this.readUnsignedShort(n17 + 6);
                                int n37 = n17 + 8;
                                while (unsignedShort24 > 0) {
                                    final int unsignedShort25 = this.readUnsignedShort(n37);
                                    if (array5[unsignedShort25] == null) {
                                        final $Label label6 = this.readLabel(unsignedShort25, array5);
                                        label6.a |= 0x1;
                                    }
                                    array5[unsignedShort25].b = this.readUnsignedShort(n37 + 2);
                                    n37 += 4;
                                    --unsignedShort24;
                                }
                            }
                        }
                        else if ("StackMapTable".equals(utf21)) {
                            if ((n & 0x4) == 0x0) {
                                n29 = n17 + 8;
                                n30 = this.readUnsignedShort(n17 + 6);
                            }
                        }
                        else if ("StackMap".equals(utf21)) {
                            if ((n & 0x4) == 0x0) {
                                n29 = n17 + 8;
                                n30 = this.readUnsignedShort(n17 + 6);
                                b4 = false;
                            }
                        }
                        else {
                            for (int n38 = 0; n38 < array.length; ++n38) {
                                if (array[n38].type.equals(utf21)) {
                                    final $Attribute read = array[n38].read(this, n17 + 6, this.readInt(n17 + 2), array2, n23 - 8, array5);
                                    if (read != null) {
                                        read.a = a14;
                                        a14 = read;
                                    }
                                }
                            }
                        }
                        n17 += 6 + this.readInt(n17 + 2);
                        --unsignedShort21;
                    }
                    if (n29 != 0) {
                        array6 = new Object[unsignedShort17];
                        array7 = new Object[unsignedShort16];
                        Label_3712: {
                            if (n6 != 0) {
                                int n39 = 0;
                                if ((unsignedShort13 & 0x8) == 0x0) {
                                    if ("<init>".equals(utf17)) {
                                        array6[n39++] = $Opcodes.UNINITIALIZED_THIS;
                                    }
                                    else {
                                        array6[n39++] = this.readClass(this.header + 2, array2);
                                    }
                                }
                                int n40 = 1;
                                while (true) {
                                    final int beginIndex = n40;
                                    switch (utf18.charAt(n40++)) {
                                        case 'B':
                                        case 'C':
                                        case 'I':
                                        case 'S':
                                        case 'Z': {
                                            array6[n39++] = $Opcodes.INTEGER;
                                            continue;
                                        }
                                        case 'F': {
                                            array6[n39++] = $Opcodes.FLOAT;
                                            continue;
                                        }
                                        case 'J': {
                                            array6[n39++] = $Opcodes.LONG;
                                            continue;
                                        }
                                        case 'D': {
                                            array6[n39++] = $Opcodes.DOUBLE;
                                            continue;
                                        }
                                        case '[': {
                                            while (utf18.charAt(n40) == '[') {
                                                ++n40;
                                            }
                                            if (utf18.charAt(n40) == 'L') {
                                                ++n40;
                                                while (utf18.charAt(n40) != ';') {
                                                    ++n40;
                                                }
                                            }
                                            array6[n39++] = utf18.substring(beginIndex, ++n40);
                                            continue;
                                        }
                                        case 'L': {
                                            while (utf18.charAt(n40) != ';') {
                                                ++n40;
                                            }
                                            array6[n39++] = utf18.substring(beginIndex + 1, n40++);
                                            continue;
                                        }
                                        default: {
                                            unsignedShort20 = n39;
                                            break Label_3712;
                                        }
                                    }
                                }
                            }
                        }
                        n32 = -1;
                    }
                    int n41 = n23;
                    while (n41 < n24) {
                        final int n42 = n41 - n23;
                        final $Label $Label = array5[n42];
                        if ($Label != null) {
                            visitMethod.visitLabel($Label);
                            if (!b3 && $Label.b > 0) {
                                visitMethod.visitLineNumber($Label.b, $Label);
                            }
                        }
                        while (array6 != null && (n32 == n42 || n32 == -1)) {
                            if (!b4 || n6) {
                                visitMethod.visitFrame(-1, unsignedShort20, array6, n34, array7);
                            }
                            else if (n32 != -1) {
                                visitMethod.visitFrame(n31, n33, array6, n34, array7);
                            }
                            if (n30 > 0) {
                                int n43;
                                if (b4) {
                                    n43 = (b[n29++] & 0xFF);
                                }
                                else {
                                    n43 = 255;
                                    n32 = -1;
                                }
                                n33 = 0;
                                int unsignedShort26;
                                if (n43 < 64) {
                                    unsignedShort26 = n43;
                                    n31 = 3;
                                    n34 = 0;
                                }
                                else if (n43 < 128) {
                                    unsignedShort26 = n43 - 64;
                                    n29 = this.a(array7, 0, n29, array2, array5);
                                    n31 = 4;
                                    n34 = 1;
                                }
                                else {
                                    unsignedShort26 = this.readUnsignedShort(n29);
                                    n29 += 2;
                                    if (n43 == 247) {
                                        n29 = this.a(array7, 0, n29, array2, array5);
                                        n31 = 4;
                                        n34 = 1;
                                    }
                                    else if (n43 >= 248 && n43 < 251) {
                                        n31 = 2;
                                        n33 = 251 - n43;
                                        unsignedShort20 -= n33;
                                        n34 = 0;
                                    }
                                    else if (n43 == 251) {
                                        n31 = 3;
                                        n34 = 0;
                                    }
                                    else if (n43 < 255) {
                                        int n44 = (n6 != 0) ? unsignedShort20 : 0;
                                        for (int n45 = n43 - 251; n45 > 0; --n45) {
                                            n29 = this.a(array6, n44++, n29, array2, array5);
                                        }
                                        n31 = 1;
                                        n33 = n43 - 251;
                                        unsignedShort20 += n33;
                                        n34 = 0;
                                    }
                                    else {
                                        n31 = 0;
                                        int n46;
                                        n33 = (n46 = (unsignedShort20 = this.readUnsignedShort(n29)));
                                        n29 += 2;
                                        int n47 = 0;
                                        while (n46 > 0) {
                                            n29 = this.a(array6, n47++, n29, array2, array5);
                                            --n46;
                                        }
                                        int unsignedShort27;
                                        n34 = (unsignedShort27 = this.readUnsignedShort(n29));
                                        n29 += 2;
                                        int n48 = 0;
                                        while (unsignedShort27 > 0) {
                                            n29 = this.a(array7, n48++, n29, array2, array5);
                                            --unsignedShort27;
                                        }
                                    }
                                }
                                n32 += unsignedShort26 + 1;
                                this.readLabel(n32, array5);
                                --n30;
                            }
                            else {
                                array6 = null;
                            }
                        }
                        int n49 = b[n41] & 0xFF;
                        switch ($ClassWriter.a[n49]) {
                            case 0: {
                                visitMethod.visitInsn(n49);
                                ++n41;
                                continue;
                            }
                            case 4: {
                                if (n49 > 54) {
                                    n49 -= 59;
                                    visitMethod.visitVarInsn(54 + (n49 >> 2), n49 & 0x3);
                                }
                                else {
                                    n49 -= 26;
                                    visitMethod.visitVarInsn(21 + (n49 >> 2), n49 & 0x3);
                                }
                                ++n41;
                                continue;
                            }
                            case 8: {
                                visitMethod.visitJumpInsn(n49, array5[n42 + this.readShort(n41 + 1)]);
                                n41 += 3;
                                continue;
                            }
                            case 9: {
                                visitMethod.visitJumpInsn(n49 - 33, array5[n42 + this.readInt(n41 + 1)]);
                                n41 += 5;
                                continue;
                            }
                            case 16: {
                                final int n50 = b[n41 + 1] & 0xFF;
                                if (n50 == 132) {
                                    visitMethod.visitIincInsn(this.readUnsignedShort(n41 + 2), this.readShort(n41 + 4));
                                    n41 += 6;
                                    continue;
                                }
                                visitMethod.visitVarInsn(n50, this.readUnsignedShort(n41 + 2));
                                n41 += 4;
                                continue;
                            }
                            case 13: {
                                n41 = n41 + 4 - (n42 & 0x3);
                                final int n51 = n42 + this.readInt(n41);
                                final int int5 = this.readInt(n41 + 4);
                                final int int6 = this.readInt(n41 + 8);
                                n41 += 12;
                                final $Label[] array8 = new $Label[int6 - int5 + 1];
                                for (int n52 = 0; n52 < array8.length; ++n52) {
                                    array8[n52] = array5[n42 + this.readInt(n41)];
                                    n41 += 4;
                                }
                                visitMethod.visitTableSwitchInsn(int5, int6, array5[n51], array8);
                                continue;
                            }
                            case 14: {
                                n41 = n41 + 4 - (n42 & 0x3);
                                final int n53 = n42 + this.readInt(n41);
                                final int int7 = this.readInt(n41 + 4);
                                n41 += 8;
                                final int[] array9 = new int[int7];
                                final $Label[] array10 = new $Label[int7];
                                for (int n54 = 0; n54 < array9.length; ++n54) {
                                    array9[n54] = this.readInt(n41);
                                    array10[n54] = array5[n42 + this.readInt(n41 + 4)];
                                    n41 += 8;
                                }
                                visitMethod.visitLookupSwitchInsn(array5[n53], array9, array10);
                                continue;
                            }
                            case 3: {
                                visitMethod.visitVarInsn(n49, b[n41 + 1] & 0xFF);
                                n41 += 2;
                                continue;
                            }
                            case 1: {
                                visitMethod.visitIntInsn(n49, b[n41 + 1]);
                                n41 += 2;
                                continue;
                            }
                            case 2: {
                                visitMethod.visitIntInsn(n49, this.readShort(n41 + 1));
                                n41 += 3;
                                continue;
                            }
                            case 10: {
                                visitMethod.visitLdcInsn(this.readConst(b[n41 + 1] & 0xFF, array2));
                                n41 += 2;
                                continue;
                            }
                            case 11: {
                                visitMethod.visitLdcInsn(this.readConst(this.readUnsignedShort(n41 + 1), array2));
                                n41 += 3;
                                continue;
                            }
                            case 6:
                            case 7: {
                                final int n55 = this.a[this.readUnsignedShort(n41 + 1)];
                                final String class3 = this.readClass(n55, array2);
                                final int n56 = this.a[this.readUnsignedShort(n55 + 2)];
                                final String utf22 = this.readUTF8(n56, array2);
                                final String utf23 = this.readUTF8(n56 + 2, array2);
                                if (n49 < 182) {
                                    visitMethod.visitFieldInsn(n49, class3, utf22, utf23);
                                }
                                else {
                                    visitMethod.visitMethodInsn(n49, class3, utf22, utf23);
                                }
                                if (n49 == 185) {
                                    n41 += 5;
                                    continue;
                                }
                                n41 += 3;
                                continue;
                            }
                            case 5: {
                                visitMethod.visitTypeInsn(n49, this.readClass(n41 + 1, array2));
                                n41 += 3;
                                continue;
                            }
                            case 12: {
                                visitMethod.visitIincInsn(b[n41 + 1] & 0xFF, b[n41 + 2]);
                                n41 += 3;
                                continue;
                            }
                            default: {
                                visitMethod.visitMultiANewArrayInsn(this.readClass(n41 + 1, array2), b[n41 + 3] & 0xFF);
                                n41 += 4;
                                continue;
                            }
                        }
                    }
                    final $Label $Label2 = array5[n24 - n23];
                    if ($Label2 != null) {
                        visitMethod.visitLabel($Label2);
                    }
                    if (!b3 && n27 != 0) {
                        int[] array11 = null;
                        if (n28 != 0) {
                            int n57;
                            int n58;
                            for (n57 = this.readUnsignedShort(n28) * 3, n58 = n28 + 2, array11 = new int[n57]; n57 > 0; array11[--n57] = n58 + 6, array11[--n57] = this.readUnsignedShort(n58 + 8), array11[--n57] = this.readUnsignedShort(n58), n58 += 10) {}
                        }
                        int unsignedShort28 = this.readUnsignedShort(n27);
                        int n59 = n27 + 2;
                        while (unsignedShort28 > 0) {
                            final int unsignedShort29 = this.readUnsignedShort(n59);
                            final int unsignedShort30 = this.readUnsignedShort(n59 + 2);
                            final int unsignedShort31 = this.readUnsignedShort(n59 + 8);
                            String utf24 = null;
                            if (array11 != null) {
                                for (int n60 = 0; n60 < array11.length; n60 += 3) {
                                    if (array11[n60] == unsignedShort29 && array11[n60 + 1] == unsignedShort31) {
                                        utf24 = this.readUTF8(array11[n60 + 2], array2);
                                        break;
                                    }
                                }
                            }
                            visitMethod.visitLocalVariable(this.readUTF8(n59 + 4, array2), this.readUTF8(n59 + 6, array2), utf24, array5[unsignedShort29], array5[unsignedShort29 + unsignedShort30], unsignedShort31);
                            n59 += 10;
                            --unsignedShort28;
                        }
                    }
                    while (a14 != null) {
                        final $Attribute a15 = a14.a;
                        a14.a = null;
                        visitMethod.visitAttribute(a14);
                        a14 = a15;
                    }
                    visitMethod.visitMaxs(unsignedShort16, unsignedShort17);
                }
                if (visitMethod != null) {
                    visitMethod.visitEnd();
                }
            }
            --unsignedShort12;
        }
        $ClassVisitor.visitEnd();
    }
    
    private void a(int a, final String s, final char[] array, final boolean b, final $MethodVisitor $MethodVisitor) {
        final int n = this.b[a++] & 0xFF;
        int n2;
        int i;
        for (n2 = $Type.getArgumentTypes(s).length - n, i = 0; i < n2; ++i) {
            final $AnnotationVisitor visitParameterAnnotation = $MethodVisitor.visitParameterAnnotation(i, "Ljava/lang/Synthetic;", false);
            if (visitParameterAnnotation != null) {
                visitParameterAnnotation.visitEnd();
            }
        }
        while (i < n + n2) {
            int j = this.readUnsignedShort(a);
            a += 2;
            while (j > 0) {
                a = this.a(a + 2, array, true, $MethodVisitor.visitParameterAnnotation(i, this.readUTF8(a, array), b));
                --j;
            }
            ++i;
        }
    }
    
    private int a(int n, final char[] array, final boolean b, final $AnnotationVisitor $AnnotationVisitor) {
        int i = this.readUnsignedShort(n);
        n += 2;
        if (b) {
            while (i > 0) {
                n = this.a(n + 2, array, this.readUTF8(n, array), $AnnotationVisitor);
                --i;
            }
        }
        else {
            while (i > 0) {
                n = this.a(n, array, null, $AnnotationVisitor);
                --i;
            }
        }
        if ($AnnotationVisitor != null) {
            $AnnotationVisitor.visitEnd();
        }
        return n;
    }
    
    private int a(int n, final char[] array, final String s, final $AnnotationVisitor $AnnotationVisitor) {
        if ($AnnotationVisitor != null) {
            Label_1259: {
                switch (this.b[n++] & 0xFF) {
                    case 68:
                    case 70:
                    case 73:
                    case 74: {
                        $AnnotationVisitor.visit(s, this.readConst(this.readUnsignedShort(n), array));
                        n += 2;
                        break;
                    }
                    case 66: {
                        $AnnotationVisitor.visit(s, new Byte((byte)this.readInt(this.a[this.readUnsignedShort(n)])));
                        n += 2;
                        break;
                    }
                    case 90: {
                        $AnnotationVisitor.visit(s, (this.readInt(this.a[this.readUnsignedShort(n)]) == 0) ? Boolean.FALSE : Boolean.TRUE);
                        n += 2;
                        break;
                    }
                    case 83: {
                        $AnnotationVisitor.visit(s, new Short((short)this.readInt(this.a[this.readUnsignedShort(n)])));
                        n += 2;
                        break;
                    }
                    case 67: {
                        $AnnotationVisitor.visit(s, new Character((char)this.readInt(this.a[this.readUnsignedShort(n)])));
                        n += 2;
                        break;
                    }
                    case 115: {
                        $AnnotationVisitor.visit(s, this.readUTF8(n, array));
                        n += 2;
                        break;
                    }
                    case 101: {
                        $AnnotationVisitor.visitEnum(s, this.readUTF8(n, array), this.readUTF8(n + 2, array));
                        n += 4;
                        break;
                    }
                    case 99: {
                        $AnnotationVisitor.visit(s, $Type.getType(this.readUTF8(n, array)));
                        n += 2;
                        break;
                    }
                    case 64: {
                        n = this.a(n + 2, array, true, $AnnotationVisitor.visitAnnotation(s, this.readUTF8(n, array)));
                        break;
                    }
                    case 91: {
                        final int unsignedShort = this.readUnsignedShort(n);
                        n += 2;
                        if (unsignedShort == 0) {
                            return this.a(n - 2, array, false, $AnnotationVisitor.visitArray(s));
                        }
                        switch (this.b[n++] & 0xFF) {
                            case 66: {
                                final byte[] array2 = new byte[unsignedShort];
                                for (int i = 0; i < unsignedShort; ++i) {
                                    array2[i] = (byte)this.readInt(this.a[this.readUnsignedShort(n)]);
                                    n += 3;
                                }
                                $AnnotationVisitor.visit(s, array2);
                                --n;
                                break Label_1259;
                            }
                            case 90: {
                                final boolean[] array3 = new boolean[unsignedShort];
                                for (int j = 0; j < unsignedShort; ++j) {
                                    array3[j] = (this.readInt(this.a[this.readUnsignedShort(n)]) != 0);
                                    n += 3;
                                }
                                $AnnotationVisitor.visit(s, array3);
                                --n;
                                break Label_1259;
                            }
                            case 83: {
                                final short[] array4 = new short[unsignedShort];
                                for (int k = 0; k < unsignedShort; ++k) {
                                    array4[k] = (short)this.readInt(this.a[this.readUnsignedShort(n)]);
                                    n += 3;
                                }
                                $AnnotationVisitor.visit(s, array4);
                                --n;
                                break Label_1259;
                            }
                            case 67: {
                                final char[] array5 = new char[unsignedShort];
                                for (int l = 0; l < unsignedShort; ++l) {
                                    array5[l] = (char)this.readInt(this.a[this.readUnsignedShort(n)]);
                                    n += 3;
                                }
                                $AnnotationVisitor.visit(s, array5);
                                --n;
                                break Label_1259;
                            }
                            case 73: {
                                final int[] array6 = new int[unsignedShort];
                                for (int n2 = 0; n2 < unsignedShort; ++n2) {
                                    array6[n2] = this.readInt(this.a[this.readUnsignedShort(n)]);
                                    n += 3;
                                }
                                $AnnotationVisitor.visit(s, array6);
                                --n;
                                break Label_1259;
                            }
                            case 74: {
                                final long[] array7 = new long[unsignedShort];
                                for (int n3 = 0; n3 < unsignedShort; ++n3) {
                                    array7[n3] = this.readLong(this.a[this.readUnsignedShort(n)]);
                                    n += 3;
                                }
                                $AnnotationVisitor.visit(s, array7);
                                --n;
                                break Label_1259;
                            }
                            case 70: {
                                final float[] array8 = new float[unsignedShort];
                                for (int n4 = 0; n4 < unsignedShort; ++n4) {
                                    array8[n4] = Float.intBitsToFloat(this.readInt(this.a[this.readUnsignedShort(n)]));
                                    n += 3;
                                }
                                $AnnotationVisitor.visit(s, array8);
                                --n;
                                break Label_1259;
                            }
                            case 68: {
                                final double[] array9 = new double[unsignedShort];
                                for (int n5 = 0; n5 < unsignedShort; ++n5) {
                                    array9[n5] = Double.longBitsToDouble(this.readLong(this.a[this.readUnsignedShort(n)]));
                                    n += 3;
                                }
                                $AnnotationVisitor.visit(s, array9);
                                --n;
                                break Label_1259;
                            }
                            default: {
                                n = this.a(n - 3, array, false, $AnnotationVisitor.visitArray(s));
                                break Label_1259;
                            }
                        }
                        break;
                    }
                }
            }
            return n;
        }
        switch (this.b[n] & 0xFF) {
            case 101: {
                return n + 5;
            }
            case 64: {
                return this.a(n + 3, array, true, null);
            }
            case 91: {
                return this.a(n + 1, array, false, null);
            }
            default: {
                return n + 3;
            }
        }
    }
    
    private int a(final Object[] array, final int n, int n2, final char[] array2, final $Label[] array3) {
        switch (this.b[n2++] & 0xFF) {
            case 0: {
                array[n] = $Opcodes.TOP;
                break;
            }
            case 1: {
                array[n] = $Opcodes.INTEGER;
                break;
            }
            case 2: {
                array[n] = $Opcodes.FLOAT;
                break;
            }
            case 3: {
                array[n] = $Opcodes.DOUBLE;
                break;
            }
            case 4: {
                array[n] = $Opcodes.LONG;
                break;
            }
            case 5: {
                array[n] = $Opcodes.NULL;
                break;
            }
            case 6: {
                array[n] = $Opcodes.UNINITIALIZED_THIS;
                break;
            }
            case 7: {
                array[n] = this.readClass(n2, array2);
                n2 += 2;
                break;
            }
            default: {
                array[n] = this.readLabel(this.readUnsignedShort(n2), array3);
                n2 += 2;
                break;
            }
        }
        return n2;
    }
    
    protected $Label readLabel(final int n, final $Label[] array) {
        if (array[n] == null) {
            array[n] = new $Label();
        }
        return array[n];
    }
    
    private $Attribute a(final $Attribute[] array, final String anObject, final int n, final int n2, final char[] array2, final int n3, final $Label[] array3) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].type.equals(anObject)) {
                return array[i].read(this, n, n2, array2, n3, array3);
            }
        }
        return new $Attribute(anObject).read(this, n, n2, null, -1, null);
    }
    
    public int getItem(final int n) {
        return this.a[n];
    }
    
    public int readByte(final int n) {
        return this.b[n] & 0xFF;
    }
    
    public int readUnsignedShort(final int n) {
        final byte[] b = this.b;
        return (b[n] & 0xFF) << 8 | (b[n + 1] & 0xFF);
    }
    
    public short readShort(final int n) {
        final byte[] b = this.b;
        return (short)((b[n] & 0xFF) << 8 | (b[n + 1] & 0xFF));
    }
    
    public int readInt(final int n) {
        final byte[] b = this.b;
        return (b[n] & 0xFF) << 24 | (b[n + 1] & 0xFF) << 16 | (b[n + 2] & 0xFF) << 8 | (b[n + 3] & 0xFF);
    }
    
    public long readLong(final int n) {
        return (long)this.readInt(n) << 32 | ((long)this.readInt(n + 4) & 0xFFFFFFFFL);
    }
    
    public String readUTF8(int n, final char[] array) {
        final int unsignedShort = this.readUnsignedShort(n);
        final String s = this.c[unsignedShort];
        if (s != null) {
            return s;
        }
        n = this.a[unsignedShort];
        return this.c[unsignedShort] = this.a(n + 2, this.readUnsignedShort(n), array);
    }
    
    private String a(int i, final int n, final char[] value) {
        final int n2 = i + n;
        final byte[] b = this.b;
        int count = 0;
        while (i < n2) {
            final int n3 = b[i++] & 0xFF;
            switch (n3 >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: {
                    value[count++] = (char)n3;
                    continue;
                }
                case 12:
                case 13: {
                    value[count++] = (char)((n3 & 0x1F) << 6 | (b[i++] & 0x3F));
                    continue;
                }
                default: {
                    value[count++] = (char)((n3 & 0xF) << 12 | (b[i++] & 0x3F) << 6 | (b[i++] & 0x3F));
                    continue;
                }
            }
        }
        return new String(value, 0, count);
    }
    
    public String readClass(final int n, final char[] array) {
        return this.readUTF8(this.a[this.readUnsignedShort(n)], array);
    }
    
    public Object readConst(final int n, final char[] array) {
        final int n2 = this.a[n];
        switch (this.b[n2 - 1]) {
            case 3: {
                return new Integer(this.readInt(n2));
            }
            case 4: {
                return new Float(Float.intBitsToFloat(this.readInt(n2)));
            }
            case 5: {
                return new Long(this.readLong(n2));
            }
            case 6: {
                return new Double(Double.longBitsToDouble(this.readLong(n2)));
            }
            case 7: {
                return $Type.getObjectType(this.readUTF8(n2, array));
            }
            default: {
                return this.readUTF8(n2, array);
            }
        }
    }
}
