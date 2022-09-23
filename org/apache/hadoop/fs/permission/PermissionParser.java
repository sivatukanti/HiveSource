// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class PermissionParser
{
    protected boolean symbolic;
    protected short userMode;
    protected short groupMode;
    protected short othersMode;
    protected short stickyMode;
    protected char userType;
    protected char groupType;
    protected char othersType;
    protected char stickyBitType;
    
    public PermissionParser(final String modeStr, final Pattern symbolic, final Pattern octal) throws IllegalArgumentException {
        this.symbolic = false;
        this.userType = '+';
        this.groupType = '+';
        this.othersType = '+';
        this.stickyBitType = '+';
        Matcher matcher = null;
        if ((matcher = symbolic.matcher(modeStr)).find()) {
            this.applyNormalPattern(modeStr, matcher);
        }
        else {
            if (!(matcher = octal.matcher(modeStr)).matches()) {
                throw new IllegalArgumentException(modeStr);
            }
            this.applyOctalPattern(matcher);
        }
    }
    
    private void applyNormalPattern(final String modeStr, final Matcher matcher) {
        boolean commaSeparated = false;
        for (int i = 0; i < 1 || matcher.end() < modeStr.length(); ++i) {
            if (i > 0 && (!commaSeparated || !matcher.find())) {
                throw new IllegalArgumentException(modeStr);
            }
            final String str = matcher.group(2);
            final char type = str.charAt(str.length() - 1);
            boolean stickyBit;
            boolean others;
            boolean user;
            boolean group = user = (others = (stickyBit = false));
            for (final char c : matcher.group(1).toCharArray()) {
                switch (c) {
                    case 'u': {
                        user = true;
                        break;
                    }
                    case 'g': {
                        group = true;
                        break;
                    }
                    case 'o': {
                        others = true;
                        break;
                    }
                    case 'a': {
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unexpected");
                    }
                }
            }
            if (!user && !group && !others) {
                group = (user = (others = true));
            }
            short mode = 0;
            for (final char c2 : matcher.group(3).toCharArray()) {
                switch (c2) {
                    case 'r': {
                        mode |= 0x4;
                        break;
                    }
                    case 'w': {
                        mode |= 0x2;
                        break;
                    }
                    case 'x': {
                        mode |= 0x1;
                        break;
                    }
                    case 'X': {
                        mode |= 0x8;
                        break;
                    }
                    case 't': {
                        stickyBit = true;
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unexpected");
                    }
                }
            }
            if (user) {
                this.userMode = mode;
                this.userType = type;
            }
            if (group) {
                this.groupMode = mode;
                this.groupType = type;
            }
            if (others) {
                this.othersMode = mode;
                this.othersType = type;
                this.stickyMode = (short)(stickyBit ? 1 : 0);
                this.stickyBitType = type;
            }
            commaSeparated = matcher.group(4).contains(",");
        }
        this.symbolic = true;
    }
    
    private void applyOctalPattern(final Matcher matcher) {
        final char typeApply = '=';
        this.stickyBitType = '=';
        this.userType = '=';
        this.groupType = '=';
        this.othersType = '=';
        final String sb = matcher.group(1);
        if (!sb.isEmpty()) {
            this.stickyMode = Short.valueOf(sb.substring(0, 1));
        }
        else {
            this.stickyMode = 0;
        }
        final String str = matcher.group(2);
        this.userMode = Short.valueOf(str.substring(0, 1));
        this.groupMode = Short.valueOf(str.substring(1, 2));
        this.othersMode = Short.valueOf(str.substring(2, 3));
    }
    
    protected int combineModes(final int existing, final boolean exeOk) {
        return this.combineModeSegments(this.stickyBitType, this.stickyMode, existing >>> 9, false) << 9 | this.combineModeSegments(this.userType, this.userMode, existing >>> 6 & 0x7, exeOk) << 6 | this.combineModeSegments(this.groupType, this.groupMode, existing >>> 3 & 0x7, exeOk) << 3 | this.combineModeSegments(this.othersType, this.othersMode, existing & 0x7, exeOk);
    }
    
    protected int combineModeSegments(final char type, int mode, final int existing, final boolean exeOk) {
        boolean capX = false;
        if ((mode & 0x8) != 0x0) {
            capX = true;
            mode &= 0xFFFFFFF7;
            mode |= 0x1;
        }
        switch (type) {
            case '+': {
                mode |= existing;
                break;
            }
            case '-': {
                mode = (~mode & existing);
                break;
            }
            case '=': {
                break;
            }
            default: {
                throw new RuntimeException("Unexpected");
            }
        }
        if (capX && !exeOk && (mode & 0x1) != 0x0 && (existing & 0x1) == 0x0) {
            mode &= 0xFFFFFFFE;
        }
        return mode;
    }
}
