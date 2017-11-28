package app.anish.com.tapp.database;

import java.util.List;

/**
 * Created by anish_khattar25 on 11/28/17.
 */

public interface PeopleMetDao {
    void createPerson(PersonMet personMet);
    void deletePerson(PersonMet personMet);
    List<PersonMet> getAllPeopleMet();
}
