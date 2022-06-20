package com.fruits.watermelon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@SpringBootApplication
public class WatermelonApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatermelonApplication.class, args);
	}

	@EnableWebSecurity
	public class SecurityConfig<S extends Session>   {

		@Autowired
		private FindByIndexNameSessionRepository<S> sessionRepository;

		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http
            .authorizeRequests()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .and()
            .sessionManagement()
                .maximumSessions(3)
				.sessionRegistry(sessionRegistry())
				.expiredUrl("/login");
			return http.build();
		}

		@Bean
		public SpringSessionBackedSessionRegistry<S> sessionRegistry() {
			return new SpringSessionBackedSessionRegistry<>(sessionRepository);
		}
		
		@Bean
		public UserDetailsService userDetailsService() {
			UserDetails user = User.withDefaultPasswordEncoder()
					.username("user")
					.password("password")
					.roles("ADMIN")
					.build();
			return new InMemoryUserDetailsManager(user);
		}
		
	}

	@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 180, redisNamespace = "watermelon")
	public class RedisConfig {

		@Bean
		public JedisConnectionFactory connectionFactory() {
			RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
			configuration.setHostName("yourHost");
			configuration.setPassword("yourPassword");
			configuration.setUsername("yourUsername");
			//configuration.setPort(10811);
			return new JedisConnectionFactory(configuration);
		}

	}
}
