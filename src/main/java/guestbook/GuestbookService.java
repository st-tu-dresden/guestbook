package guestbook;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class GuestbookService {

    public static long numberOfEntries(GuestbookRepository repository){
        return repository.count();
    }
}
