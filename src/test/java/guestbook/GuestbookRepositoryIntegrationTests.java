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

import static org.assertj.core.api.Assertions.*;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

/**
 * Integration tests for {@link GuestbookRepository}.
 * <p>
 * Bootstraps the application using the {@link Application} configuration class. Enables transaction rollbacks after
 * test methods using the {@link Transactional} annotation.
 *
 * @author Oliver Drotbohm
 * @author Paul Henke
 */
@SpringBootTest
@Transactional
class GuestbookRepositoryIntegrationTests {

	@Autowired GuestbookRepository repository;

	@Test
	void persistsGuestbookEntry() {

		GuestbookEntry entry = repository.save(new GuestbookEntry("Yoda", "May the force be with you!"));

		assertThat(repository.findAll()).contains(entry);
	}

	@Test // #34
	void findsGuestbookEntryByAuthorName() {

		GuestbookEntry entry = repository.save(new GuestbookEntry("Yoda", "May the force be with you!"));

		assertThat(repository.findByName("Yoda", Sort.by("date"))).contains(entry);
	}
}
