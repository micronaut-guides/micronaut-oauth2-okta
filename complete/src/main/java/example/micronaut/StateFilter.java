package example.micronaut;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.security.filters.SecurityFilterOrderProvider;
import io.micronaut.security.utils.SecurityService;
import org.reactivestreams.Publisher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@Requires(beans = {
        SecurityService.class
})
@Filter("/**") // <1>
public class StateFilter extends OncePerRequestHttpServerFilter { // <2>

    protected final Integer order;

    private final static int ORDER_OFFSET = 100;

    public final static String STATE_COOKIENAME = "AUTHORIZATION_STATE";

    @Nonnull
    private final SecurityService securityService;

    public StateFilter(@Nonnull SecurityService securityService,
                       @Nullable SecurityFilterOrderProvider securityFilterOrderProvider) {
        this.securityService = securityService;
        this.order = securityFilterOrderProvider != null ?
                securityFilterOrderProvider.getOrder() + ORDER_OFFSET :
                ORDER_OFFSET; // <3>
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected Publisher<MutableHttpResponse<?>> doFilterOnce(HttpRequest<?> request, ServerFilterChain chain) {
        if(securityService.isAuthenticated()) {
            return Publishers.map(chain.proceed(request), response -> {
                if (request.getCookies().contains(STATE_COOKIENAME)) { // <4>
                    response.cookie(request.getCookies().get(STATE_COOKIENAME).maxAge(0));
                }
                return response;
            });

        } else {
            return Publishers.map(chain.proceed(request), response -> {
            if (!request.getCookies().contains(STATE_COOKIENAME)) { // <5>
                String state = generateState();
                Cookie cookie = Cookie.of(STATE_COOKIENAME, state);
                cookie.maxAge(Integer.MAX_VALUE);
                response.cookie(cookie);
            }
                return response;
            });
        }
    }

    private String generateState() {
        return UUID.randomUUID().toString();
    }
}
