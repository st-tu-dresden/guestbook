package guestbook;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.util.Assert;

/**
 * A guestbook entry.
 * 
 * @author Paul Henke
 * @author Oliver Gierke
 */
@Entity
public class GuestbookEntry {

	private @Id @GeneratedValue Long id;
	private String name, text;
	private Date date;

	/**
	 * Creates a new {@link GuestbookEntry} for the given name and text.
	 * 
	 * @param name must not be {@literal null} or empty.
	 * @param text must not be {@literal null} or empty;
	 */
	public GuestbookEntry(String name, String text) {

		Assert.hasText(name, "Name must not be null or empty!");
		Assert.hasText(text, "Text must not be null or empty!");

		this.name = name;
		this.text = text;
		this.date = new Date();
	}

	GuestbookEntry() {}

	public String getName() {
		return name;
	}

	public Long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public String getText() {
		return text;
	}
}
