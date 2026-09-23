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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.Method;

/**
 * A WebDriverListener that slows down the execution of the tests by adding a delay before and after certain actions.
 * <p>
 * This is useful for debugging and observing the behavior of the application during UI tests.
 * </p>
 *
 * @author Oliver Geisel
 */
public class SlowMotionListener implements WebDriverListener {

	private void delay() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void beforeClick(WebElement element) {
		delay();
	}

	@Override
	public void afterClick(WebElement element) {
		delay();
	}

	@Override
	public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
		delay();
	}

	@Override
	public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
		delay();
	}

	@Override
	public void beforeAnyNavigationCall(WebDriver.Navigation navigation, Method method, Object[] args) {
		delay();
	}

	@Override
	public void beforeIsSelected(WebElement element) {
		delay();
	}


}