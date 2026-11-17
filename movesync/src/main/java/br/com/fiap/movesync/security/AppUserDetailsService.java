package br.com.fiap.movesync.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Value("${app.security.admin.username}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Value("${app.security.operador.username}")
    private String operadorUsername;

    @Value("${app.security.operador.password}")
    private String operadorPassword;

    private final PasswordEncoder passwordEncoder;

    public AppUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equals(adminUsername)) {
            return User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles("ADMIN")
                    .build();
        }

        if (username.equals(operadorUsername)) {
            return User.builder()
                    .username(operadorUsername)
                    .password(passwordEncoder.encode(operadorPassword))
                    .roles("OPERADOR")
                    .build();
        }

        throw new UsernameNotFoundException("Usuário não encontrado.");
    }
}