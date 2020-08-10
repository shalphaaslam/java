package com.shalpha.app.config.util;

import java.io.IOException;
import java.util.NoSuchElementException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shalpha.app.config.SecurityConfiguration;
import com.shalpha.app.security.oauth2.JwtGrantedAuthorityConverter;

import io.github.jhipster.config.JHipsterProperties;

/**
 * @author Shalpha Aslam
 * Class that dynamically fetches the tenant URI of the logged-in user and sets
 * the threadLocal.
 * 
 */
@Component
public class JwtFilter extends CorsFilter implements AuthenticationProvider {

	private final CorsConfigurationSource configSource;
	private CorsProcessor processor = new DefaultCorsProcessor();

	private final JHipsterProperties jHipsterProp;
	private final SecurityProblemSupport problemSupport;

	public JwtFilter(CorsConfigurationSource configSource, JHipsterProperties jHipsterProp,
			SecurityProblemSupport problemSupport) {
		super(configSource);
		this.configSource = configSource;
		this.jHipsterProp = jHipsterProp;
		this.problemSupport = problemSupport;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(request);
		boolean isValid = this.processor.processRequest(corsConfiguration, request, response);
		if (!isValid || CorsUtils.isPreFlightRequest(request)) {
			return;
		}

		if (request.getHeader("Authorization") != null) {
			String auth = request.getHeader("Authorization");

			String[] splitString = auth.split("\\.");
			if(splitString.length > 1) { 
				String base64EncodedBody = splitString[1];
	
				Base64 base64Url = new Base64(true);
				String body = new String(base64Url.decode(base64EncodedBody));
	
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				CustomJwtParser parsedClaim = mapper.readValue(body, CustomJwtParser.class);
				if (parsedClaim.getIss() != null) {
					TenantContext.setLiveTenantUri(parsedClaim.getIss());
				} else {
					throw new NoSuchElementException("Issuer URI not found in token passed!");
				}
			}
		} else {
			response.setHeader("WWW-Authenticate", "Basic realm=\"MyRealm\"");
			response.setStatus(401);
		}
		filterChain.doFilter(request, response);

	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		BearerTokenAuthenticationToken bearer = (BearerTokenAuthenticationToken) authentication;

		Jwt jwt;
		try {
			SecurityConfiguration securityConfiguration = new SecurityConfiguration(this, jHipsterProp, problemSupport);
			JwtDecoder jwtDecoderLocal = securityConfiguration.jwtDecoder();
			jwt = jwtDecoderLocal.decode(bearer.getToken());
		} catch (JwtException failed) {
			OAuth2Error invalidToken = invalidToken(failed.getMessage());
			throw new OAuth2AuthenticationException(invalidToken, invalidToken.getDescription(), failed);
		}
		AbstractAuthenticationToken token = this.grantedAuthoritiesExtractor().convert(jwt);
		token.setDetails(bearer.getDetails());
		return token;

	}

	Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthorityConverter());
		return jwtAuthenticationConverter;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private static final OAuth2Error DEFAULT_INVALID_TOKEN = invalidToken(
			"An error occurred while attempting to decode the Jwt: Invalid token");

	private static OAuth2Error invalidToken(String message) {
		try {
			return new BearerTokenError(BearerTokenErrorCodes.INVALID_TOKEN, HttpStatus.UNAUTHORIZED, message,
					"https://tools.ietf.org/html/rfc6750#section-3.1");
		} catch (IllegalArgumentException malformed) {
			// some third-party library error messages are not suitable for RFC 6750's error
			// message charset
			return DEFAULT_INVALID_TOKEN;
		}
	}

}
