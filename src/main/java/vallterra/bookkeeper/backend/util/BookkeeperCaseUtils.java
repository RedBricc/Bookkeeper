package vallterra.bookkeeper.backend.util;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.validation.annotation.Validated;

@Validated
public class BookkeeperCaseUtils {

    public static String snakeCaseToTitleCase(@Nullable String input) {
        var spaceSeparated = StringUtils.replace(input, "_", " ");
        return WordUtils.capitalizeFully(spaceSeparated);
    }

}
