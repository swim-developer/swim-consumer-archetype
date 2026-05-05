#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain.model.command;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@RegisterForReflection
public record SubscriptionCommand(
        String topic,
        String queueName,
        String provider,
        String description
        // TODO: Add domain-specific subscription parameters
) {
    public String generateConfigHash() {
        String prov = provider != null ? provider : "";
        return sha256(prov + topic);
    }

    private static String sha256(String content) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(content.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
