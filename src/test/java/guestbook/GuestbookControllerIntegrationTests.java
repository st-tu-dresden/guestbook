/*
 * Copyright 2019-2026 the original author or authors.
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

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Integration tests for {@link GuestbookController}.
 *
 * @author Oliver Drotbohm
 * @author Oliver Geisel
 */
@SpringBootTest
@AutoConfigureMockMvc
class GuestbookControllerIntegrationTests {

	@Autowired MockMvcTester mvc;
	@Autowired GuestbookRepository repository;

	@Test // #58
	void redirectsToLoginPageForSecuredResource() {

		GuestbookEntry entry = repository.findAll().iterator().next();

		assertThat(mvc.perform(delete("/guestbook/{id}", entry.getId())))
				.hasStatus3xxRedirection()
				.hasHeader(HttpHeaders.LOCATION, "/login");
	}

	@Test // #58
	@WithMockUser(roles = "ADMIN")
	void allowsDeletingEntryForAdmin()  {

		long numberOfEntries = repository.count();
		GuestbookEntry entry = repository.findAll().iterator().next();

		assertThat(mvc.perform(delete("/guestbook/{id}", entry.getId())))
		.hasStatus3xxRedirection()
		.hasRedirectedUrl("/guestbook");

		assertThat(repository.count()).isEqualTo(numberOfEntries - 1);
	}

	@Test
	void deleteNotExistingEntryReturnsNotFound()  {
		assertThat(mvc.perform(delete("/guestbook/{id}", 9999L)
						.with(user("admin").roles("ADMIN")) // Alternative to @WithMockUser(roles = "ADMIN")
				)
		).hasStatus(HttpStatus.NOT_FOUND);
	}

	@Test
	@WithAnonymousUser
	void returnsToMainLoginWhenNotAuthorized() {

		long numberOfEntries = repository.count();
		GuestbookEntry entry = repository.findAll().iterator().next();

		assertThat(mvc.perform(delete("/guestbook/{id}", entry.getId())))
				.hasStatus3xxRedirection()
				.hasRedirectedUrl("/login");

		// no changes in the repository, because the entry was not deleted
		assertThat(repository.count()).isEqualTo(numberOfEntries);
	}


	@Test
	void everyoneCanCreateEntry()  {

		long numberOfEntries = repository.count();

		assertThat(mvc.perform(post("/guestbook")
						.param("text", "Test Message")
						.param("name", "Test Name")
				)
		)// All 3 assertions check if a redirect happens, but every assertion checks a different aspect of the redirect
		 .hasStatus3xxRedirection()
		 .hasRedirectedUrl("/guestbook")
		 .hasViewName("redirect:/guestbook");

		assertThat(repository.count()).isEqualTo(numberOfEntries + 1);
	}

	@Test
	void accessToEntryIsForbidden() {
		assertThat(mvc.perform(get("/guestbook/1")))

				.hasStatus(HttpStatus.METHOD_NOT_ALLOWED); // Only Delete is allowed, so GET should return 405
	}
}
