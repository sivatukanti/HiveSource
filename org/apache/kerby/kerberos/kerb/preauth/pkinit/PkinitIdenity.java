// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.pkinit;

import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

public class PkinitIdenity
{
    public static void processIdentityOption(final IdentityOpts identityOpts, final String value) {
        IdentityType idType = IdentityType.NONE;
        String residual = null;
        if (value.contains(":")) {
            if (value.startsWith("FILE:")) {
                idType = IdentityType.FILE;
            }
            else if (value.startsWith("PKCS11:")) {
                idType = IdentityType.PKCS11;
            }
            else if (value.startsWith("PKCS12:")) {
                idType = IdentityType.PKCS12;
            }
            else if (value.startsWith("DIR:")) {
                idType = IdentityType.DIR;
            }
            else {
                if (!value.startsWith("ENV:")) {
                    throw new RuntimeException("Invalid Identity option format: " + value);
                }
                idType = IdentityType.ENVVAR;
            }
        }
        else {
            residual = value;
            idType = IdentityType.FILE;
        }
        identityOpts.idType = idType;
        switch (idType) {
            case ENVVAR: {
                processIdentityOption(identityOpts, System.getenv(residual));
                break;
            }
            case FILE: {
                parseFileOption(identityOpts, residual);
                break;
            }
            case PKCS11: {
                parsePkcs11Option(identityOpts, residual);
                break;
            }
            case PKCS12: {
                parsePkcs12Option(identityOpts, residual);
                break;
            }
            case DIR: {
                identityOpts.certFile = residual;
                break;
            }
        }
    }
    
    public static void parseFileOption(final IdentityOpts identityOpts, final String residual) {
        final String[] parts = residual.split(",");
        final String certName = parts[0];
        String keyName = null;
        if (parts.length > 1) {
            keyName = parts[1];
        }
        identityOpts.certFile = certName;
        identityOpts.keyFile = keyName;
    }
    
    public static void parsePkcs12Option(final IdentityOpts identityOpts, final String residual) {
        identityOpts.certFile = residual;
        identityOpts.keyFile = residual;
    }
    
    public static void parsePkcs11Option(final IdentityOpts identityOpts, final String residual) {
    }
    
    public static void loadCerts(final IdentityOpts identityOpts, final PrincipalName principal) {
        switch (identityOpts.idType) {
            case FILE: {
                loadCertsFromFile(identityOpts, principal);
                break;
            }
            case DIR: {
                loadCertsFromDir(identityOpts, principal);
                break;
            }
            case PKCS11: {
                loadCertsAsPkcs11(identityOpts, principal);
                break;
            }
            case PKCS12: {
                loadCertsAsPkcs12(identityOpts, principal);
                break;
            }
        }
    }
    
    private static void loadCertsAsPkcs12(final IdentityOpts identityOpts, final PrincipalName principal) {
    }
    
    private static void loadCertsAsPkcs11(final IdentityOpts identityOpts, final PrincipalName principal) {
    }
    
    private static void loadCertsFromDir(final IdentityOpts identityOpts, final PrincipalName principal) {
    }
    
    private static void loadCertsFromFile(final IdentityOpts identityOpts, final PrincipalName principal) {
    }
    
    public static void initialize(final IdentityOpts identityOpts, final PrincipalName principal) {
    }
}
