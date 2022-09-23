// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.asm;

class $MethodWriter implements $MethodVisitor
{
    $MethodWriter a;
    final $ClassWriter b;
    private int c;
    private final int d;
    private final int e;
    private final String f;
    String g;
    int h;
    int i;
    int j;
    int[] k;
    private $ByteVector l;
    private $AnnotationWriter m;
    private $AnnotationWriter n;
    private $AnnotationWriter[] o;
    private $AnnotationWriter[] p;
    private int S;
    private $Attribute q;
    private $ByteVector r;
    private int s;
    private int t;
    private int u;
    private $ByteVector v;
    private int w;
    private int[] x;
    private int y;
    private int[] z;
    private int A;
    private $Handler B;
    private $Handler C;
    private int D;
    private $ByteVector E;
    private int F;
    private $ByteVector G;
    private int H;
    private $ByteVector I;
    private $Attribute J;
    private boolean K;
    private int L;
    private final int M;
    private $Label N;
    private $Label O;
    private $Label P;
    private int Q;
    private int R;
    
    $MethodWriter(final $ClassWriter b, final int c, final String anObject, final String f, final String g, final String[] array, final boolean b2, final boolean b3) {
        this.r = new $ByteVector();
        if (b.A == null) {
            b.A = this;
        }
        else {
            b.B.a = this;
        }
        b.B = this;
        this.b = b;
        this.c = c;
        this.d = b.newUTF8(anObject);
        this.e = b.newUTF8(f);
        this.f = f;
        this.g = g;
        if (array != null && array.length > 0) {
            this.j = array.length;
            this.k = new int[this.j];
            for (int i = 0; i < this.j; ++i) {
                this.k[i] = b.newClass(array[i]);
            }
        }
        this.M = (b3 ? 0 : (b2 ? 1 : 2));
        if (b2 || b3) {
            if (b3 && "<init>".equals(anObject)) {
                this.c |= 0x40000;
            }
            int t = a(this.f) >> 2;
            if ((c & 0x8) != 0x0) {
                --t;
            }
            this.t = t;
            this.N = new $Label();
            final $Label n = this.N;
            n.a |= 0x8;
            this.visitLabel(this.N);
        }
    }
    
    public $AnnotationVisitor visitAnnotationDefault() {
        this.l = new $ByteVector();
        return new $AnnotationWriter(this.b, false, this.l, null, 0);
    }
    
