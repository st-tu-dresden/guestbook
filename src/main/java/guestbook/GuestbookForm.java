/*
 * Copyright 2015-2019 the original author or authors.
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

import jakarta.validation.constraints.NotBlank;

/**
 * Type to bind request payloads and make them available in the controller. In contrast to {@link GuestbookEntry} it is
 * particularly designed to also be able to capture invalid input, so that the raw form data can be bound and validated
 * against business constraints using code and / or annotations.
 * <p>
 * Note how the fields are annotated with the {@link NotBlank} annotation, which tells Spring how to validate the
 * values.
 *
 * @author Oliver Drotbohm
 * @see GuestbookController#addEntry(GuestbookForm, org.springframework.validation.Errors, org.springframework.ui.Model)
 */
class GuestbookForm {

	private final @NotBlank String name;
	private final @NotBlank String text;

	/**
	 * Creates a new {@link GuestbookForm} with the given name and text. Spring Framework will use this constructor to
	 * bind the values provided in the web form described in {@code src/main/resources/templates/guestbook.html}, in
	 * particular the {@code name} and {@code text} fields as they correspond to the parameter names of the constructor.
	 * The constructor needs to be public so that Spring will actually consider it for form data binding until
	 * {@link https://github.com/spring-projects/spring-framework/issues/22600} is resolved.
	 *
	 * @param name the value to bind to {@code name}
	 * @param text the value to bind to {@code text}
	 */
	public GuestbookForm(String name, String text) {

		this.name = name;
		this.text = text;
	}

	/**
	 * Returns the value bound to the {@code name} attribute of the request. Needs to be public so that Spring will
	 * actually consider it for form data binding until
	 * {@link https://github.com/spring-projects/spring-framework/issues/22600} is resolved.
	 *
	 * @return the value bound to {@code name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value bound to the {@code text} attribute of the request. Needs to be public so that Spring will
	 * actually consider it for form data binding until
	 * {@link https://github.com/spring-projects/spring-framework/issues/22600} is resolved.
	 *
	 * @return the value bound to {@code text}
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns a new {@link GuestbookEntry} using the data submitted in the request.
	 *
	 * @return the newly created {@link GuestbookEntry}
	 * @throws IllegalArgumentException if you call this on an instance without the name and text actually set.
	 */
	GuestbookEntry toNewEntry() {
		return new GuestbookEntry(getName(), getText());
	}
}
