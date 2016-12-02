package sk.stu.fei.asos.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sk.stu.fei.asos.domain.Account;

import java.util.Base64;
import java.util.Date;

@Component
public class JsonWebTokenUtils {
    public static final String tokenHeader = "Authorization";
    public static final String secret = "asos";
    public static final Long expiration = 604800L;

    public String getUsernameFromToken(String token) {
        if ( token == null || token.isEmpty() ) {
            return null;
        }

        String username;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            username = claims.getSubject();
        }
        catch ( Exception e ) {
            username = null;
        }
        return username;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            created = new Date((Long) claims.get("created"));
        }
        catch ( Exception e ) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            expiration = claims.getExpiration();
        }
        catch ( Exception e ) {
            expiration = null;
        }
        return expiration;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch ( Exception e ) {
            claims = null;
        }
        return claims;
    }

    private Date generateCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(this.generateCurrentDate());
    }

    public String generateToken(UserDetails userDetails) {
        final Claims claims = new DefaultClaims();
        claims.setSubject(userDetails.getUsername());
        claims.setIssuedAt(this.generateCurrentDate());
        if ( userDetails instanceof AuthenticatedUserDetails ) {
            final AuthenticatedUserDetails auth = (AuthenticatedUserDetails) userDetails;
            final Account account = auth.getAccount();
            claims.setId(Long.toString(account.getId()));
        }
        return this.generateToken(claims);
    }

    private String generateToken(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(this.generateExpirationDate())
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        if ( userDetails instanceof AuthenticatedUserDetails ) {
            final Account user = ((AuthenticatedUserDetails) userDetails).getAccount();
            final String username = this.getUsernameFromToken(token);
            return (username.equals(user.getUsername()) && !(this.isTokenExpired(token)));
        }
        else {
            throw new IllegalArgumentException("Expected object of type Account as user details.");
        }
    }
}

