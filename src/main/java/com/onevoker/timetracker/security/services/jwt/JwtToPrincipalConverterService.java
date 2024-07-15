package com.onevoker.timetracker.security.services.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.onevoker.timetracker.configs.SecurityPropertiesConfig;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtToPrincipalConverterService {
    private final SecurityPropertiesConfig.ClaimNames claimNames;

    public UserPrincipal convert(DecodedJWT jwt) {
        return UserPrincipal.builder()
                .userId(Integer.valueOf(jwt.getSubject()))
                .username(jwt.getClaim(claimNames.usernameClaimName()).asString())
                .authorities(extractAuthorities(jwt))
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuthorities(DecodedJWT jwt) {
        Claim claim = jwt.getClaim(claimNames.authoritiesClaimName());

        if (claim.isNull() || claim.isMissing()) {
            return List.of();
        }

        return claim.asList(SimpleGrantedAuthority.class);
    }
}
