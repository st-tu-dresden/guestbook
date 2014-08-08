package guestbook.web;

import guestbook.Guestbook;
import guestbook.GuestbookEntry;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

// ‎(｡◕‿◕｡)
// Bitte Welcome- und danach GuestbookController anschauen, danach erst wiederkommen. ;)
// Das hier sieht dem normalen GuestbookController schon sehr ähnlich, 
// allerdings etwas modifiziert um über Javascript mit der Anwendung zu kommunizieren.
// Das Beispiel ist mit Hilfe von http://blog.springsource.com/2010/01/25/ajax-simplifications-in-spring-3-0/ entstanden.


@Controller
@RequestMapping("/ajaxbook")
class AjaxGuestbookController {

	private final Guestbook guestbook;

	@Autowired
	public AjaxGuestbookController(Guestbook guestbook) {
		this.guestbook = guestbook;
	}

	@ModelAttribute("guestbookEntries")
	public Iterable<GuestbookEntry> getEntries() {
		return guestbook.findAll();
	}

	@RequestMapping
	public String guestBook() {
		return "guestbook_ajax";
	}

	@RequestMapping(method = RequestMethod.POST)
	public HttpEntity<GuestbookEntry> addEntry(@RequestParam("name") String name, //
			@RequestParam("text") String text) {

		return new ResponseEntity<>(new GuestbookEntry(name, text), HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public HttpEntity<Boolean> removeEntry(@PathVariable Long id) {

		Optional<GuestbookEntry> entry = guestbook.findOne(id);

		return entry.map(e -> new ResponseEntity<>(true, HttpStatus.OK)).//
				orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
