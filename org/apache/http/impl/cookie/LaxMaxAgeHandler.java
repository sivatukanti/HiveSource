// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.cookie;

import org.apache.http.cookie.MalformedCookieException;
import java.util.regex.Matcher;
import java.util.Date;
import org.apache.http.util.TextUtils;
import org.apache.http.util.Args;
import org.apache.http.cookie.SetCookie;
import java.util.regex.Pattern;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.CommonCookieAttributeHandler;

@Immutable
public class LaxMaxAgeHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler
{
    private static final Pattern MAX_AGE_PATTERN;
    
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (TextUtils.isBlank(value)) {
            return;
        }
        final Matcher matcher = LaxMaxAgeHandler.MAX_AGE_PATTERN.matcher(value);
        if (matcher.matches()) {
            int age;
            try {
                age = Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                return;
            }
            final Date expiryDate = (age >= 0) ? new Date(System.currentTimeMillis() + age * 1000L) : new Date(Long.MIN_VALUE);
            cookie.setExpiryDate(expiryDate);
        }
    }
    
    @Override
    public String getAttributeName() {
        return "max-age";
    }
    
    static {
        MAX_AGE_PATTERN = Pattern.compile("^\\-?[0-9]+$");
    }
}
