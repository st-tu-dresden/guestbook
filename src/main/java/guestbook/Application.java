package guestbook;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

// ‎(｡◕‿◕｡)
// Der Einstiegspunkt unserer Applikation.
// Hier kann sehr viel konfiguriert werden, da dies im GB nicht benötigt wird, wird dieser Punkt auf den Videoshop verschoben.
// Da sich hier eine main-Methode befindet, kann die Webanwendung wie eine normale Anwendung gestartet werden.

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CharacterEncodingFilter characterEncodingFilter() {

		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		return characterEncodingFilter;
	}

	@Autowired Guestbook guestbook;

	@PostConstruct
	void initialize() {

		guestbook.save(new GuestbookEntry("H4xx0r", "first!!!"));
		guestbook.save(new GuestbookEntry("Arni", "Hasta la vista, baby"));
		guestbook.save(new GuestbookEntry("Duke Nukem",
				"It's time to kick ass and chew bubble gum. And I'm all out of gum."));
		guestbook.save(new GuestbookEntry("Gump1337",
				"Mama always said life was like a box of chocolates. You never know what you're gonna get."));
	}
}
