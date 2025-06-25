package com.calendar.backend.auth.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
@Slf4j
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken>
{
    @NotEmpty
    private String clientId;

    @Override
    public AbstractAuthenticationToken convert(Jwt source)
    {
        return new JwtAuthenticationToken(source, Stream.concat(new JwtGrantedAuthoritiesConverter().convert(source)
                .stream(), extractResourceRoles(source).stream())
                .collect(toSet()));
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt)
    {
        var resourceAccess = new HashMap<>(jwt.getClaim("resource_access"));
        var resourceRoles = new ArrayList<>();

        if (resourceAccess.containsKey(clientId))
        {
            var resource = (Map<String, List<String>>) resourceAccess.get(clientId);
            resourceRoles.addAll(resource.get("roles"));
        }
        return resourceRoles.isEmpty() ? emptySet() : resourceRoles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(toSet());
    }
}