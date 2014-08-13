/*
 * Copyright 2014 the original author or authors.
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

import java.util.Optional;

import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

/**
 * A repository to manage {@link GuestbookEntry} instances. The methods are dynamically implemented by Spring Data JPA.
 * 
 * @author Oliver Gierke
 * @see http://en.wikipedia.org/wiki/Domain-driven_design#Building_blocks_of_DDD
 * @see http://projects.spring.io/spring-data-jpa/
 */
@Component("guestbookRepository")
public interface Guestbook extends Repository<GuestbookEntry, Long> {

	/**
	 * Deletes the {@link GuestbookEntry} with the given id.
	 * 
	 * @param id must not be {@literal null}.
	 */
	void delete(Long id);

	/**
	 * Saves the given {@link GuestbookEntry}.
	 * 
	 * @param entry the entry to save, must not be {@literal null}.
	 * @return the saved {@link GuestbookEntry}.
	 */
	GuestbookEntry save(GuestbookEntry entry);

	/**
	 * Returns the {@link GuestbookEntry} with the given id, if it exists, {@link Optional#empty()} otherwise.
	 * 
	 * @param id must not be {@literal null}.
	 * @return
	 */
	Optional<GuestbookEntry> findOne(Long id);

	/**
	 * Returns all {@link GuestbookEntry}s available.
	 * 
	 * @return
	 */
	Iterable<GuestbookEntry> findAll();

	/**
	 * Returns the number of {@link GuestbookEntry}s available.
	 * 
	 * @return
	 */
	int count();
}
