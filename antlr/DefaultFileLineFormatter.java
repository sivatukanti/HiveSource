// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class DefaultFileLineFormatter extends FileLineFormatter
{
    public String getFormatString(final String str, final int i, final int j) {
        final StringBuffer sb = new StringBuffer();
        if (str != null) {
            sb.append(str + ":");
        }
        if (i != -1) {
            if (str == null) {
                sb.append("line ");
            }
            sb.append(i);
            if (j != -1) {
                sb.append(":" + j);
            }
            sb.append(":");
        }
        sb.append(" ");
        return sb.toString();
    }
}
