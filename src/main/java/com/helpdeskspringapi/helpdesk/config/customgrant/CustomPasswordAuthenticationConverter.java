package com.helpdeskspringapi.helpdesk.config.customgrant;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

public class CustomPasswordAuthenticationConverter implements AuthenticationConverter {

    @Nullable
    @Override
    public Authentication convert(HttpServletRequest request) {

        String grantType = request.getParameter("grant_type");
        if (!"password".equals(grantType)) {
            return null;
        }

        MultiValueMap<String, String> parameters = getParameters(request);


        String scope = parameters.getFirst("scope");
        if (StringUtils.hasText(scope) && parameters.get("scope").size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }


        String username = parameters.getFirst("username");
        if (!StringUtils.hasText(username) || parameters.get("username").size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }


        String password = parameters.getFirst("password");
        if (!StringUtils.hasText(password) || parameters.get("password").size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }

        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes = new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }

        Map<String, Object> additionalParameters = new HashMap<>();
        parameters.forEach((key, value) -> {
            if (!key.equals("grant_type") && !key.equals("scope")) {
                additionalParameters.put(key, value.get(0));
            }
        });

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        return new CustomPasswordAuthenticationToken(clientPrincipal, requestedScopes, additionalParameters);
    }

    private static MultiValueMap<String, String> getParameters(HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());

        parameterMap.forEach((key, values) -> {
            if (values != null && values.length > 0) {
                for (String value : values) {
                    parameters.add(key, value);
                }
            }
        });

        return parameters;
    }
}
