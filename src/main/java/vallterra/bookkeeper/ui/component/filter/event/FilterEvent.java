package vallterra.bookkeeper.ui.component.filter.event;

import org.jooq.Condition;

import java.util.UUID;

public record FilterEvent (UUID filterId, Condition condition) {
}
