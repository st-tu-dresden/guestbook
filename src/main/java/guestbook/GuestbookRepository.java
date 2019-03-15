/*
 * Copyright 2014-2016 the original author or authors.
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
 * @author Oliver Gierke
 * @see http://en.wikipedia.org/wiki/Domain-driven_design#Building_blocks_of_DDD
 * @see http://projects.spring.io/spring-data-jpa/
 */
interface GuestbookRepository extends CrudRepository<GuestbookEntry, Long> {

	/**
	 * Returns all {@link GuestbookEntry}s created by the user with the given name, sorted by the given sort criteria.
	 *
	 * @param string
	 * @return
	 */
	Streamable<GuestbookEntry> findByName(String string, Sort sort);
}
