package sk.stu.fei.asos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.stu.fei.asos.domain.Account;
import sk.stu.fei.asos.domain.AccountRole;
import sk.stu.fei.asos.domain.InMemoryAccountDao;
import sk.stu.fei.asos.jwt.AuthenticatedUserDetails;
import sk.stu.fei.asos.jwt.JsonWebTokenUtils;

@RestController
@RequestMapping("/jwt/auth")
public class JwtAuthController {
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final InMemoryAccountDao dao;
    private final JsonWebTokenUtils tokenUtils;

    @Autowired
    public JwtAuthController(UserDetailsService userDetailsService, AuthenticationManager authenticationManager, InMemoryAccountDao dao, JsonWebTokenUtils tokenUtils) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.dao = dao;
        this.tokenUtils = tokenUtils;
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse attemptToSignIn(@RequestBody LoginRequest request) {
        authenticate(request);
        return createLoginResponse(request);
    }

    private void authenticate(LoginRequest request) {
        Account account = dao.findByUsername(request.username);

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username, request.password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private LoginResponse createLoginResponse(LoginRequest request) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username);
        final String token = tokenUtils.generateToken(userDetails);

        final LoginResponse result = new LoginResponse();
        result.token = token;
        final Account account = ((AuthenticatedUserDetails) userDetails).getAccount();
        result.account = account;
        result.role = account.getRole();

        return result;
    }

    private static class LoginResponse {
        private String token;
        private AccountRole role;
        private Account account;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public AccountRole getRole() {
            return role;
        }

        public void setRole(AccountRole role) {
            this.role = role;
        }

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }
    }

    private static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
