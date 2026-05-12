package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    // Constructor Injection
    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // GET SIGNING KEY
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // GENERATE TOKEN (7 DAYS VALID)
    public String generateToken(Long id, String role) {

        return Jwts.builder()
                .setSubject(id.toString())        // store user ID as subject
                .claim("role", role)              // store role
                .setIssuedAt(new Date())          // now
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 days
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // EXTRACT IDENTIFIER (USER ID)
    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

            return Long.parseLong(claims.getSubject());

        } catch (Exception e) {
            return null;
        }
    }

    // EXTRACT EMAIL (FOR PATIENT)
    public String extractEmail(String token) {
        try {
            Long id = extractUserId(token);
            if (id == null) return null;

            return patientRepository.findById(id)
                    .map(p -> p.getEmail())
                    .orElse(null);

        } catch (Exception e) {
            return null;
        }
    }

    // VALIDATE TOKEN FOR USER TYPE
    public boolean validateToken(String token, String userType) {

        try {
            Long id = extractUserId(token);
            if (id == null) return false;

            return switch (userType.toLowerCase()) {
                case "admin" -> adminRepository.findById(id).isPresent();
                case "doctor" -> doctorRepository.findById(id).isPresent();
                case "patient" -> patientRepository.findById(id).isPresent();
                default -> false;
            };

        } catch (Exception e) {
            return false;
        }
    }
}
