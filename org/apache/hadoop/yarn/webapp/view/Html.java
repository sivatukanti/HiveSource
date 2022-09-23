// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import java.util.regex.Pattern;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class Html
{
    static final Pattern validIdRe;
    
    public static boolean isValidId(final String id) {
        return Html.validIdRe.matcher(id).matches();
    }
    
    static {
        validIdRe = Pattern.compile("^[a-zA-Z_.0-9]+$");
    }
}
