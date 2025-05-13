package ca.sheridancollege.vonghil.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;


@Configuration
@EnableWebSecurity
public class SecurityConfig {


	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Bean
	public SpringSecurityDialect securityDialect() {
	    return new SpringSecurityDialect();
	}
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
		MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);


		return http.authorizeHttpRequests(authorize -> authorize
				// Admin paths
				.requestMatchers(mvc.pattern("/admin/**")).hasRole("ADMIN")
//				.requestMatchers(mvc.pattern("/admin/**")).permitAll()
				// User paths - add this section to handle your new user endpoints
				.requestMatchers(mvc.pattern("/user/**")).hasAnyRole("USER", "ADMIN")
				


				// Public content
				
				.requestMatchers(mvc.pattern("/")).permitAll()
				.requestMatchers(mvc.pattern("/courses")).permitAll()
				.requestMatchers(mvc.pattern("/js/**")).permitAll()
				.requestMatchers(mvc.pattern("/css/**")).permitAll()
				.requestMatchers(mvc.pattern("/images/**")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/signup")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/signup")).permitAll()
				.requestMatchers(mvc.pattern("/permission-denied")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
				
				// Deny anything not explicitly permitted
				.requestMatchers(mvc.pattern("/**")).denyAll()
			)
			.csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).disable())
			.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
			.formLogin(form -> form.loginPage("/login").permitAll().defaultSuccessUrl("/user/courses", true))
			.exceptionHandling(exception -> exception.accessDeniedPage("/permission-denied"))
			.logout(logout -> logout.permitAll())
			.build();
	}
//
}
