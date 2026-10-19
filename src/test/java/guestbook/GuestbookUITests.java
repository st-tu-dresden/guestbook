/*
 * Copyright 2026-2026 the original author or authors.
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

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A small example for UI tests for the guestbook page using Selenide, a bit more powerful Extension to Selenium.
 * <p>
 * The SpringBootTest annotation is used to start the application on a random port for testing. So it not runs on
 * port 8080. So you can run the application and the tests at the same time. The port is injected into the test class
 * via the {@link LocalServerPort} annotation.
 *
 * <p>
 * For independent Test the Context of the application is set to be dirty after each test method.
 * This is only for demonstration purposes. In a real application you would use a test database or mock the database.
 * The annotation {@link DirtiesContext} with the class mode
 * {@link DirtiesContext.ClassMode#AFTER_EACH_TEST_METHOD} is used to reset the context after <u>each</u> test method.
 * <br>
 * Don't use the {@link org.springframework.transaction.annotation.Transactional} annotation for UI tests.
 * It does not work because the transaction is rolled back after the test method and the UI test runs in a different thread.
 * </p>
 *
 * <p>
 * Tipp: Create a selenide.properties file in src/test/resources to configure Selenide. <br>For example:
 *     <ul>
 *         <li>selenide.browser=chrome - Sets the browser to Chrome</li>
 *         <li>selenide.headless=true - Sets the headless mode (no GUI) to true</li>
 *         <li>selenide.timeout=10000 - Timeout in milliseconds</li>
 *     </ul>
 * </p>
 *
 * @author Oliver Geisel
 * @see <a href="https://www.selenium.dev/">Selenium</a>
 * @see <a href="https://selenide.org/">Selenide</a>
 * @see <a href="https://testcontainers.com/">Testcontainers</a>
 * @see DataJpaTest
 */
// Start the application on a random port for testing
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GuestbookUITests {


	@LocalServerPort
	private int port; // Inject the random port number into this field

	@AfterAll
	static void tearDown() {
		// Quit the WebDriver after all tests are done to clean up resources
		Selenide.closeWebDriver();
	}

	/**
	 * Set up the test environment before each test method.
	 * <p>
	 * This method opens the guestbook page in the browser.
	 * <ul>
	 *     <li> It adds a {@link SlowMotionListener} to slow down the execution of the tests for better visibility
	 *           during debugging.
	 *     </li>
	 *     <li>Adds a custom cursor to the page for better visibility of mouse movements during test execution</li>
	 * </ul>
	 * </p>
	 *
	 * @see <a href="https://github.com/selenide-examples/gmail/blob/main/test/org/selenide/examples/gmail/Highlighter.java">
	 * Highlighting of elements during execution</a>
	 */
	@BeforeEach
	void setUp() {
		// OPTIONAL: Activate a SlowMotionListener to slow down the execution of the tests for
		// better visibility during debugging. This must be done before opening the browser. Otherwise, it will not work.
		WebDriverRunner.addListener(new SlowMotionListener());

		// Set the base URL for Selenide to the guestbook page
		String baseUrl = "http://localhost:" + port + "/guestbook";
		// open calls the webbrowser to open the specified URL
		open(baseUrl);


		// OPTIONAL: Add a custom cursor to the page for better visibility of mouse movements during test execution
		executeJavaScript("""
				    const cursor = document.createElement('div');
				    cursor.id = 'selenide-cursor';
				    cursor.style.cssText = `
				        position: fixed;
				        width: 12px;
				        height: 12px;
				        border: 2px solid red;
				        border-radius: 50%;
				        pointer-events: none;
				        z-index: 99;
				        transform: translate(-50%, -50%);
				    `;
				    document.body.appendChild(cursor);
				
				    document.addEventListener('mousemove', e => {
				        cursor.style.left = e.clientX + 'px';
				        cursor.style.top = e.clientY + 'px';
				    });
				""");
	}

	/**
	 * Simple test to check if something is present on the page.
	 * It searchs the element by tag name and checks if the text is present.
	 */
	@Test
	void testGuestbookPageLoads() {
		// The $()-Method is a Selenide method to find the FIRST element matching the given criteria
		$("h1").shouldHave(text("Gästebuch"));
	}

	/**
	 * Advanced test to check the number of entries in the guestbook.
	 * It uses the $$() method to find ALL elements matching the given criteria and checks the size of the collection.
	 */
	@Test
	void countNumberOfEntries() {
		// Use $$()-Method to find ALL elements with the given CSS selector or other criteria
		int numberOfEntries = $$(".entry").size();
		assertEquals(4, numberOfEntries, "There should be 4 entries in the guestbook initially.");
	}


	/**
	 * Complex UI test with a complete use-case to enter a new entry in the guestbook and verify that it appears in the list of entries.
	 * It uses Selenide methods to interact with the form fields, submit the form, and verify the result.
	 */
	@Test
	void enterNewEntry() {
		// Fill in the form fields and submit the form
		SelenideElement nameInput = $("#name");
		SelenideElement textInput = $("#text");
		WebElement button = $("#form [type='submit']").scrollTo();
		// SelenideElement is a subclass of WebElement.
		// scrollTo() is a Selenide method to scroll the element into view before clicking it.

		nameInput.setValue("GLaDOS");
		textInput.setValue("The cake is a lie.");
		button.click();

		// Verify that the new entry appears in the guestbook
		SelenideElement entriesDiv = $("#entries");
		ElementsCollection allEntries = entriesDiv.$$(
				By.className("entry")); // You can call $ and $$ on SelenideElement to find child elements
		SelenideElement lastEntry = allEntries.last();

		// Validation via build-in Selenide methods; criteria use By for better readability.
		lastEntry.$(By.className("entry-text")).shouldHave(text("The cake is a lie."));
	}


	/**
	 * A test wit multiple steps (many sites) to delete an entry from the guestbook and verify that it has been removed.
	 * It uses Selenide methods to find the delete button, click it, and check the number of entries after deletion.
	 * First it has to log in as admin to be able to delete an entry.
	 *
	 * <p>This is a complex test case. It uses more advanced Selenide features. Also, it makes some more checks that
	 * are not necessary but show what you can do.</p>
	 *
	 * @see <a href="https://selenide.org/javadoc/current/com/codeborne/selenide/Configuration.html#timeout">Selenide Configuration</a>
	 */
	@Test
	void testDeleteEntry() {
		// Got to the login page and login as admin
		$("a[href='/login']").click();
		// check current URL to be sure we are on the login page
		String currentUrl = WebDriverRunner.url();
		assertEquals("http://localhost:" + port + "/login", currentUrl, "The current URL should be the login page.");

		// Get the password from the application.properties in test/resources. Don't use this in practice.
		String password = "TOP-SECRET";

		// Fill in the login form
		$("#username").setValue("user");
		$("#password").setValue(password);
		$("button").click();

		// Delete the third entry in the guestbook by clicking the delete button.
		SelenideElement firstDeleteButton = $("#entry3");
		firstDeleteButton.$("button[title='Eintrag löschen']").click();

		// Since the request ist asynchronous, we need to wait for the entry to be removed from the DOM.
		// Selenide will automatically wait for the element to disappear. There is a built-in timeout for this.
		// The default timeout is 4 seconds.
		firstDeleteButton.shouldNot(exist);

		// Verify that the entry has been deleted by checking the number of entries
		int numberOfEntriesAfterDeletion = $$(".entry").size();
		assertEquals(3, numberOfEntriesAfterDeletion, "There should be 3 entries in the guestbook after deletion.");
	}

}
