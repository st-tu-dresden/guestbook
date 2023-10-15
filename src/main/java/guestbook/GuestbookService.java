package guestbook;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class GuestbookService {

    /**
     * Calculates the number of entries in the current repository.
     *
     * @param repository the repo used to save the guestbook entries
     * @return {@code long} containing the number of entries
     */
    public static long numberOfEntries(GuestbookRepository repository) {
        return repository.count();
    }
}
