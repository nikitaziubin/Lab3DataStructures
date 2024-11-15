package demo;

import utils.*;

import java.util.Locale;

import static utils.HashMap.DEFAULT_INITIAL_CAPACITY;
import static utils.HashMap.DEFAULT_LOAD_FACTOR;

public class ManualTest {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US); // Unify number formats
        executeTest();
    }

    public static void executeTest() {
        Car car1 = new Car("Renault", "Laguna", 1997, 50000, 1700);
        Car car2 = new Car("Renault", "Megane", 2001, 20000, 3500);
        Car car3 = new Car("Toyota", "Corolla", 2001, 20000, 8500.8);
        Car car4 = new Car("Renault Laguna 2001 115900 7500");
        Car car5 = new Car.Builder().buildRandom();
        Car car6 = new Car("Honda   Civic  2007  36400 8500.3");
        Car car7 = new Car("Renault Laguna 2001 115900 7500");

        // array of hash map keys
        String[] carsIds = {"TA156", "TA102", "TA178", "TA126", "TA105", "TA106", "TA107", "TA108"};
        // array of hash map values
        Car[] cars = {car1, car2, car3, car4, car5, car6, car7};
        executeCarMapTests(carsIds, cars);
        executeCarMapOaTests(carsIds, cars);
    }

    private static void executeCarMapTests(String[] carsIds, Car[] cars) {
        ParsableMap<String, Car> carsMap = new ParsableHashMap<>(
                String::new,
                Car::new,
                DEFAULT_INITIAL_CAPACITY,
                DEFAULT_LOAD_FACTOR,
                HashManager.HashType.DIVISION
        );

        for (int id = 0; id < cars.length; id++) {
            carsMap.put(carsIds[id], cars[id]);
        }

        Ks.oun("Distribution of key-value pairs in the hash map:");
        carsMap.println("");
        Ks.oun("Does key exists in the hash map?");
        Ks.oun(carsMap.contains(carsIds[6]));
        Ks.oun(carsMap.contains(carsIds[7]));
        Ks.oun("Distribution of key-value pairs in the hash map. Only keys displayed:");
        carsMap.println("=");

        Ks.oun("Lookup by key:");
        Ks.oun(carsMap.get(carsIds[2]));
        Ks.oun(carsMap.get(carsIds[7]));
        Ks.oun("Print the string of key-value pairs:");
        Ks.ounn(carsMap);
    }

    private static void executeCarMapOaTests(String[] carsIds, Car[] cars) {
        ParsableMap<String, Car> carsMapOa = new ParsableHashMapOa<>(
                String::new,
                Car::new,
                DEFAULT_INITIAL_CAPACITY,
                DEFAULT_LOAD_FACTOR,
                HashManager.HashType.DIVISION,
                HashMapOa.OpenAddressingType.LINEAR
        );

        for (int id = 0; id < cars.length; id++) {
            carsMapOa.put(carsIds[id], cars[id]);
        }

        Ks.oun("Distribution of key-value pairs in the hash map based on the open addressing:");
        carsMapOa.println("");
        Ks.oun("Does key exists in the hash map?");
        Ks.oun(carsMapOa.contains(carsIds[6]));
        Ks.oun(carsMapOa.contains(carsIds[7]));
        Ks.oun("Distribution of key-value pairs in the hash map based on the open addressing. Only keys displayed:");
        carsMapOa.println("=");

        Ks.oun("Lookup by key:");
        Ks.oun(carsMapOa.get(carsIds[2]));
        Ks.oun(carsMapOa.get(carsIds[7]));
        Ks.oun("Print the string of key-value pairs:");
        Ks.ounn(carsMapOa);
    }
}
