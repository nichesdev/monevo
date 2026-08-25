package br.com.nichesdev.userAuth.domain;

public record TokenResponseDto(String token, long expiresIn) {
}
