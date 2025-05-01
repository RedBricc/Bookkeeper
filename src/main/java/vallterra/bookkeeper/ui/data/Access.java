package vallterra.bookkeeper.ui.data;

import org.jooq.DSLContext;
import org.springframework.context.ApplicationContext;

public class Access {

    public static ApplicationContext applicationContext;

    public static DSLContext db() {
        return applicationContext.getBean(DSLContext.class);
    }

}