    public $AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        final $ByteVector $ByteVector = new $ByteVector();
        $ByteVector.putShort(this.b.newUTF8(s)).putShort(0);
        final $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this.b, true, $ByteVector, $ByteVector, 2);
        if (b) {
            $AnnotationWriter.g = this.m;
            this.m = $AnnotationWriter;
        }
        else {
            $AnnotationWriter.g = this.n;
            this.n = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }
    
    public $AnnotationVisitor visitParameterAnnotation(final int n, final String anObject, final boolean b) {
        final $ByteVector $ByteVector = new $ByteVector();
        if ("Ljava/lang/Synthetic;".equals(anObject)) {
            this.S = Math.max(this.S, n + 1);
            return new $AnnotationWriter(this.b, false, $ByteVector, null, 0);
        }
        $ByteVector.putShort(this.b.newUTF8(anObject)).putShort(0);
        final $AnnotationWriter $AnnotationWriter = new $AnnotationWriter(this.b, true, $ByteVector, $ByteVector, 2);
        if (b) {
            if (this.o == null) {
                this.o = new $AnnotationWriter[$Type.getArgumentTypes(this.f).length];
            }
            $AnnotationWriter.g = this.o[n];
            this.o[n] = $AnnotationWriter;
        }
        else {
            if (this.p == null) {
                this.p = new $AnnotationWriter[$Type.getArgumentTypes(this.f).length];
            }
            $AnnotationWriter.g = this.p[n];
            this.p[n] = $AnnotationWriter;
        }
        return $AnnotationWriter;
    }
    
    public void visitAttribute(final $Attribute $Attribute) {
        if ($Attribute.isCodeAttribute()) {
            $Attribute.a = this.J;
            this.J = $Attribute;
        }
        else {
            $Attribute.a = this.q;
            this.q = $Attribute;
        }
    }
    
    public void visitCode() {
    }
    
    public void visitFrame(final int n, final int n2, final Object[] array, final int n3, final Object[] array2) {
        if (this.M == 0) {
            return;
        }
        if (n == -1) {
            this.a(this.r.b, n2, n3);
            for (int i = 0; i < n2; ++i) {
                if (array[i] instanceof String) {
                    this.z[this.y++] = (0x1700000 | this.b.c((String)array[i]));
                }
                else if (array[i] instanceof Integer) {
                    this.z[this.y++] = (int)array[i];
                }
                else {
                    this.z[this.y++] = (0x1800000 | this.b.a("", (($Label)array[i]).c));
                }
            }
            for (int j = 0; j < n3; ++j) {
                if (array2[j] instanceof String) {
                    this.z[this.y++] = (0x1700000 | this.b.c((String)array2[j]));
                }
                else if (array2[j] instanceof Integer) {
                    this.z[this.y++] = (int)array2[j];
                }
                else {
                    this.z[this.y++] = (0x1800000 | this.b.a("", (($Label)array2[j]).c));
                }
            }
            this.b();
        }
        else {
            int b;
            if (this.v == null) {
                this.v = new $ByteVector();
                b = this.r.b;
            }
            else {
                b = this.r.b - this.w - 1;
            }
            switch (n) {
                case 0: {
                    this.v.putByte(255).putShort(b).putShort(n2);
                    for (int k = 0; k < n2; ++k) {
                        this.a(array[k]);
                    }
                    this.v.putShort(n3);
                    for (int l = 0; l < n3; ++l) {
                        this.a(array2[l]);
                    }
                    break;
                }
                case 1: {
                    this.v.putByte(251 + n2).putShort(b);
                    for (int n4 = 0; n4 < n2; ++n4) {
                        this.a(array[n4]);
                    }
                    break;
                }
                case 2: {
                    this.v.putByte(251 - n2).putShort(b);
                    break;
                }
                case 3: {
                    if (b < 64) {
                        this.v.putByte(b);
                        break;
                    }
                    this.v.putByte(251).putShort(b);
                    break;
                }
                case 4: {
                    if (b < 64) {
                        this.v.putByte(64 + b);
                    }
                    else {
                        this.v.putByte(247).putShort(b);
                    }
                    this.a(array2[0]);
                    break;
                }
            }
            this.w = this.r.b;
            ++this.u;
        }
    }
    
    public void visitInsn(final int n) {
        this.r.putByte(n);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, null, null);
            }
            else {
                final int n2 = this.Q + $Frame.a[n];
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
            if ((n >= 172 && n <= 177) || n == 191) {
                this.e();
            }
        }
    }
    
    public void visitIntInsn(final int n, final int n2) {
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, n2, null, null);
            }
            else if (n != 188) {
                final int n3 = this.Q + 1;
                if (n3 > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (n == 17) {
            this.r.b(n, n2);
        }
        else {
            this.r.a(n, n2);
        }
    }
    
    public void visitVarInsn(final int n, final int n2) {
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, n2, null, null);
            }
            else if (n == 169) {
                final $Label p2 = this.P;
                p2.a |= 0x100;
                this.P.f = this.Q;
                this.e();
            }
            else {
                final int n3 = this.Q + $Frame.a[n];
                if (n3 > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (this.M != 2) {
            int t;
            if (n == 22 || n == 24 || n == 55 || n == 57) {
                t = n2 + 2;
            }
            else {
                t = n2 + 1;
            }
            if (t > this.t) {
                this.t = t;
            }
        }
        if (n2 < 4 && n != 169) {
            int n4;
            if (n < 54) {
                n4 = 26 + (n - 21 << 2) + n2;
            }
            else {
                n4 = 59 + (n - 54 << 2) + n2;
            }
            this.r.putByte(n4);
        }
        else if (n2 >= 256) {
            this.r.putByte(196).b(n, n2);
        }
        else {
            this.r.a(n, n2);
        }
        if (n >= 54 && this.M == 0 && this.A > 0) {
            this.visitLabel(new $Label());
        }
    }
    
    public void visitTypeInsn(final int n, final String s) {
        final $Item a = this.b.a(s);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, this.r.b, this.b, a);
            }
            else if (n == 187) {
                final int n2 = this.Q + 1;
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
        }
        this.r.b(n, a.a);
    }
    
    public void visitFieldInsn(final int n, final String s, final String s2, final String s3) {
        final $Item a = this.b.a(s, s2, s3);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, this.b, a);
            }
            else {
                final char char1 = s3.charAt(0);
                int n2 = 0;
                switch (n) {
                    case 178: {
                        n2 = this.Q + ((char1 == 'D' || char1 == 'J') ? 2 : 1);
                        break;
                    }
                    case 179: {
                        n2 = this.Q + ((char1 == 'D' || char1 == 'J') ? -2 : -1);
                        break;
                    }
                    case 180: {
                        n2 = this.Q + ((char1 == 'D' || char1 == 'J') ? 1 : 0);
                        break;
                    }
                    default: {
                        n2 = this.Q + ((char1 == 'D' || char1 == 'J') ? -3 : -2);
                        break;
                    }
                }
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
        }
        this.r.b(n, a.a);
    }
    
    public void visitMethodInsn(final int n, final String s, final String s2, final String s3) {
        final boolean b = n == 185;
        final $Item a = this.b.a(s, s2, s3, b);
        int n2 = a.c;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, this.b, a);
            }
            else {
                if (n2 == 0) {
                    n2 = a(s3);
                    a.c = n2;
                }
                int n3;
                if (n == 184) {
                    n3 = this.Q - (n2 >> 2) + (n2 & 0x3) + 1;
                }
                else {
                    n3 = this.Q - (n2 >> 2) + (n2 & 0x3);
                }
                if (n3 > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (b) {
            if (n2 == 0) {
                n2 = a(s3);
                a.c = n2;
            }
            this.r.b(185, a.a).a(n2 >> 2, 0);
        }
        else {
            this.r.b(n, a.a);
        }
    }
    
    public void visitJumpInsn(final int n, final $Label $Label) {
        $Label $Label2 = null;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, null, null);
                final $Label a = $Label.a();
                a.a |= 0x10;
                this.a(0, $Label);
                if (n != 167) {
                    $Label2 = new $Label();
                }
            }
            else if (n == 168) {
                if (($Label.a & 0x200) == 0x0) {
                    $Label.a |= 0x200;
                    ++this.L;
                }
                final $Label p2 = this.P;
                p2.a |= 0x80;
                this.a(this.Q + 1, $Label);
                $Label2 = new $Label();
            }
            else {
                this.a(this.Q += $Frame.a[n], $Label);
            }
        }
        if (($Label.a & 0x2) != 0x0 && $Label.c - this.r.b < -32768) {
            if (n == 167) {
                this.r.putByte(200);
            }
            else if (n == 168) {
                this.r.putByte(201);
            }
            else {
                if ($Label2 != null) {
                    final $Label $Label3 = $Label2;
                    $Label3.a |= 0x10;
                }
                this.r.putByte((n <= 166) ? ((n + 1 ^ 0x1) - 1) : (n ^ 0x1));
                this.r.putShort(8);
                this.r.putByte(200);
            }
            $Label.a(this, this.r, this.r.b - 1, true);
        }
        else {
            this.r.putByte(n);
            $Label.a(this, this.r, this.r.b - 1, false);
        }
        if (this.P != null) {
            if ($Label2 != null) {
                this.visitLabel($Label2);
            }
            if (n == 167) {
                this.e();
            }
        }
    }
    
    public void visitLabel(final $Label o) {
        this.K |= o.a(this, this.r.b, this.r.a);
        if ((o.a & 0x1) != 0x0) {
            return;
        }
        if (this.M == 0) {
            if (this.P != null) {
                if (o.c == this.P.c) {
                    final $Label p = this.P;
                    p.a |= (o.a & 0x10);
                    o.h = this.P.h;
                    return;
                }
                this.a(0, o);
            }
            this.P = o;
            if (o.h == null) {
                o.h = new $Frame();
                o.h.b = o;
            }
            if (this.O != null) {
                if (o.c == this.O.c) {
                    final $Label o2 = this.O;
                    o2.a |= (o.a & 0x10);
                    o.h = this.O.h;
                    this.P = this.O;
                    return;
                }
                this.O.i = o;
            }
            this.O = o;
        }
        else if (this.M == 1) {
            if (this.P != null) {
                this.P.g = this.R;
                this.a(this.Q, o);
            }
            this.P = o;
            this.Q = 0;
            this.R = 0;
            if (this.O != null) {
                this.O.i = o;
            }
            this.O = o;
        }
    }
    
    public void visitLdcInsn(final Object o) {
        final $Item a = this.b.a(o);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(18, 0, this.b, a);
            }
            else {
                int n;
                if (a.b == 5 || a.b == 6) {
                    n = this.Q + 2;
                }
                else {
                    n = this.Q + 1;
                }
                if (n > this.R) {
                    this.R = n;
                }
                this.Q = n;
            }
        }
        final int a2 = a.a;
        if (a.b == 5 || a.b == 6) {
            this.r.b(20, a2);
        }
        else if (a2 >= 256) {
            this.r.b(19, a2);
        }
        else {
            this.r.a(18, a2);
        }
    }
    
    public void visitIincInsn(final int n, final int n2) {
        if (this.P != null && this.M == 0) {
            this.P.h.a(132, n, null, null);
        }
        if (this.M != 2) {
            final int t = n + 1;
            if (t > this.t) {
                this.t = t;
            }
        }
        if (n > 255 || n2 > 127 || n2 < -128) {
            this.r.putByte(196).b(132, n).putShort(n2);
        }
        else {
            this.r.putByte(132).a(n, n2);
        }
    }
    
    public void visitTableSwitchInsn(final int n, final int n2, final $Label $Label, final $Label[] array) {
        final int b = this.r.b;
        this.r.putByte(170);
        final $ByteVector r = this.r;
        r.b += (4 - this.r.b % 4) % 4;
        $Label.a(this, this.r, b, true);
        this.r.putInt(n).putInt(n2);
        for (int i = 0; i < array.length; ++i) {
            array[i].a(this, this.r, b, true);
        }
        this.a($Label, array);
    }
    
    public void visitLookupSwitchInsn(final $Label $Label, final int[] array, final $Label[] array2) {
        final int b = this.r.b;
        this.r.putByte(171);
        final $ByteVector r = this.r;
        r.b += (4 - this.r.b % 4) % 4;
        $Label.a(this, this.r, b, true);
        this.r.putInt(array2.length);
        for (int i = 0; i < array2.length; ++i) {
            this.r.putInt(array[i]);
            array2[i].a(this, this.r, b, true);
        }
        this.a($Label, array2);
    }
    
    private void a(final $Label $Label, final $Label[] array) {
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(171, 0, null, null);
                this.a(0, $Label);
                final $Label a = $Label.a();
                a.a |= 0x10;
                for (int i = 0; i < array.length; ++i) {
                    this.a(0, array[i]);
                    final $Label a2 = array[i].a();
                    a2.a |= 0x10;
                }
            }
            else {
                this.a(--this.Q, $Label);
                for (int j = 0; j < array.length; ++j) {
                    this.a(this.Q, array[j]);
                }
            }
            this.e();
        }
    }
    
    public void visitMultiANewArrayInsn(final String s, final int n) {
        final $Item a = this.b.a(s);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(197, n, this.b, a);
            }
            else {
                this.Q += 1 - n;
            }
        }
        this.r.b(197, a.a).putByte(n);
    }
    
    public void visitTryCatchBlock(final $Label a, final $Label b, final $Label c, final String d) {
        ++this.A;
        final $Handler c2 = new $Handler();
        c2.a = a;
        c2.b = b;
        c2.c = c;
        c2.d = d;
        c2.e = ((d != null) ? this.b.newClass(d) : 0);
        if (this.C == null) {
            this.B = c2;
        }
        else {
            this.C.f = c2;
        }
        this.C = c2;
    }
    
    public void visitLocalVariable(final String s, final String s2, final String s3, final $Label $Label, final $Label $Label2, final int n) {
        if (s3 != null) {
            if (this.G == null) {
                this.G = new $ByteVector();
            }
            ++this.F;
            this.G.putShort($Label.c).putShort($Label2.c - $Label.c).putShort(this.b.newUTF8(s)).putShort(this.b.newUTF8(s3)).putShort(n);
        }
        if (this.E == null) {
            this.E = new $ByteVector();
        }
        ++this.D;
        this.E.putShort($Label.c).putShort($Label2.c - $Label.c).putShort(this.b.newUTF8(s)).putShort(this.b.newUTF8(s2)).putShort(n);
        if (this.M != 2) {
            final char char1 = s2.charAt(0);
            final int t = n + ((char1 == 'J' || char1 == 'D') ? 2 : 1);
            if (t > this.t) {
                this.t = t;
            }
        }
    }
    
    public void visitLineNumber(final int n, final $Label $Label) {
        if (this.I == null) {
            this.I = new $ByteVector();
        }
        ++this.H;
        this.I.putShort($Label.c);
        this.I.putShort(n);
    }
    
    public void visitMaxs(final int s, final int t) {
        if (this.M == 0) {
            for ($Handler $Handler = this.B; $Handler != null; $Handler = $Handler.f) {
                $Label $Label = $Handler.a.a();
                final $Label a = $Handler.c.a();
                final $Label a2 = $Handler.b.a();
                final int a3 = 0x1700000 | this.b.c(($Handler.d == null) ? "java/lang/Throwable" : $Handler.d);
                final $Label $Label2 = a;
                $Label2.a |= 0x10;
                while ($Label != a2) {
                    final $Edge j = new $Edge();
                    j.a = a3;
                    j.b = a;
                    j.c = $Label.j;
                    $Label.j = j;
                    $Label = $Label.i;
                }
            }
            final $Frame h = this.N.h;
            h.a(this.b, this.c, $Type.getArgumentTypes(this.f), this.t);
            this.b(h);
            int s2 = 0;
            $Label k = this.N;
            while (k != null) {
                final $Label $Label3 = k;
                k = k.k;
                $Label3.k = null;
                final $Frame h2 = $Label3.h;
                if (($Label3.a & 0x10) != 0x0) {
                    final $Label $Label4 = $Label3;
                    $Label4.a |= 0x20;
                }
                final $Label $Label5 = $Label3;
                $Label5.a |= 0x40;
                final int n = h2.d.length + $Label3.g;
                if (n > s2) {
                    s2 = n;
                }
                for ($Edge $Edge = $Label3.j; $Edge != null; $Edge = $Edge.c) {
                    final $Label a4 = $Edge.b.a();
                    if (h2.a(this.b, a4.h, $Edge.a) && a4.k == null) {
                        a4.k = k;
                        k = a4;
                    }
                }
            }
            this.s = s2;
            for ($Label $Label6 = this.N; $Label6 != null; $Label6 = $Label6.i) {
                final $Frame h3 = $Label6.h;
                if (($Label6.a & 0x20) != 0x0) {
                    this.b(h3);
                }
                if (($Label6.a & 0x40) == 0x0) {
                    final $Label i = $Label6.i;
                    final int c = $Label6.c;
                    final int n2 = ((i == null) ? this.r.b : i.c) - 1;
                    if (n2 >= c) {
                        for (int l = c; l < n2; ++l) {
                            this.r.a[l] = 0;
                        }
                        this.r.a[n2] = -65;
                        this.a(c, 0, 1);
                        this.z[this.y++] = (0x1700000 | this.b.c("java/lang/Throwable"));
                        this.b();
                    }
                }
            }
        }
        else if (this.M == 1) {
            for ($Handler $Handler2 = this.B; $Handler2 != null; $Handler2 = $Handler2.f) {
                $Label $Label7 = $Handler2.a;
                final $Label c2 = $Handler2.c;
                while ($Label7 != $Handler2.b) {
                    final $Edge $Edge2 = new $Edge();
                    $Edge2.a = Integer.MAX_VALUE;
                    $Edge2.b = c2;
                    if (($Label7.a & 0x80) == 0x0) {
                        $Edge2.c = $Label7.j;
                        $Label7.j = $Edge2;
                    }
                    else {
                        $Edge2.c = $Label7.j.c.c;
                        $Label7.j.c.c = $Edge2;
                    }
                    $Label7 = $Label7.i;
                }
            }
            if (this.L > 0) {
                int n3 = 0;
                this.N.b(null, 1L, this.L);
                for ($Label $Label8 = this.N; $Label8 != null; $Label8 = $Label8.i) {
                    if (($Label8.a & 0x80) != 0x0) {
                        final $Label b = $Label8.j.c.b;
                        if ((b.a & 0x400) == 0x0) {
                            ++n3;
                            b.b(null, n3 / 32L << 32 | 1L << n3 % 32, this.L);
                        }
                    }
                }
                for ($Label $Label9 = this.N; $Label9 != null; $Label9 = $Label9.i) {
                    if (($Label9.a & 0x80) != 0x0) {
                        for ($Label $Label10 = this.N; $Label10 != null; $Label10 = $Label10.i) {
                            final $Label $Label11 = $Label10;
                            $Label11.a &= 0xFFFFFBFF;
                        }
                        $Label9.j.c.b.b($Label9, 0L, this.L);
                    }
                }
            }
            int s3 = 0;
            $Label m = this.N;
            while (m != null) {
                final $Label $Label12 = m;
                m = m.k;
                final int f = $Label12.f;
                final int n4 = f + $Label12.g;
                if (n4 > s3) {
                    s3 = n4;
                }
                $Edge $Edge3 = $Label12.j;
                if (($Label12.a & 0x80) != 0x0) {
                    $Edge3 = $Edge3.c;
                }
                while ($Edge3 != null) {
                    final $Label b2 = $Edge3.b;
                    if ((b2.a & 0x8) == 0x0) {
                        b2.f = (($Edge3.a == Integer.MAX_VALUE) ? 1 : (f + $Edge3.a));
                        final $Label $Label13 = b2;
                        $Label13.a |= 0x8;
                        b2.k = m;
                        m = b2;
                    }
                    $Edge3 = $Edge3.c;
                }
            }
            this.s = s3;
        }
        else {
            this.s = s;
            this.t = t;
        }
    }
    
    public void visitEnd() {
    }
    
    static int a(final String s) {
        int n = 1;
        int n2 = 1;
        while (true) {
            final char char1 = s.charAt(n2++);
            if (char1 == ')') {
                break;
            }
            if (char1 == 'L') {
                while (s.charAt(n2++) != ';') {}
                ++n;
            }
            else if (char1 == '[') {
                char char2;
                while ((char2 = s.charAt(n2)) == '[') {
                    ++n2;
                }
                if (char2 != 'D' && char2 != 'J') {
                    continue;
                }
                --n;
            }
            else if (char1 == 'D' || char1 == 'J') {
                n += 2;
            }
            else {
                ++n;
            }
        }
        final char char3 = s.charAt(n2);
        return n << 2 | ((char3 == 'V') ? 0 : ((char3 == 'D' || char3 == 'J') ? 2 : 1));
    }
    
    private void a(final int a, final $Label b) {
        final $Edge j = new $Edge();
        j.a = a;
        j.b = b;
        j.c = this.P.j;
        this.P.j = j;
    }
    
    private void e() {
        if (this.M == 0) {
            final $Label o = new $Label();
            o.h = new $Frame();
            (o.h.b = o).a(this, this.r.b, this.r.a);
            this.O.i = o;
            this.O = o;
        }
        else {
            this.P.g = this.R;
        }
        this.P = null;
    }
    
    private void b(final $Frame $Frame) {
        int n = 0;
        int i = 0;
        int n2 = 0;
        final int[] c = $Frame.c;
        final int[] d = $Frame.d;
        for (int j = 0; j < c.length; ++j) {
            final int n3 = c[j];
            if (n3 == 16777216) {
                ++n;
            }
            else {
                i += n + 1;
                n = 0;
            }
            if (n3 == 16777220 || n3 == 16777219) {
                ++j;
            }
        }
        for (int k = 0; k < d.length; ++k) {
            final int n4 = d[k];
            ++n2;
            if (n4 == 16777220 || n4 == 16777219) {
                ++k;
            }
        }
        this.a($Frame.b.c, i, n2);
        int n5 = 0;
        while (i > 0) {
            final int n6 = c[n5];
            this.z[this.y++] = n6;
            if (n6 == 16777220 || n6 == 16777219) {
                ++n5;
            }
            ++n5;
            --i;
        }
        for (int l = 0; l < d.length; ++l) {
            final int n7 = d[l];
            this.z[this.y++] = n7;
            if (n7 == 16777220 || n7 == 16777219) {
                ++l;
            }
        }
        this.b();
    }
    
    private void a(final int n, final int n2, final int n3) {
        final int n4 = 3 + n2 + n3;
        if (this.z == null || this.z.length < n4) {
            this.z = new int[n4];
        }
        this.z[0] = n;
        this.z[1] = n2;
        this.z[2] = n3;
        this.y = 3;
    }
    
    private void b() {
        if (this.x != null) {
            if (this.v == null) {
                this.v = new $ByteVector();
            }
            this.c();
            ++this.u;
        }
        this.x = this.z;
        this.z = null;
    }
    
    private void c() {
        final int n = this.z[1];
        final int n2 = this.z[2];
        if ((this.b.b & 0xFFFF) < 50) {
            this.v.putShort(this.z[0]).putShort(n);
            this.a(3, 3 + n);
            this.v.putShort(n2);
            this.a(3 + n, 3 + n + n2);
            return;
        }
        int n3 = this.x[1];
        int n4 = 255;
        int n5 = 0;
        int n6;
        if (this.u == 0) {
            n6 = this.z[0];
        }
        else {
            n6 = this.z[0] - this.x[0] - 1;
        }
        if (n2 == 0) {
            n5 = n - n3;
            switch (n5) {
                case -3:
                case -2:
                case -1: {
                    n4 = 248;
                    n3 = n;
                    break;
                }
                case 0: {
                    n4 = ((n6 < 64) ? 0 : 251);
                    break;
                }
                case 1:
                case 2:
                case 3: {
                    n4 = 252;
                    break;
                }
            }
        }
        else if (n == n3 && n2 == 1) {
            n4 = ((n6 < 63) ? 64 : 247);
        }
        if (n4 != 255) {
            int n7 = 3;
            for (int i = 0; i < n3; ++i) {
                if (this.z[n7] != this.x[n7]) {
                    n4 = 255;
                    break;
                }
                ++n7;
            }
        }
        switch (n4) {
            case 0: {
                this.v.putByte(n6);
                break;
            }
            case 64: {
                this.v.putByte(64 + n6);
                this.a(3 + n, 4 + n);
                break;
            }
            case 247: {
                this.v.putByte(247).putShort(n6);
                this.a(3 + n, 4 + n);
                break;
            }
            case 251: {
                this.v.putByte(251).putShort(n6);
                break;
            }
            case 248: {
                this.v.putByte(251 + n5).putShort(n6);
                break;
            }
            case 252: {
                this.v.putByte(251 + n5).putShort(n6);
                this.a(3 + n3, 3 + n);
                break;
            }
            default: {
                this.v.putByte(255).putShort(n6).putShort(n);
                this.a(3, 3 + n);
                this.v.putShort(n2);
                this.a(3 + n, 3 + n + n2);
                break;
            }
        }
    }
    
    private void a(final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            final int n3 = this.z[i];
            final int n4 = n3 & 0xF0000000;
            if (n4 == 0) {
                final int n5 = n3 & 0xFFFFF;
                switch (n3 & 0xFF00000) {
                    case 24117248: {
                        this.v.putByte(7).putShort(this.b.newClass(this.b.E[n5].g));
                        break;
                    }
                    case 25165824: {
                        this.v.putByte(8).putShort(this.b.E[n5].c);
                        break;
                    }
                    default: {
                        this.v.putByte(n5);
                        break;
                    }
                }
            }
            else {
                final StringBuffer sb = new StringBuffer();
                int n6 = n4 >> 28;
                while (n6-- > 0) {
                    sb.append('[');
                }
                if ((n3 & 0xFF00000) == 0x1700000) {
                    sb.append('L');
                    sb.append(this.b.E[n3 & 0xFFFFF].g);
                    sb.append(';');
                }
                else {
                    switch (n3 & 0xF) {
                        case 1: {
                            sb.append('I');
                            break;
                        }
                        case 2: {
                            sb.append('F');
                            break;
                        }
                        case 3: {
                            sb.append('D');
                            break;
                        }
                        case 9: {
                            sb.append('Z');
                            break;
                        }
                        case 10: {
                            sb.append('B');
                            break;
                        }
                        case 11: {
                            sb.append('C');
                            break;
                        }
                        case 12: {
                            sb.append('S');
                            break;
                        }
                        default: {
                            sb.append('J');
                            break;
                        }
                    }
                }
                this.v.putByte(7).putShort(this.b.newClass(sb.toString()));
            }
        }
    }
    
    private void a(final Object o) {
        if (o instanceof String) {
            this.v.putByte(7).putShort(this.b.newClass((String)o));
        }
        else if (o instanceof Integer) {
            this.v.putByte((int)o);
        }
        else {
            this.v.putByte(8).putShort((($Label)o).c);
        }
    }
    
    final int a() {
        if (this.h != 0) {
            return 6 + this.i;
        }
        if (this.K) {
            this.d();
        }
        int n = 8;
        if (this.r.b > 0) {
            this.b.newUTF8("Code");
            n += 18 + this.r.b + 8 * this.A;
            if (this.E != null) {
                this.b.newUTF8("LocalVariableTable");
                n += 8 + this.E.b;
            }
            if (this.G != null) {
                this.b.newUTF8("LocalVariableTypeTable");
                n += 8 + this.G.b;
            }
            if (this.I != null) {
                this.b.newUTF8("LineNumberTable");
                n += 8 + this.I.b;
            }
            if (this.v != null) {
                this.b.newUTF8(((this.b.b & 0xFFFF) >= 50) ? "StackMapTable" : "StackMap");
                n += 8 + this.v.b;
            }
            if (this.J != null) {
                n += this.J.a(this.b, this.r.a, this.r.b, this.s, this.t);
            }
        }
        if (this.j > 0) {
            this.b.newUTF8("Exceptions");
            n += 8 + 2 * this.j;
        }
        if ((this.c & 0x1000) != 0x0 && (this.b.b & 0xFFFF) < 49) {
            this.b.newUTF8("Synthetic");
            n += 6;
        }
        if ((this.c & 0x20000) != 0x0) {
            this.b.newUTF8("Deprecated");
            n += 6;
        }
        if (this.g != null) {
            this.b.newUTF8("Signature");
            this.b.newUTF8(this.g);
            n += 8;
        }
        if (this.l != null) {
            this.b.newUTF8("AnnotationDefault");
            n += 6 + this.l.b;
        }
        if (this.m != null) {
            this.b.newUTF8("RuntimeVisibleAnnotations");
            n += 8 + this.m.a();
        }
        if (this.n != null) {
            this.b.newUTF8("RuntimeInvisibleAnnotations");
            n += 8 + this.n.a();
        }
        if (this.o != null) {
            this.b.newUTF8("RuntimeVisibleParameterAnnotations");
            n += 7 + 2 * (this.o.length - this.S);
            for (int i = this.o.length - 1; i >= this.S; --i) {
                n += ((this.o[i] == null) ? 0 : this.o[i].a());
            }
        }
        if (this.p != null) {
            this.b.newUTF8("RuntimeInvisibleParameterAnnotations");
            n += 7 + 2 * (this.p.length - this.S);
            for (int j = this.p.length - 1; j >= this.S; --j) {
                n += ((this.p[j] == null) ? 0 : this.p[j].a());
            }
        }
        if (this.q != null) {
            n += this.q.a(this.b, null, 0, -1, -1);
        }
        return n;
    }
    
    final void a(final $ByteVector $ByteVector) {
        $ByteVector.putShort(this.c).putShort(this.d).putShort(this.e);
        if (this.h != 0) {
            $ByteVector.putByteArray(this.b.J.b, this.h, this.i);
            return;
        }
        int n = 0;
        if (this.r.b > 0) {
            ++n;
        }
        if (this.j > 0) {
            ++n;
        }
        if ((this.c & 0x1000) != 0x0 && (this.b.b & 0xFFFF) < 49) {
            ++n;
        }
        if ((this.c & 0x20000) != 0x0) {
            ++n;
        }
        if (this.g != null) {
            ++n;
        }
        if (this.l != null) {
            ++n;
        }
        if (this.m != null) {
            ++n;
        }
        if (this.n != null) {
            ++n;
        }
        if (this.o != null) {
            ++n;
        }
        if (this.p != null) {
            ++n;
        }
        if (this.q != null) {
            n += this.q.a();
        }
        $ByteVector.putShort(n);
        if (this.r.b > 0) {
            int n2 = 12 + this.r.b + 8 * this.A;
            if (this.E != null) {
                n2 += 8 + this.E.b;
            }
            if (this.G != null) {
                n2 += 8 + this.G.b;
            }
            if (this.I != null) {
                n2 += 8 + this.I.b;
            }
            if (this.v != null) {
                n2 += 8 + this.v.b;
            }
            if (this.J != null) {
                n2 += this.J.a(this.b, this.r.a, this.r.b, this.s, this.t);
            }
            $ByteVector.putShort(this.b.newUTF8("Code")).putInt(n2);
            $ByteVector.putShort(this.s).putShort(this.t);
            $ByteVector.putInt(this.r.b).putByteArray(this.r.a, 0, this.r.b);
            $ByteVector.putShort(this.A);
            if (this.A > 0) {
                for ($Handler $Handler = this.B; $Handler != null; $Handler = $Handler.f) {
                    $ByteVector.putShort($Handler.a.c).putShort($Handler.b.c).putShort($Handler.c.c).putShort($Handler.e);
                }
            }
            int n3 = 0;
            if (this.E != null) {
                ++n3;
            }
            if (this.G != null) {
                ++n3;
            }
            if (this.I != null) {
                ++n3;
            }
            if (this.v != null) {
                ++n3;
            }
            if (this.J != null) {
                n3 += this.J.a();
            }
            $ByteVector.putShort(n3);
            if (this.E != null) {
                $ByteVector.putShort(this.b.newUTF8("LocalVariableTable"));
                $ByteVector.putInt(this.E.b + 2).putShort(this.D);
                $ByteVector.putByteArray(this.E.a, 0, this.E.b);
            }
            if (this.G != null) {
                $ByteVector.putShort(this.b.newUTF8("LocalVariableTypeTable"));
                $ByteVector.putInt(this.G.b + 2).putShort(this.F);
                $ByteVector.putByteArray(this.G.a, 0, this.G.b);
            }
            if (this.I != null) {
                $ByteVector.putShort(this.b.newUTF8("LineNumberTable"));
                $ByteVector.putInt(this.I.b + 2).putShort(this.H);
                $ByteVector.putByteArray(this.I.a, 0, this.I.b);
            }
            if (this.v != null) {
                $ByteVector.putShort(this.b.newUTF8(((this.b.b & 0xFFFF) >= 50) ? "StackMapTable" : "StackMap"));
                $ByteVector.putInt(this.v.b + 2).putShort(this.u);
                $ByteVector.putByteArray(this.v.a, 0, this.v.b);
            }
            if (this.J != null) {
                this.J.a(this.b, this.r.a, this.r.b, this.t, this.s, $ByteVector);
            }
        }
        if (this.j > 0) {
            $ByteVector.putShort(this.b.newUTF8("Exceptions")).putInt(2 * this.j + 2);
            $ByteVector.putShort(this.j);
            for (int i = 0; i < this.j; ++i) {
                $ByteVector.putShort(this.k[i]);
            }
        }
        if ((this.c & 0x1000) != 0x0 && (this.b.b & 0xFFFF) < 49) {
            $ByteVector.putShort(this.b.newUTF8("Synthetic")).putInt(0);
        }
        if ((this.c & 0x20000) != 0x0) {
            $ByteVector.putShort(this.b.newUTF8("Deprecated")).putInt(0);
        }
        if (this.g != null) {
            $ByteVector.putShort(this.b.newUTF8("Signature")).putInt(2).putShort(this.b.newUTF8(this.g));
        }
        if (this.l != null) {
            $ByteVector.putShort(this.b.newUTF8("AnnotationDefault"));
            $ByteVector.putInt(this.l.b);
            $ByteVector.putByteArray(this.l.a, 0, this.l.b);
        }
        if (this.m != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeVisibleAnnotations"));
            this.m.a($ByteVector);
        }
        if (this.n != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeInvisibleAnnotations"));
            this.n.a($ByteVector);
        }
        if (this.o != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeVisibleParameterAnnotations"));
            $AnnotationWriter.a(this.o, this.S, $ByteVector);
        }
        if (this.p != null) {
            $ByteVector.putShort(this.b.newUTF8("RuntimeInvisibleParameterAnnotations"));
            $AnnotationWriter.a(this.p, this.S, $ByteVector);
        }
        if (this.q != null) {
            this.q.a(this.b, null, 0, -1, -1, $ByteVector);
        }
    }
    
    private void d() {
        final byte[] a = this.r.a;
        int[] array = new int[0];
        int[] array2 = new int[0];
        final boolean[] array3 = new boolean[this.r.b];
        int i = 3;
        do {
            if (i == 3) {
                i = 2;
            }
            int j = 0;
            while (j < a.length) {
                int n = a[j] & 0xFF;
                int n2 = 0;
                switch ($ClassWriter.a[n]) {
                    case 0:
                    case 4: {
                        ++j;
                        break;
                    }
                    case 8: {
                        int n3;
                        if (n > 201) {
                            n = ((n < 218) ? (n - 49) : (n - 20));
                            n3 = j + c(a, j + 1);
                        }
                        else {
                            n3 = j + b(a, j + 1);
                        }
                        final int a2 = a(array, array2, j, n3);
                        if ((a2 < -32768 || a2 > 32767) && !array3[j]) {
                            if (n == 167 || n == 168) {
                                n2 = 2;
                            }
                            else {
                                n2 = 5;
                            }
                            array3[j] = true;
                        }
                        j += 3;
                        break;
                    }
                    case 9: {
                        j += 5;
                        break;
                    }
                    case 13: {
                        if (i == 1) {
                            n2 = -(a(array, array2, 0, j) & 0x3);
                        }
                        else if (!array3[j]) {
                            n2 = (j & 0x3);
                            array3[j] = true;
                        }
                        final int n4 = j + 4 - (j & 0x3);
                        j = n4 + (4 * (a(a, n4 + 8) - a(a, n4 + 4) + 1) + 12);
                        break;
                    }
                    case 14: {
                        if (i == 1) {
                            n2 = -(a(array, array2, 0, j) & 0x3);
                        }
                        else if (!array3[j]) {
                            n2 = (j & 0x3);
                            array3[j] = true;
                        }
                        final int n5 = j + 4 - (j & 0x3);
                        j = n5 + (8 * a(a, n5 + 4) + 8);
                        break;
                    }
                    case 16: {
                        if ((a[j + 1] & 0xFF) == 0x84) {
                            j += 6;
                            break;
                        }
                        j += 4;
                        break;
                    }
                    case 1:
                    case 3:
                    case 10: {
                        j += 2;
                        break;
                    }
                    case 2:
                    case 5:
                    case 6:
                    case 11:
                    case 12: {
                        j += 3;
                        break;
                    }
                    case 7: {
                        j += 5;
                        break;
                    }
                    default: {
                        j += 4;
                        break;
                    }
                }
                if (n2 != 0) {
                    final int[] array4 = new int[array.length + 1];
                    final int[] array5 = new int[array2.length + 1];
                    System.arraycopy(array, 0, array4, 0, array.length);
                    System.arraycopy(array2, 0, array5, 0, array2.length);
                    array4[array.length] = j;
                    array5[array2.length] = n2;
                    array = array4;
                    array2 = array5;
                    if (n2 <= 0) {
                        continue;
                    }
                    i = 3;
                }
            }
            if (i < 3) {
                --i;
            }
        } while (i != 0);
        final $ByteVector r = new $ByteVector(this.r.b);
        int k = 0;
        while (k < this.r.b) {
            int n6 = a[k] & 0xFF;
            switch ($ClassWriter.a[n6]) {
                case 0:
                case 4: {
                    r.putByte(n6);
                    ++k;
                    continue;
                }
                case 8: {
                    int n7;
                    if (n6 > 201) {
                        n6 = ((n6 < 218) ? (n6 - 49) : (n6 - 20));
                        n7 = k + c(a, k + 1);
                    }
                    else {
                        n7 = k + b(a, k + 1);
                    }
                    int a3 = a(array, array2, k, n7);
                    if (array3[k]) {
                        if (n6 == 167) {
                            r.putByte(200);
                        }
                        else if (n6 == 168) {
                            r.putByte(201);
                        }
                        else {
                            r.putByte((n6 <= 166) ? ((n6 + 1 ^ 0x1) - 1) : (n6 ^ 0x1));
                            r.putShort(8);
                            r.putByte(200);
                            a3 -= 3;
                        }
                        r.putInt(a3);
                    }
                    else {
                        r.putByte(n6);
                        r.putShort(a3);
                    }
                    k += 3;
                    continue;
                }
                case 9: {
                    final int a4 = a(array, array2, k, k + a(a, k + 1));
                    r.putByte(n6);
                    r.putInt(a4);
                    k += 5;
                    continue;
                }
                case 13: {
                    final short n8 = (short)k;
                    k = k + 4 - (n8 & 0x3);
                    r.putByte(170);
                    final $ByteVector $ByteVector = r;
                    $ByteVector.b += (4 - r.b % 4) % 4;
                    final int n9 = n8 + a(a, k);
                    k += 4;
                    r.putInt(a(array, array2, n8, n9));
                    final int a5 = a(a, k);
                    k += 4;
                    r.putInt(a5);
                    int l = a(a, k) - a5 + 1;
                    k += 4;
                    r.putInt(a(a, k - 4));
                    while (l > 0) {
                        final int n10 = n8 + a(a, k);
                        k += 4;
                        r.putInt(a(array, array2, n8, n10));
                        --l;
                    }
                    continue;
                }
                case 14: {
                    final short n11 = (short)k;
                    k = k + 4 - (n11 & 0x3);
                    r.putByte(171);
                    final $ByteVector $ByteVector2 = r;
                    $ByteVector2.b += (4 - r.b % 4) % 4;
                    final int n12 = n11 + a(a, k);
                    k += 4;
                    r.putInt(a(array, array2, n11, n12));
                    int a6 = a(a, k);
                    k += 4;
                    r.putInt(a6);
                    while (a6 > 0) {
                        r.putInt(a(a, k));
                        k += 4;
                        final int n13 = n11 + a(a, k);
                        k += 4;
                        r.putInt(a(array, array2, n11, n13));
                        --a6;
                    }
                    continue;
                }
                case 16: {
                    if ((a[k + 1] & 0xFF) == 0x84) {
                        r.putByteArray(a, k, 6);
                        k += 6;
                        continue;
                    }
                    r.putByteArray(a, k, 4);
                    k += 4;
                    continue;
                }
                case 1:
                case 3:
                case 10: {
                    r.putByteArray(a, k, 2);
                    k += 2;
                    continue;
                }
                case 2:
                case 5:
                case 6:
                case 11:
                case 12: {
                    r.putByteArray(a, k, 3);
                    k += 3;
                    continue;
                }
                case 7: {
                    r.putByteArray(a, k, 5);
                    k += 5;
                    continue;
                }
                default: {
                    r.putByteArray(a, k, 4);
                    k += 4;
                    continue;
                }
            }
        }
        if (this.u > 0) {
            if (this.M == 0) {
                this.u = 0;
                this.v = null;
                this.x = null;
                this.z = null;
                final $Frame $Frame = new $Frame();
                $Frame.b = this.N;
                $Frame.a(this.b, this.c, $Type.getArgumentTypes(this.f), this.t);
                this.b($Frame);
                for ($Label $Label = this.N; $Label != null; $Label = $Label.i) {
                    final int n14 = $Label.c - 3;
                    if (($Label.a & 0x20) != 0x0 || (n14 >= 0 && array3[n14])) {
                        a(array, array2, $Label);
                        this.b($Label.h);
                    }
                }
            }
            else {
                this.b.I = true;
            }
        }
        for ($Handler $Handler = this.B; $Handler != null; $Handler = $Handler.f) {
            a(array, array2, $Handler.a);
            a(array, array2, $Handler.b);
            a(array, array2, $Handler.c);
        }
        for (int n15 = 0; n15 < 2; ++n15) {
            final $ByteVector $ByteVector3 = (n15 == 0) ? this.E : this.G;
            if ($ByteVector3 != null) {
                final byte[] a7 = $ByteVector3.a;
                for (int n16 = 0; n16 < $ByteVector3.b; n16 += 10) {
                    final int c = c(a7, n16);
                    final int a8 = a(array, array2, 0, c);
                    a(a7, n16, a8);
                    a(a7, n16 + 2, a(array, array2, 0, c + c(a7, n16 + 2)) - a8);
                }
            }
        }
        if (this.I != null) {
            final byte[] a9 = this.I.a;
            for (int n17 = 0; n17 < this.I.b; n17 += 4) {
                a(a9, n17, a(array, array2, 0, c(a9, n17)));
            }
        }
        for ($Attribute $Attribute = this.J; $Attribute != null; $Attribute = $Attribute.a) {
            final $Label[] labels = $Attribute.getLabels();
            if (labels != null) {
                for (int n18 = labels.length - 1; n18 >= 0; --n18) {
                    a(array, array2, labels[n18]);
                }
            }
        }
        this.r = r;
    }
    
    static int c(final byte[] array, final int n) {
        return (array[n] & 0xFF) << 8 | (array[n + 1] & 0xFF);
    }
    
    static short b(final byte[] array, final int n) {
        return (short)((array[n] & 0xFF) << 8 | (array[n + 1] & 0xFF));
    }
    
    static int a(final byte[] array, final int n) {
        return (array[n] & 0xFF) << 24 | (array[n + 1] & 0xFF) << 16 | (array[n + 2] & 0xFF) << 8 | (array[n + 3] & 0xFF);
    }
    
    static void a(final byte[] array, final int n, final int n2) {
        array[n] = (byte)(n2 >>> 8);
        array[n + 1] = (byte)n2;
    }
    
    static int a(final int[] array, final int[] array2, final int n, final int n2) {
        int n3 = n2 - n;
        for (int i = 0; i < array.length; ++i) {
            if (n < array[i] && array[i] <= n2) {
                n3 += array2[i];
            }
            else if (n2 < array[i] && array[i] <= n) {
                n3 -= array2[i];
            }
        }
        return n3;
    }
    
    static void a(final int[] array, final int[] array2, final $Label $Label) {
        if (($Label.a & 0x4) == 0x0) {
            $Label.c = a(array, array2, 0, $Label.c);
            $Label.a |= 0x4;
        }
    }
}
