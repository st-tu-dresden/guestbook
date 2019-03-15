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

import javax.validation.Valid;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A controller to handle web requests to manage {@link GuestbookEntry}s
 *
 * @author Paul Henke
 * @author Oliver Gierke
 */
@Controller
class GuestbookController {

	// A special header sent with each AJAX request
	private static final String IS_AJAX_HEADER = "X-Requested-With=XMLHttpRequest";

	private final GuestbookRepository guestbook;

	/**
	 * Creates a new {@link GuestbookController} using the given {@link GuestbookRepository}. Spring will look for a bean of type
	 * {@link GuestbookRepository} and hand this into this class when an instance is created.
	 *
	 * @param guestbook must not be {@literal null}.
	 */
	public GuestbookController(GuestbookRepository guestbook) {

		Assert.notNull(guestbook, "Guestbook must not be null!");
		this.guestbook = guestbook;
	}

	/**
	 * Handles requests to the application root URI. Note, that you can use {@code redirect:} as prefix to trigger a
	 * browser redirect instead of simply rendering a view.
	 *
	 * @return
	 */
	@GetMapping(path = "/")
	String index() {
		return "redirect:/guestbook";
	}

	/**
	 * Handles requests to access the guestbook. Obtains all currently available {@link GuestbookEntry}s and puts them
	 * into the {@link Model} that's used to render the view.
	 *
	 * @return
	 */
	@GetMapping(path = "/guestbook")
	String guestBook(Model model, @ModelAttribute(binding = false) GuestbookForm form) {

		model.addAttribute("entries", guestbook.findAll());
		model.addAttribute("form", form);

		return "guestbook";
	}

	/**
	 * Handles requests to create a new {@link GuestbookEntry}. Uses the fields {@code name} and {@code text} from the
	 * HTML form via the {@link RequestParam} annotations. The mapping also supports other types than {@link String}, see
	 * {@link #removeEntry(Long)}.
	 * <p>
	 * For the sake of simplicity we don't do any validation here. Spring has support for that kind of stuff but we leave
	 * that for the VideoShop example to cover.
	 *
	 * @param name the name of the person that made the entry
	 * @param text the actual text of the entry
	 * @return
	 */
	@PostMapping(path = "/guestbook")
	String addEntry(@Valid @ModelAttribute("form") GuestbookForm form, Errors errors, Model model) {

		if (errors.hasErrors()) {
			return guestBook(model, form);
		}

		guestbook.save(form.toNewEntry());

		return "redirect:/guestbook";
	}

	/**
	 * Handles AJAX requests to create a new {@link GuestbookEntry}.
	 *
	 * @param name the name of the person that made the entry
	 * @param text the actual text of the entry
	 * @param model
	 * @return
	 * @see #addEntry(String, String)
	 */
	@PostMapping(path = "/guestbook", headers = IS_AJAX_HEADER)
	String addEntry(@Valid GuestbookForm form, Model model) {

		model.addAttribute("entry", guestbook.save(form.toNewEntry()));
		model.addAttribute("index", guestbook.count());

		return "guestbook :: entry";
	}

	/**
	 * Deletes a {@link GuestbookEntry}. Note how the path variable used in the {@link RequestMapping} annotation is bound
	 * to the controller method using the {@link PathVariable} annotation.
	 *
	 * @param id the id of the {@link GuestbookEntry} to delete.
	 * @return
	 */
	@DeleteMapping(path = "/guestbook/{id}")
	String removeEntry(@PathVariable Long id) {

		guestbook.deleteById(id);

		return "redirect:/guestbook";
	}

	/**
	 * Handles AJAX requests to delete {@link GuestbookEntry}s.
	 *
	 * @param id the id of the {@link GuestbookEntry} to delete.
	 * @return
	 */
	@DeleteMapping(path = "/guestbook/{id}", headers = IS_AJAX_HEADER)
	HttpEntity<?> removeEntryJS(@PathVariable Long id) {

		return guestbook.findById(id).map(e -> {

			guestbook.deleteById(e.getId());
			return ResponseEntity.ok().build();

		}).orElse(ResponseEntity.notFound().build());
	}
}
