package com.onevoker.timetracker.security.services.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.onevoker.timetracker.configs.SecurityPropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtDecoderService {
    private final SecurityPropertiesConfig.Jwt jwt;

    public DecodedJWT decode(String token) {
        return JWT.require(Algorithm.HMAC256(jwt.secretKey()))
                .build()
                .verify(token);
    }
}
