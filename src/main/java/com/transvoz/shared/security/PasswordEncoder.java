package com.transvoz.shared.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/**
 * Wrapper utilitário para hash/validação de senhas com Argon2id.
 * Não registra bean; é usado apenas para chamadas estáticas quando necessário.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PasswordEncoder {

    private static final Argon2PasswordEncoder DELEGATE =
            Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public static String encode(String raw) {
        return DELEGATE.encode(raw);
    }

    public static boolean matches(String raw, String encoded) {
        return DELEGATE.matches(raw, encoded);
    }
}
