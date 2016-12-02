package sk.stu.fei.asos.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import sk.stu.fei.asos.AuthenticatedUserDetailsService;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    static final String RESOURCE_ID = "asos";

    private final TokenStore tokenStore;
    private final AuthorizationCodeServices authorizationCodeServices;
    private final AuthenticationManager authenticationManager;
    private final AuthenticatedUserDetailsService userDetailsService;

    @Autowired
    public AuthorizationServerConfiguration(TokenStore tokenStore, AuthorizationCodeServices authorizationCodeServices, AuthenticationManager authenticationManager, AuthenticatedUserDetailsService userDetailsService) {
        this.tokenStore = tokenStore;
        this.authorizationCodeServices = authorizationCodeServices;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .authorizationCodeServices(authorizationCodeServices)
                .tokenStore(tokenStore)
                .userDetailsService(userDetailsService);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // @formatter:off
        clients.inMemory()
                .withClient("trusted-client")
                    .resourceIds(RESOURCE_ID)
                    .authorizedGrantTypes("password", "refresh_token")
                    .authorities("ROLE_ADMIN", "ROLE_USER", "ROLE_PRIVILEGEDUSER")
                    .accessTokenValiditySeconds(600)
                    .refreshTokenValiditySeconds(3600)
                    .scopes("create", "read", "update", "delete")
                    .secret("asos")
                .and()
                .withClient("public-client")
                    .resourceIds(RESOURCE_ID)
                    .authorizedGrantTypes("client_credentials")
                    .authorities("ROLE_USER")
                    .accessTokenValiditySeconds(600)
                    .secret("asos")
                    .scopes("read");
        // @formatter:on
    }
}
