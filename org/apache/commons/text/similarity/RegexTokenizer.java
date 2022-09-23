// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.text.similarity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.StringUtils;

class RegexTokenizer implements Tokenizer<CharSequence>
{
    @Override
    public CharSequence[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text", new Object[0]);
        final Pattern pattern = Pattern.compile("(\\w)+");
        final Matcher matcher = pattern.matcher(text.toString());
        final List<String> tokens = new ArrayList<String>();
        while (matcher.find()) {
            tokens.add(matcher.group(0));
        }
        return tokens.toArray(new String[0]);
    }
}
