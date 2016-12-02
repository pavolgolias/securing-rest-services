package sk.stu.fei.asos;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import sk.stu.fei.asos.domain.Account;

import java.util.ArrayList;
import java.util.List;

public class AuthenticatedUserDetails extends User {
    private final Account account;

    public AuthenticatedUserDetails(Account account) {
        super(account.getUsername(), account.getPassword(), createAuthorities(account));
        this.account = account;
    }

    private static List<GrantedAuthority> createAuthorities(Account account) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRole().name().toUpperCase()));
        return authorities;
    }

    public Account getAccount() {
        return account;
    }
}
