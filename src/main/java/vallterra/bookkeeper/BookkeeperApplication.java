package vallterra.bookkeeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import vallterra.bookkeeper.ui.data.Access;

@SpringBootApplication
public class BookkeeperApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookkeeperApplication.class, args);
    }

    @EventListener
    public void contextRefreshed(ContextRefreshedEvent event) {
        Access.applicationContext = event.getApplicationContext();
    }

}
