package utils;

/* @author Eimutis Karčiauskas
 *
 * This is the interface that the data classes developed by students have to implement.
 * The methods provide a convenient way of forming data from strings.
 ******************************************************************************/
public interface Parsable<T> {

    /**
     * Forms an object from a string
     *
     * @param dataString
     */
    void parse(String dataString);
}
