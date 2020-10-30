/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guestbook;

import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The core class to bootstrap our application. It triggers Spring Boot's auto-configuration, component scanning and
 * configuration properties scanning using the {@link SpringBootApplication} convenience annotation. At the same time,
 * this class acts as configuration class to configure additional components (see {@link #init(GuestbookRepository)}) that
 * the Spring container will take into account when bootstrapping.
 *
 * @author Paul Henke
 * @author Oliver Drotbohm
 */
@SpringBootApplication
public class Application {

	/**
	 * The main application method, bootstraps the Spring container.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Some initializing code to populate our database with some {@link GuestbookEntry}s. Beans of type
	 * {@link CommandLineRunner} will be executed on application startup which makes them a convenient way to run
	 * initialization code.
	 */
	@Bean
	CommandLineRunner init(GuestbookRepository guestbook) {

		return args -> {

			Stream.of( //
					new GuestbookEntry("H4xx0r", "first!!!", EntryColor.GREEN), //
					new GuestbookEntry("Arni", "Hasta la vista, baby", EntryColor.GREEN), //
					new GuestbookEntry("Duke Nukem", "It's time to kick ass and chew bubble gum. And I'm all out of gum.", EntryColor.BLUE), //
					new GuestbookEntry("Gump1337",
							"Mama always said life was like a box of chocolates. You never know what you're gonna get.", EntryColor.RED)) //
					.forEach(guestbook::save);
		};
	}

	/**
	 * This class customizes the web and web security configuration through callback methods provided by the
	 * {@link WebMvcConfigurer} interface.
	 */
	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	static class SecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

		/*
		 * (non-Javadoc)
		 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)
		 */
		@Override
		public void addViewControllers(ViewControllerRegistry registry) {

			// Route requests to /login to the login view (a default one provided by Spring Security)
			registry.addViewController("/login").setViewName("login");
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http.csrf().disable();

			// Allow all requests on the URI level, configure form login.
			http.authorizeRequests().anyRequest().permitAll() //
					.and().formLogin() //
					.and().logout().logoutSuccessUrl("/").clearAuthentication(true);
		}
	}
}
