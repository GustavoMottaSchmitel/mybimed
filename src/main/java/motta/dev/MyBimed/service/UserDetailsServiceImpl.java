package motta.dev.MyBimed.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import motta.dev.MyBimed.model.UserModel;
import motta.dev.MyBimed.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Carregando usuário pelo email: {}", email);

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com email: {}", email);
                    return new UsernameNotFoundException("Credenciais inválidas");
                });

        log.debug("Usuário encontrado: {}", user.getEmail());

        return createUserDetails(user);
    }

    private UserDetails createUserDetails(UserModel user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getCargo().name())
        );

        return User.builder()
                .username(user.getEmail())
                .password(user.getSenha())
                .authorities(authorities)
                .accountLocked(!user.isAtivo())
                .disabled(!user.isAtivo())
                .accountExpired(false)
                .credentialsExpired(false)
                .build();
    }
}