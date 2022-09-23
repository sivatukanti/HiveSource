// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;

public class MismatchedCharException extends RecognitionException
{
    public static final int CHAR = 1;
    public static final int NOT_CHAR = 2;
    public static final int RANGE = 3;
    public static final int NOT_RANGE = 4;
    public static final int SET = 5;
    public static final int NOT_SET = 6;
    public int mismatchType;
    public int foundChar;
    public int expecting;
    public int upper;
    public BitSet set;
    public CharScanner scanner;
    
    public MismatchedCharException() {
        super("Mismatched char");
    }
    
    public MismatchedCharException(final char foundChar, final char expecting, final char upper, final boolean b, final CharScanner scanner) {
        super("Mismatched char", scanner.getFilename(), scanner.getLine(), scanner.getColumn());
        this.mismatchType = (b ? 4 : 3);
        this.foundChar = foundChar;
        this.expecting = expecting;
        this.upper = upper;
        this.scanner = scanner;
    }
    
    public MismatchedCharException(final char foundChar, final char expecting, final boolean b, final CharScanner scanner) {
        super("Mismatched char", scanner.getFilename(), scanner.getLine(), scanner.getColumn());
        this.mismatchType = (b ? 2 : 1);
        this.foundChar = foundChar;
        this.expecting = expecting;
        this.scanner = scanner;
    }
    
    public MismatchedCharException(final char foundChar, final BitSet set, final boolean b, final CharScanner scanner) {
        super("Mismatched char", scanner.getFilename(), scanner.getLine(), scanner.getColumn());
        this.mismatchType = (b ? 6 : 5);
        this.foundChar = foundChar;
        this.set = set;
        this.scanner = scanner;
    }
    
    public String getMessage() {
        final StringBuffer sb = new StringBuffer();
        switch (this.mismatchType) {
            case 1: {
                sb.append("expecting ");
                this.appendCharName(sb, this.expecting);
                sb.append(", found ");
                this.appendCharName(sb, this.foundChar);
                break;
            }
            case 2: {
                sb.append("expecting anything but '");
                this.appendCharName(sb, this.expecting);
                sb.append("'; got it anyway");
                break;
            }
            case 3:
            case 4: {
                sb.append("expecting token ");
                if (this.mismatchType == 4) {
                    sb.append("NOT ");
                }
                sb.append("in range: ");
                this.appendCharName(sb, this.expecting);
                sb.append("..");
                this.appendCharName(sb, this.upper);
                sb.append(", found ");
                this.appendCharName(sb, this.foundChar);
                break;
            }
            case 5:
            case 6: {
                sb.append("expecting " + ((this.mismatchType == 6) ? "NOT " : "") + "one of (");
                final int[] array = this.set.toArray();
                for (int i = 0; i < array.length; ++i) {
                    this.appendCharName(sb, array[i]);
                }
                sb.append("), found ");
                this.appendCharName(sb, this.foundChar);
                break;
            }
            default: {
                sb.append(super.getMessage());
                break;
            }
        }
        return sb.toString();
    }
    
    private void appendCharName(final StringBuffer sb, final int n) {
        switch (n) {
            case 65535: {
                sb.append("'<EOF>'");
                break;
            }
            case 10: {
                sb.append("'\\n'");
                break;
            }
            case 13: {
                sb.append("'\\r'");
                break;
            }
            case 9: {
                sb.append("'\\t'");
                break;
            }
            default: {
                sb.append('\'');
                sb.append((char)n);
                sb.append('\'');
                break;
            }
        }
    }
}
