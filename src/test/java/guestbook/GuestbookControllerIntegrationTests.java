/*
 * Copyright 2019 the original author or authors.
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
import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for {@link GuestbookController}.
 *
 * @author Oliver Drotbohm
 */
@SpringBootTest
@AutoConfigureMockMvc
public class GuestbookControllerIntegrationTests {

	@Autowired MockMvc mvc;
	@Autowired GuestbookRepository repository;

	@Test // #58
	void redirectsToLoginPageForSecuredResource() throws Exception {

		GuestbookEntry entry = repository.findAll().iterator().next();

		mvc.perform(delete("/guestbook/{id}", entry.getId())) //
				.andExpect(status().is3xxRedirection()) //
				.andExpect(header().string("Location", endsWith("/login")));
	}

	@Test // #58
	@WithMockUser(roles = "ADMIN")
	void returnsModelAndViewForSecuredUriAfterAuthentication() throws Exception {

		long numberOfEntries = repository.count();
		GuestbookEntry entry = repository.findAll().iterator().next();

		mvc.perform(delete("/guestbook/{id}", entry.getId())) //
				.andExpect(status().is3xxRedirection()) //
				.andExpect(view().name("redirect:/guestbook"));

		assertThat(repository.count()).isEqualTo(numberOfEntries - 1);
	}
}
