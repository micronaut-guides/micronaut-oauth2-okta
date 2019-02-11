package example.micronaut;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.oauth2.openid.endpoints.authorization.StateProvider;
import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton // <1>
public class DefaultStateProvider implements StateProvider {
    @Nonnull
    @Override
    public String generateState(@Nonnull HttpRequest<?> request) {
        Cookie cookie = request.getCookies().get(StateFilter.STATE_COOKIENAME);
        if (cookie != null) {
            return cookie.getValue();
        }
        throw new RuntimeException("Authorization code state parameter could not be retrieved from cookie");
    }
}
