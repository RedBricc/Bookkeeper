package vallterra.bookkeeper;

import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import vallterra.bookkeeper.ui.data.Access;

@SpringBootApplication
@Push(value = PushMode.AUTOMATIC)
public class BookkeeperApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookkeeperApplication.class, args);
    }

    @EventListener
    public void contextRefreshed(ContextRefreshedEvent event) {
        Access.applicationContext = event.getApplicationContext();
    }

}
