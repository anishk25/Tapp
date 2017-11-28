package app.anish.com.tapp.database;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * Created by anish_khattar25 on 11/28/17.
 */

@Data
@Builder
public class PersonMet {

    private final String name;
    private final String phoneNumber;
    private final String email;
    private final Date dateMet;
    private final String facebookId;
    private final String linkedInId;
}
