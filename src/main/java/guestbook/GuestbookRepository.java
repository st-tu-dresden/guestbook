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

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

/**
 * A repository to manage {@link GuestbookEntry} instances. The methods are dynamically implemented by Spring Data JPA.
 *
 * @author Oliver Drotbohm
 * @see https://en.wikipedia.org/wiki/Domain-driven_design#Building_blocks
 * @see https://spring.io/projects/spring-data-jpa
 */
interface GuestbookRepository extends CrudRepository<GuestbookEntry, Long> {

	/**
	 * Returns all {@link GuestbookEntry}s created by the user with the given name, sorted by the given sort criteria.
	 *
	 * @param name the name to search for
	 * @param sort the given sorting criteria
	 * @return all {@link GuestbookEntry}s matching the query
	 */
	Streamable<GuestbookEntry> findByName(String name, Sort sort);
}
