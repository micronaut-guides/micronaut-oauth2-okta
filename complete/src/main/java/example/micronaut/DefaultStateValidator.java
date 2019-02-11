package example.micronaut;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.oauth2.openid.endpoints.authorization.StateValidator;
import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton // <1>
public class DefaultStateValidator implements StateValidator {

    @Override
    public boolean validate(@Nonnull HttpRequest<?> request, @Nonnull String state) {
        Cookie cookie = findCookie(request);
        if (cookie == null) {
            return false;
        }
        String serverState = cookie.getValue();
        return serverState.equals(state);
    }

    private Cookie findCookie(HttpRequest request) {
        return request.getCookies().get(StateFilter.STATE_COOKIENAME);
    }
}
