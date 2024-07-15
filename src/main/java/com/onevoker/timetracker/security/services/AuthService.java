package com.onevoker.timetracker.security.services;

import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.repositories.RoleEntityRepository;
import com.onevoker.timetracker.domain.repositories.UserEntityRepository;
import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.dto.auth.AuthResponse;
import com.onevoker.timetracker.exceptions.DuplicateDataException;
import com.onevoker.timetracker.security.services.jwt.JwtIssuerService;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtIssuerService jwtIssuerService;
    private final AuthenticationManager authenticationManager;
    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ROLE_USER = "ROLE_User";
    private static final String USERNAME_IN_USE_MESSAGE = "This username in use, try another one, please";

    public AuthResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticateUser(authRequest.getUsername(), authRequest.getPassword());
        return generateAuthResponse(authentication);
    }

    /***
     * After registration, automatically log in the user
     * @throws DuplicateDataException
     *          If request have already existing username
     */
    public AuthResponse register(AuthRequest authRequest) {
        if (userEntityRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new DuplicateDataException(USERNAME_IN_USE_MESSAGE);
        }

        UserEntity user = new UserEntity();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRoleEntities(List.of(roleEntityRepository.findByName(ROLE_USER).orElseThrow()));

        userEntityRepository.save(user);

        Authentication authentication = authenticateUser(authRequest.getUsername(), authRequest.getPassword());
        return generateAuthResponse(authentication);
    }

    private Authentication authenticateUser(String username, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    private AuthResponse generateAuthResponse(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        String token = jwtIssuerService.generateToken(principal.getUserId(), principal.getUsername(), roles);
        return new AuthResponse(token);
    }
}
