package vallterra.bookkeeper.ui.data;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContextAccess {

    private final ApplicationContext applicationContext;

    public DSLContext db() {
        return applicationContext.getBean(DSLContext.class);
    }

}
