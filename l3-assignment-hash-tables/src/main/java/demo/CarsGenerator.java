package demo;

import gui.ValidationException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CarsGenerator {

    private static final String ID_CODE = "TA";
    private static int serNr = 100;

    private Queue<String> keys;
    private Queue<Car> cars;

    /**
     * Generates car and car id queues. This method is used in the graphical interface
     *
     * @param setSize queue size
     * @throws ValidationException
     */
    public void generateShuffleIdsAndCars(int setSize) throws ValidationException {
        keys = generateShuffleIds(setSize);
        cars = generateShuffleCars(setSize);
        int LOX = 0;
    }

    /**
     * Pops the first car element in the car queue.
     * If car queue is empty, ValidationException is thrown.
     * This method is used in the graphical interface
     *
     * @return the first car element in the car queue
     */
    public Car getCar() {
        if (cars == null) {
            throw new ValidationException("carsNotGenerated");
        }
        if (cars.isEmpty()) {
            throw new ValidationException("allSetStoredToMap");
        }

        return cars.poll();
    }

    /**
     * Pops the first id element in the id queue.
     * If id queue is empty, ValidationException is thrown.
     * This method is used in the graphical interface
     *
     * @return the first id element in the id queue
     */
    public String getCarId() {
        if (keys == null) {
            throw new ValidationException("carsIdsNotGenerated");
        }
        if (keys.isEmpty()) {
            throw new ValidationException("allKeysStoredToMap");
        }

        return keys.poll();
    }

    public static Queue<Car> generateShuffleCars(int size) {
        LinkedList<Car> cars = IntStream.range(0, size)
                .mapToObj(i -> new Car.Builder().buildRandom())
                .collect(Collectors.toCollection(LinkedList::new));
        Collections.shuffle(cars);
        return cars;
    }

    public static Queue<String> generateShuffleIds(int size) {
        LinkedList<String> keys = IntStream.range(0, size)
                .mapToObj(i -> generateId())
                .collect(Collectors.toCollection(LinkedList::new));
        Collections.shuffle(keys);
        return keys;
    }

    public static String generateId() {
        return ID_CODE + (serNr++);
    }
}
