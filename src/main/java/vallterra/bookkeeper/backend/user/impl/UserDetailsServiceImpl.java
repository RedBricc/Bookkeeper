package vallterra.bookkeeper.backend.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vallterra.bookkeeper.backend.role.BookkeeperUserRoleRepository;
import vallterra.bookkeeper.backend.user.BookkeeperUserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final BookkeeperUserRepository bookkeeperUserRepository;
    private final BookkeeperUserRoleRepository bookkeeperUserRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = bookkeeperUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var roles = bookkeeperUserRoleRepository.getRoles(user.getId());

        return User.builder()
                .username(username)
                .password(user.getPassword())
                .roles(roles.toArray(new String[0]))
                .build();
    }
}
