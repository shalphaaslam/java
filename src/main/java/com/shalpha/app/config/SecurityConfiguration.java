package com.shalpha.app.config;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import com.shalpha.app.config.util.JwtFilter;
import com.shalpha.app.config.util.TenantContext;
import com.shalpha.app.security.AuthoritiesConstants;
import com.shalpha.app.security.oauth2.AudienceValidator;
import com.shalpha.app.security.oauth2.JwtGrantedAuthorityConverter;

import io.github.jhipster.config.JHipsterProperties;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Value("${spring.security.oauth2.client.provider.oidc.issuer-uri}")
	private String issuerUri;

	private final JwtFilter jwtFilter;
	private final JHipsterProperties jHipsterProperties;
	private final SecurityProblemSupport problemSupport;

	public SecurityConfiguration(JwtFilter jwtFilter, JHipsterProperties jHipsterProperties,
			SecurityProblemSupport problemSupport) {
		this.jwtFilter = jwtFilter;
		this.problemSupport = problemSupport;
		this.jHipsterProperties = jHipsterProperties;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.csrf().disable().addFilterBefore(jwtFilter, CorsFilter.class)
				.exceptionHandling().authenticationEntryPoint(problemSupport)
				.accessDeniedHandler(problemSupport).and().headers()
				.contentSecurityPolicy(
						"default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
				.and().referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN).and()
				.featurePolicy(
						"geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
				.and().frameOptions().deny().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/api/auth-info").permitAll().antMatchers("/api/**").authenticated()
				.antMatchers("/management/health").permitAll().antMatchers("/management/info").permitAll()
				.antMatchers("/management/prometheus").permitAll().antMatchers("/management/**")
				.hasAuthority(AuthoritiesConstants.ADMIN).and().oauth2ResourceServer().jwt()
				.jwtAuthenticationConverter(authenticationConverter()).and().and().oauth2Client();
		// @formatter:on
	}

	Converter<Jwt, AbstractAuthenticationToken> authenticationConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthorityConverter());
		return jwtAuthenticationConverter;
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		try {
			final String tenantIssuerUri = (null != TenantContext.getLiveTenantUri()) ? TenantContext.getLiveTenantUri() : issuerUri;
			NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(tenantIssuerUri);

			OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(
					jHipsterProperties.getSecurity().getOauth2().getAudience());
			OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(tenantIssuerUri);
			OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer,
					audienceValidator);

			jwtDecoder.setJwtValidator(withAudience);
			return jwtDecoder;
		} catch (Exception exception) {
			throw new NoSuchElementException("Error while setting the tenant URI");
		}
	}
}
