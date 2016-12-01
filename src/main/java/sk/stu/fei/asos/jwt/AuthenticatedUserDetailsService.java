package sk.stu.fei.asos.jwt;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import sk.stu.fei.asos.domain.Account;
import sk.stu.fei.asos.domain.InMemoryAccountDao;

@Component
public class AuthenticatedUserDetailsService implements UserDetailsService {

    private final InMemoryAccountDao dao;

    @Autowired
    public AuthenticatedUserDetailsService(InMemoryAccountDao dao) {
        this.dao = dao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws AuthenticationException {
        final Account account = this.dao.findByUsername(username);

        if ( account == null ) {
            throw new UsernameNotFoundException(String.format("No account found with email %s", username));
        }
        else {
            return new AuthenticatedUserDetails(account);
        }
    }

}
