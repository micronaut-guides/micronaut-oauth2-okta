package example.micronaut;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.handlers.ForbiddenRejectionUriProvider;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton // <1>
public class DefaultForbiddenRejectionUriProvider implements ForbiddenRejectionUriProvider { // <2>
    @Override
    public Optional<String> getForbiddenRedirectUri(HttpRequest<?> request) {
        return Optional.of("/denied");
    }
}
