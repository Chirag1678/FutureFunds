package com.cg.futurefunds.utility;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.cg.futurefunds.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtility {
	@Autowired
	private UserRepository userRepository;
	
	private static final String SECRET_KEY = "futurefundsprojectsecretkeyextended";
	
	private Key generateKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}
	
	public String generateToken(String email, Long user_id) {
		return Jwts.builder()
				.setSubject(email)
				.claim("user_id", user_id)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
				.signWith(generateKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	public Claims extractClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(generateKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	public String extractEmail(String token) {
		Claims claims = extractClaims(token);
		
		return claims.getSubject();
	}
	
	public Long extractUserId(String token) {
		Claims claims = extractClaims(token);
		
		return claims.get("user_id", Long.class);
	}
	
	public Date extractExpiration(String token) {
		Claims claims = extractClaims(token);
		
		return claims.getExpiration();
	}
	
	public boolean isExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	public boolean validateToken(String token, UserDetails user) {
		String email = extractEmail(token);
		
		return (email.equals(user.getUsername()) && !isExpired(token));
	}
}
