package br.com.nichesdev.userAuth.domain;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monevo/auth")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserAuthService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody UserRequestDto userRequestDto) throws BadRequestException {
        userService.register(userRequestDto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponseDto login(@Valid @RequestBody LoginRequestDto loginRequestDto) throws BadRequestException {
        return userService.login(loginRequestDto);
    }
}
