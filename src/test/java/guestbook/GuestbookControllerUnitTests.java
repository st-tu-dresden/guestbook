/*
 * Copyright 2016-2017 the original author or authors.
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.util.Streamable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Unit tests for {@link GuestbookController}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class GuestbookControllerUnitTests {

	@Mock Guestbook guestbook;

	@Test
	public void populatesModelForGuestbook() {

		GuestbookEntry entry = new GuestbookEntry("Yoda", "May the 4th b with you!");
		doReturn(Streamable.of(entry)).when(guestbook).findAll();

		Model model = new ExtendedModelMap();
		GuestbookForm form = mock(GuestbookForm.class);

		GuestbookController controller = new GuestbookController(guestbook);
		String viewName = controller.guestBook(model, form);

		assertThat(viewName).isEqualTo("guestbook");
		assertThat(model.asMap().get("entries")).isInstanceOf(Iterable.class);
		assertThat(model.asMap().get("form")).isEqualTo(form);

		verify(guestbook, times(1)).findAll();
	}
}
