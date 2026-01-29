package br.com.seplag.musicapi.infra.security;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import br.com.seplag.musicapi.infra.ratelimit.RateLimitFilter;
import br.com.seplag.musicapi.infra.ratelimit.RateLimitProperties;

@Configuration
@EnableConfigurationProperties({SecurityProperties.class, RateLimitProperties.class})
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService(org.springframework.core.env.Environment env, PasswordEncoder encoder) {
    var user = env.getProperty("demo-user.username", "admin");
    var pass = env.getProperty("demo-user.password", "admin123");

    return new InMemoryUserDetailsManager(
        User.withUsername(user)
            .password(encoder.encode(pass))
            .roles("USER")
            .build()
    );
  }

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService uds, PasswordEncoder encoder) {
    var provider = new DaoAuthenticationProvider(uds);
    provider.setPasswordEncoder(encoder);
    return new ProviderManager(provider);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
                                        SecurityProperties secProps,
                                        RateLimitProperties rateProps,
                                        JwtService jwtService,
                                        UserDetailsService uds) throws Exception {

    var jwtFilter = new JwtAuthFilter(jwtService, uds);
    var rateFilter = new RateLimitFilter(rateProps);

    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(cors -> cors.configurationSource(req -> {
          var cfg = new CorsConfiguration();
          cfg.setAllowedOrigins(List.of(secProps.allowedOrigins().split(",")));
          cfg.setAllowedMethods(List.of("GET","POST","PUT","OPTIONS"));
          cfg.setAllowedHeaders(List.of("*"));
          cfg.setAllowCredentials(true);
          return cfg;
        }))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/actuator/health/**").permitAll()
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/albums/**", "/api/v1/artists/**", "/api/v1/regionais/**").authenticated()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(rateFilter, JwtAuthFilter.class);

    return http.build();
  }
}
