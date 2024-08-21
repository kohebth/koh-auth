package koh.service.auth.secure;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class Jwt {
    private static final long JWT_ACCESS_TIMEOUT = 1000 * 60 * 5L; // 5 minutes
    private static final long JWT_REFRESH_TIMEOUT = 1000 * 60 * 60 * 24 * 7L; // 7 day
    private static final Logger log = LoggerFactory.getLogger(Jwt.class);

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public Jwt(String publicKeyPath)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        this.publicKey = loadPublicKey(publicKeyPath);
        this.privateKey = null;
    }

    public Jwt(String privateKeyPath, String publicKeyPath)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        this.privateKey = loadPrivateKey(privateKeyPath);
        this.publicKey = loadPublicKey(publicKeyPath);
    }

    public String generate(String user, Long timeout) {
        try {
            return Jwts
                    .builder()
                    .setSubject(user)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + timeout))
                    .signWith(SignatureAlgorithm.RS256, this.privateKey)
                    .compact();
        } catch (Exception e) {
            throw new JwtException(e.getMessage(), e);
        }
    }

    public String generateAccess(String user) {
        return generate(user, JWT_ACCESS_TIMEOUT);
    }

    public String generateRefresh(String user) {
        return generate(user, JWT_REFRESH_TIMEOUT);
    }

    public Claims verify(String token) {
        try {
            return Jwts.parser().setSigningKey(this.publicKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new JwtException(e.getMessage(), e);
        }
    }

    public static PrivateKey loadPrivateKey(String path)
            throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
        byte[] keyBytes = loadKey(path);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);

    }

    public static PublicKey loadPublicKey(String path)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        byte[] keyBytes = loadKey(path);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    private static byte[] loadKey(String path)
            throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
        lines.removeIf(s -> s.contains("----"));
        String pem = String.join("", lines);
        return Base64.getDecoder().decode(pem.getBytes());
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
