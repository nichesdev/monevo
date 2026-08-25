package br.com.nichesdev.userAuth.domain;

import br.com.nichesdev.userAuth.config.TokenProvider;
import br.com.nichesdev.userAuth.enums.RoleTypeEnum;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    @Value("${JWT_EXPIRATION:900000}")
    private Long expirationTime;

    public void register(UserRequestDto userRequestDto) throws BadRequestException {
        UserEntity user = userRepository.findByEmail(userRequestDto.getEmail())
                .orElse(null);
        if (user != null) {
            throw new BadRequestException("Usuário já cadastrado com este email.");
        }

        RolesEntity role = rolesRepository.findByName(RoleTypeEnum.ROLE_USER.name())
                .orElseGet(() -> rolesRepository.save(RolesEntity.builder()
                        .name(RoleTypeEnum.ROLE_USER.name())
                        .build()));

        UserEntity userEntity = userRepository.save(UserEntity.builder()
                .username(userRequestDto.getUsername())
                .email(userRequestDto.getEmail())
                .roles(Set.of(role))
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .build());
    }

    public TokenResponseDto login(LoginRequestDto loginRequestDto) throws BadRequestException {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
            String token = tokenProvider.gerarToken(authentication);

            return new TokenResponseDto(token, expirationTime);
        }catch (BadCredentialsException e){
            throw new BadRequestException("Invalid username or password.");
        }catch (Exception e){
            throw e;
        }
    }
}

