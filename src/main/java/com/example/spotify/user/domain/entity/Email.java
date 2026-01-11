package com.example.spotify.user.domain.entity;



import org.springframework.web.server.NotAcceptableStatusException;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public static Email of(String email) {
        if (email == null || email.isEmpty()) {
            throw new NotAcceptableStatusException("Email não pode ser vazio");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new NotAcceptableStatusException("Formato de email inválido");
        }

        return new Email(email);
    }

    public boolean isValid() {
        return EMAIL_PATTERN.matcher(value).matches() && !value.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value.toLowerCase(), email.value.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
