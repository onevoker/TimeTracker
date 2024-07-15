package com.onevoker.timetracker.security.services.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.onevoker.timetracker.configs.SecurityPropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtIssuerService {
    private final SecurityPropertiesConfig securityPropertiesConfig;

    public String generateToken(int userId, String username, List<String> roles) {
        var jwtProps = securityPropertiesConfig.jwt();
        var claimNames = securityPropertiesConfig.claimNames();

        Algorithm algorithm = Algorithm.HMAC256(jwtProps.secretKey());
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProps.tokenLifetime().toMillis());

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim(claimNames.usernameClaimName(), username)
                .withClaim(claimNames.authoritiesClaimName(), roles)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(algorithm);
    }
}
