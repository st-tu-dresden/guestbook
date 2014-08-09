package guestbook.web;

import guestbook.Guestbook;
import guestbook.GuestbookEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

// ‎(｡◕‿◕｡)
// Schon den Kommentar im WelcomeController gelesen?
// Wie hier zu sehen ist, kann man die @RequestMapping auch an einen Controller hängen.

@Controller
public class GuestbookController {

	private static final String IS_AJAX_HEADER = "X-Requested-With=XMLHttpRequest";

	private final Guestbook guestbook;

	// ‎(｡◕‿◕｡)
	// Via @Autowired wird automatisch eine Instanz des Guestbooks in den Controller gegegeben.
	@Autowired
	public GuestbookController(Guestbook guestbook) {
		this.guestbook = guestbook;
	}

	@RequestMapping("/")
	public String index() {
		return "redirect:/guestbook";
	}

	@RequestMapping(value = "/guestbook", method = RequestMethod.GET)
	public String guestBook() {
		return "guestbook";
	}

	// ‎(｡◕‿◕｡)
	// In der Html-Form wurden 2 Inputfelder mit Namen versehen, auf den Inhalt der Felder kann mit @RequestParam
	// zugegriffen werden.
	// Dabei werden nicht nur Strings, sondern auch andere Typen, wie z.B. Integer (siehe removeEntry), unterstützt.

	// Auf Validierung, z.B. leerer Name oder Text wurde an der Stelle verzichtet.
	// Spring bietet hierfür aber auch Support, sehen wir im Videoshop.
	@RequestMapping(value = "/guestbook", method = RequestMethod.POST)
	public String addEntry(@RequestParam("name") String name, @RequestParam("text") String text) {

		guestbook.save(new GuestbookEntry(name, text));
		return "redirect:/guestbook";
	}

	/**
	 * Handles AJAX requests to create a new {@link GuestbookEntry}.
	 * 
	 * @param name
	 * @param text
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/guestbook", method = RequestMethod.POST, headers = IS_AJAX_HEADER)
	String addEntry(@RequestParam("name") String name, @RequestParam("text") String text, Model model) {

		model.addAttribute("entry", guestbook.save(new GuestbookEntry(name, text)));
		model.addAttribute("index", guestbook.count());
		return "guestbook :: entry";
	}

	@RequestMapping(value = "/guestbook/{id}", method = RequestMethod.DELETE)
	public String removeEntry(@PathVariable Long id) {
		guestbook.delete(id);
		return "redirect:/guestbook";
	}

	// ‎(｡◕‿◕｡)
	// @ModelAttribute sorgt dafür, dass bei _jedem_ Controlleraufruf die Variable guestbookEntries im Html genutzt werden
	// kann
	// Der Wert wird in ein Model abgelegt und ist damit aus dem View abrufbar.
	@ModelAttribute("entries")
	private Iterable<GuestbookEntry> getEntries() {
		return guestbook.findAll();
	}

	// ‎(｡◕‿◕｡)
	// Benötigt man einen Wert nicht bei jedem Aufruf sondern nur bei einem ganz bestimmten
	// oder ist der Wert von anderen Requestparameter abhängig, so ist es sinnvoll das Model selber zu füllen
	// Dazu reicht es diese in die Parameterliste aufzunehmen, Spring kümmert sich darum diese zu übergeben.
	// Btw, es sind noch viel mehr Parameter möglich, welche von Spring übergeben werden können, siehe:
	// http://docs.spring.io/spring/docs/3.2.x/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html
	public String dummy(Model model) {
		model.addAttribute("variable", 1337);
		return "guestbook";
	}
}
