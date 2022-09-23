// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.auth;

import java.io.ObjectStreamException;
import org.apache.http.util.CharsetUtils;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.HeaderElement;
import org.apache.http.message.HeaderValueParser;
import java.util.Locale;
import org.apache.http.message.ParserCursor;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.HttpRequest;
import org.apache.http.Consts;
import java.util.HashMap;
import org.apache.http.auth.ChallengeState;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.http.annotation.NotThreadSafe;
import java.io.Serializable;

@NotThreadSafe
public abstract class RFC2617Scheme extends AuthSchemeBase implements Serializable
{
    private static final long serialVersionUID = -2845454858205884623L;
    private final Map<String, String> params;
    private transient Charset credentialsCharset;
    
    @Deprecated
    public RFC2617Scheme(final ChallengeState challengeState) {
        super(challengeState);
        this.params = new HashMap<String, String>();
        this.credentialsCharset = Consts.ASCII;
    }
    
    public RFC2617Scheme(final Charset credentialsCharset) {
        this.params = new HashMap<String, String>();
        this.credentialsCharset = ((credentialsCharset != null) ? credentialsCharset : Consts.ASCII);
    }
    
    public RFC2617Scheme() {
        this(Consts.ASCII);
    }
    
    public Charset getCredentialsCharset() {
        return (this.credentialsCharset != null) ? this.credentialsCharset : Consts.ASCII;
    }
    
    String getCredentialsCharset(final HttpRequest request) {
        String charset = (String)request.getParams().getParameter("http.auth.credential-charset");
        if (charset == null) {
            charset = this.getCredentialsCharset().name();
        }
        return charset;
    }
    
    @Override
    protected void parseChallenge(final CharArrayBuffer buffer, final int pos, final int len) throws MalformedChallengeException {
        final HeaderValueParser parser = BasicHeaderValueParser.INSTANCE;
        final ParserCursor cursor = new ParserCursor(pos, buffer.length());
        final HeaderElement[] elements = parser.parseElements(buffer, cursor);
        this.params.clear();
        for (final HeaderElement element : elements) {
            this.params.put(element.getName().toLowerCase(Locale.ROOT), element.getValue());
        }
    }
    
    protected Map<String, String> getParameters() {
        return this.params;
    }
    
    @Override
    public String getParameter(final String name) {
        if (name == null) {
            return null;
        }
        return this.params.get(name.toLowerCase(Locale.ROOT));
    }
    
    @Override
    public String getRealm() {
        return this.getParameter("realm");
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(this.credentialsCharset.name());
        out.writeObject(this.challengeState);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.credentialsCharset = CharsetUtils.get(in.readUTF());
        if (this.credentialsCharset == null) {
            this.credentialsCharset = Consts.ASCII;
        }
        this.challengeState = (ChallengeState)in.readObject();
    }
    
    private void readObjectNoData() throws ObjectStreamException {
    }
}
