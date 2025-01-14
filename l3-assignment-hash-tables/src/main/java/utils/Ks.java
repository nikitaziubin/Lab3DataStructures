package utils;

import java.io.*;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gui.Table.ARROW;
import static java.util.stream.Collectors.summarizingInt;

/*
 * The class is designed for convenient retrieval of data from the keyboard and
 * efficient presentation of results to sout and serr streams.
 *
 * @author eimutis
 */
public class Ks {

    private static final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
    private static String dataFolder = "data";

    static public String giveString(String prompt) {
        Ks.ou(prompt);
        try {
            return keyboard.readLine();
        } catch (IOException e) {
            Ks.ern("Keyboard input not working, program stopped working");
            System.exit(0);
        }
        return "";
    }

    static public long giveLong(String prompt) {
        while (true) {
            String s = giveString(prompt);
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                Ks.ern("Incorrect number format, please repeat");
            }
        }
    }

    static public long giveLong(String prompt, long bound1, long bound2) {
        while (true) {
            long a = giveLong(prompt + " between boundaries [" + bound1 + ":" + bound2 + "]=");
            if (a < bound1) {
                Ks.ern("Number less than allowed, please repeat");
            } else if (a > bound2) {
                Ks.ern("Number greater than allowed, please repeat");
            } else {
                return a;
            }
        }
    }

    static public int giveInt(String prompt) {
        while (true) {
            long a = giveLong(prompt);
            if (a < Integer.MIN_VALUE) {
                Ks.ern("Number less than " + Integer.MIN_VALUE
                        + ", please repeat");
            } else if (a > Integer.MAX_VALUE) {
                Ks.ern("Number greater than " + Integer.MAX_VALUE
                        + ", please repeat");
            } else {
                return (int) a;
            }
        }
    }

    static public int giveInt(String prompt, int bound1, int bound2) {
        return (int) giveLong(prompt, bound1, bound2);
    }

    static public double giveDouble(String prompt) {
        while (true) {
            String s = giveString(prompt);
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                if (s.contains(",")) {
                    Ks.ern("Use a full stop instead of a comma, please repeat");

                } else {
                    Ks.ern("Incorrect number format, please repeat");

                }
            }
        }
    }

    static public double giveDouble(String prompt, double bound1, double bound2) {
        while (true) {
            double a = giveDouble(prompt + " between boundaries [" + bound1 + ":" + bound2 + "]=");
            if (a < bound1) {
                Ks.ern("Number less than allowed, please repeat");
            } else if (a > bound2) {
                Ks.ern("Number greater than allowed, please repeat");
            } else {
                return a;
            }
        }
    }

    static public String giveFileName() {
        File dir = new File(dataFolder);
        dir.mkdir();
        oun("Files available to you " + Arrays.toString(dir.list()));
        String fn = giveString("Specify the name of the selected data file: ");
        return (fn);
    }

    static public String getDataFolder() {
        return dataFolder;
    }

    static public void setDataFolder(String folderName) {
        dataFolder = folderName;
    }

    private static final PrintStream sout = System.out;
    private static final PrintStream serr = System.out;
    private static int lineNo;
    private static int errorNo;
    private static final boolean formatStartOfLine = true;

    static public void ou(Object obj) {
        if (formatStartOfLine) {
            sout.printf("%2d| %s", ++lineNo, obj);
        } else {
            sout.println(obj);
        }
    }

    static public void oun(Object obj) {
        if (formatStartOfLine) {
            sout.printf("%2d| %s\n", ++lineNo, obj);
        } else {
            sout.println(obj);
        }
    }

    static public void ounn(Object obj) {
        if (formatStartOfLine) {
            sout.printf("%2d|\n", ++lineNo);
            sout.printf("%s\n", obj);
        } else {
            sout.println(obj);
        }
    }

    static public void oufln(String format, Object... args) {
        sout.printf(format, args);
        sout.println();
    }

    static public void out(Object obj) {
        sout.print(obj);
    }

    static public void ouf(String format, Object... args) {
        sout.printf(format, args);
    }

    static public void er(Object obj) {
        serr.printf("***Error %d: %s", ++errorNo, obj);
    }

    static public void ern(Object obj) {
        serr.printf("***Error %d: %s\n", ++errorNo, obj);
    }

    static public void erf(String format, Object... args) {
        serr.printf(format, args);
    }


    public static void printMapModel(String delimiter, String[][] mapModel) {
        java.util.Map<Boolean, IntSummaryStatistics> mappingsStatistics = summaryStatistics(mapModel, delimiter);

        for (String[] row : mapModel) {
            for (int j = 0; j < row.length; j++) {
                String format;
                if (j == 0) { // first column
                    format = "%" + (mappingsStatistics.get(Boolean.FALSE).getMax() + 1) + "s";
                } else { // rest of the columns
                    format = j % 2 == 1 ? "%-2s" : "%-" + (mappingsStatistics.get(Boolean.TRUE).getMax() + 1) + "s";
                }

                String value = row[j];
                Ks.ouf(format, (value == null ? "" : split(value, delimiter) + " "));
            }
            Ks.oufln("");
        }
    }

    private static Map<Boolean, IntSummaryStatistics> summaryStatistics(String[][] mapModel, String delimiter) {
        Function<String, Boolean> groupingFunction = s -> !s.equals(ARROW) && !s.startsWith("[") && !s.endsWith("]");
        return Arrays
                .stream(mapModel)
                .flatMap(Arrays::stream)
                .map(s -> split(s, delimiter))
                .collect(Collectors.groupingBy(groupingFunction, summarizingInt(String::length)));
    }

    private static String split(String s, String delimiter) {
        int k = s.indexOf(delimiter);
        if (k <= 0) {
            return s;
        }
        return s.substring(0, k);
    }
}
