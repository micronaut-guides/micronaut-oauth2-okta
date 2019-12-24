package example.micronaut;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.oauth2.endpoint.authorization.state.DefaultState;
import io.micronaut.security.oauth2.endpoint.authorization.state.JacksonStateSerDes;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.authorization.state.StateSerDes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.Base64;

@Replaces(JacksonStateSerDes.class)
@Singleton
public class JacksonStateSerDesReplacement implements StateSerDes {
    private static final Logger LOG = LoggerFactory.getLogger(JacksonStateSerDesReplacement.class);

    private final ObjectMapper objectMapper;

    /**
     * @param objectMapper To serialize/de-serialize the state
     */
    public JacksonStateSerDesReplacement(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public State deserialize(String base64State) {
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(base64State);
            String state = new String(decodedBytes);
            return objectMapper.readValue(state, DefaultState.class);
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Failed to deserialize the authorization request state", e);
            }
        }
        return null;
    }

    @Override
    public String serialize(State state) {
        try {
            String originalInput = objectMapper.writeValueAsString(state);
            return Base64.getEncoder().encodeToString(originalInput.getBytes());
        } catch (JsonProcessingException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Failed to serialize the authorization request state to JSON", e);
            }
        }
        return null;
    }
}
