package br.com.nichesdev.userAuth.domain;

import br.com.nichesdev.userAuth.enums.RoleTypeEnum;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserAuthService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

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



}
