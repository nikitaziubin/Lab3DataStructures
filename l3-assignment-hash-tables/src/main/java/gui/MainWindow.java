package gui;

import demo.Car;
import demo.CarsGenerator;
import utils.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Swing window
 * <pre>
 *                  BorderLayout
 *              (Center)               (East)
 * |----------scrollTable----------|-scrollEast-|
 * |                               |            |
 * |                               |            |
 * |                               |            |
 * |                               |            |
 * |             table             |            |
 * |                               |            |
 * |                               |            |
 * |                               |------------|
 * |                               | panButtons |
 * |                               |            |
 * |----------------panParam12Events------------|
 * |-------scrollParam12---------|-scrollEvents-|
 * |-----------|-----------|--------------------|
 * | panParam1 | panParam2 |      taEvents      |
 * |-----------|-----------|--------------------|
 *                   (South)
 * </pre>
 *
 * @author darius.matulis@ktu.lt
 */
public class MainWindow extends JFrame implements ActionListener {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("edu.ktu.ds.lab3.gui.messages");

    private static final int TF_WIDTH = 6;
    private static final int NUMBER_OF_BUTTONS = 3;

    private final JComboBox<String> cmbCollisionTypes = new JComboBox<>();
    private final JComboBox<String> cmbHashFunctions = new JComboBox<>();
    private final JTextArea taInput = new JTextArea();
    private final Table table = new Table();
    private final JScrollPane scrollTable = new JScrollPane(table);
    private final JPanel panParam12 = new JPanel();
    private final JScrollPane scrollParam12 = new JScrollPane(panParam12);
    private final JPanel panParam12Events = new JPanel();
    private final JTextArea taEvents = new JTextArea();
    private final JScrollPane scrollEvents = new JScrollPane(taEvents);
    private final JPanel panEast = new JPanel();
    private final JScrollPane scrollEast = new JScrollPane(panEast);
    private Panels panParam1, panParam2, panButtons;
    private MainWindowMenu mainWindowMenu;

    private HashManager.HashType ht = HashManager.HashType.DIVISION;
    private ParsableMap<String, Car> map;
    private int sizeOfInitialSubSet, sizeOfGenSet, colWidth, initialCapacity;
    private float loadFactor;
    private final CarsGenerator carsGenerator = new CarsGenerator();

    public MainWindow() {
        initComponents();
    }

    private void initComponents() {
        // Blue panel (right side) is formed, JComboBoxes are filled in
        Stream.of(MESSAGES.getString("cmbCollisionType1"),
                        MESSAGES.getString("cmbCollisionType2"),
                        MESSAGES.getString("cmbCollisionType3"),
                        MESSAGES.getString("cmbCollisionType4"))
                .forEach(cmbCollisionTypes::addItem);
        cmbCollisionTypes.addActionListener(this);

        Stream.of(MESSAGES.getString("cmbHashFunction1"),
                        MESSAGES.getString("cmbHashFunction2"),
                        MESSAGES.getString("cmbHashFunction3"),
                        MESSAGES.getString("cmbHashFunction4"))
                .forEach(cmbHashFunctions::addItem);
        cmbHashFunctions.addActionListener(this);

        // Grid of buttons (blue) is formed. The Panels class is used.
        panButtons = new Panels(new String[]{
                MESSAGES.getString("button1"),
                MESSAGES.getString("button2"),
                MESSAGES.getString("button3")},
                1, NUMBER_OF_BUTTONS);
        panButtons.getButtons().forEach((btn) -> btn.addActionListener(this));
        IntStream.of(1, 2).forEach(p -> panButtons.getButtons().get(p).setEnabled(false));

        // Put everything in one (blue) panel
        panEast.setLayout(new BoxLayout(panEast, BoxLayout.Y_AXIS));
        Stream.of(new JLabel(MESSAGES.getString("border1")),
                        cmbCollisionTypes,
                        new JLabel(MESSAGES.getString("border2")),
                        cmbHashFunctions,
                        new JLabel(MESSAGES.getString("border3")),
                        taInput,
                        panButtons)
                .forEach(comp -> {
                    comp.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                    panEast.add(Box.createRigidArea(new Dimension(0, 2)));
                    panEast.add(comp);
                });

        // The first table of parameters (light green) is created. The Panels class is used.
        panParam1 = new Panels(
                new String[]{
                        MESSAGES.getString("lblParam11"),
                        MESSAGES.getString("lblParam12"),
                        MESSAGES.getString("lblParam13"),
                        MESSAGES.getString("lblParam14"),
                        MESSAGES.getString("lblParam15"),
                        MESSAGES.getString("lblParam16"),
                        MESSAGES.getString("lblParam17")},
                new String[]{
                        MESSAGES.getString("tfParam11"),
                        MESSAGES.getString("tfParam12"),
                        MESSAGES.getString("tfParam13"),
                        MESSAGES.getString("tfParam14"),
                        MESSAGES.getString("tfParam15"),
                        MESSAGES.getString("tfParam16"),
                        MESSAGES.getString("tfParam17")},
                TF_WIDTH);

        // The input parameters are checked for correctness. Cannot be negative.
        IntStream.of(0, 1, 2, 4).forEach(v -> panParam1.getTfOfTable().get(v).setInputVerifier(new NotNegativeNumberVerifier()));

        // The load factor entered is checked. Must be within (0;1]
        panParam1.getTfOfTable().get(3).setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                String text = ((JTextField) input).getText().trim();
                try {
                    float loadFactor = Float.parseFloat(text);
                    if (loadFactor <= 0.0 || loadFactor > 1.0) {
                        input.setBackground(Color.RED);
                        return false;
                    }
                    input.setBackground(Color.WHITE);
                    return true;
                } catch (NumberFormatException e) {
                    input.setBackground(Color.RED);
                    return false;
                }
            }
        });

        // A second table of parameters (yellowish) is generated. Class Panels is used
        panParam2 = new Panels(
                new String[]{
                        MESSAGES.getString("lblParam21"),
                        MESSAGES.getString("lblParam22"),
                        MESSAGES.getString("lblParam23"),
                        MESSAGES.getString("lblParam24"),
                        MESSAGES.getString("lblParam25"),
                        MESSAGES.getString("lblParam26"),
                        MESSAGES.getString("lblParam27")},
                new String[]{
                        MESSAGES.getString("tfParam21"),
                        MESSAGES.getString("tfParam22"),
                        MESSAGES.getString("tfParam23"),
                        MESSAGES.getString("tfParam24"),
                        MESSAGES.getString("tfParam25"),
                        MESSAGES.getString("tfParam26"),
                        MESSAGES.getString("tfParam27")}, TF_WIDTH);

        // The panels of the two parameter tables are added to the light grey panel
        Stream.of(panParam1, panParam2).forEach(panParam12::add);

        // The following panel is made up of a light grey panel and JTextArea events
        panParam12Events.setLayout(new BorderLayout());
        panParam12Events.add(scrollParam12, BorderLayout.WEST);
        panParam12Events.add(scrollEvents, BorderLayout.CENTER);

        // To ensure that the image always jumps to the bottom when attaching text to a JTextArea
        scrollEvents.getVerticalScrollBar()
                .addAdjustmentListener((AdjustmentEvent e) -> taEvents.select(taEvents.getCaretPosition() * taEvents.getFont().getSize(), 0));

        // A general L3 window with menus is created
        mainWindowMenu = new MainWindowMenu() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    Object command = ae.getSource();
                    KsGui.setFormatStartOfLine(true);
                    if (command.equals(mainWindowMenu.getMenu(0).getItem(0))) {
                        fileChooseMenu();
                    } else if (command.equals(mainWindowMenu.getMenu(0).getItem(1))) {
                        KsGui.ounerr(taEvents, MESSAGES.getString("notImplemented"));
                    } else if (command.equals(mainWindowMenu.getMenu(0).getItem(3))) {
                        System.exit(0);
                    } else if (command.equals(mainWindowMenu.getMenu(1).getItem(0))) {
                        JOptionPane.showOptionDialog(this,
                                MESSAGES.getString("author"),
                                MESSAGES.getString("menuItem21"),
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                new String[]{"OK"},
                                null);
                    }
                } catch (ValidationException e) {
                    KsGui.ounerr(taEvents, e.getMessage());
                } catch (Exception e) {
                    KsGui.ounerr(taEvents, MESSAGES.getString("systemError"));
                    e.printStackTrace(System.out);
                }
            }

            private void fileChooseMenu() {
                JFileChooser fc = new JFileChooser(".");
                // Supplemented by our filters
                fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        String filename = file.getName();
                        // Only directories and txt files are shown
                        return file.isDirectory() || filename.endsWith(".txt");
                    }

                    @Override
                    public String getDescription() {
                        return "*.txt";
                    }
                });
                int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    taEvents.setBackground(Color.white);
                    File file = fc.getSelectedFile();
                    mapGeneration(file.getAbsolutePath());
                }
            }

        };
        // The menu bar is placed in this frame
        setJMenuBar(mainWindowMenu);

        setLayout(new BorderLayout());
        add(scrollEast, BorderLayout.EAST);
        add(scrollTable, BorderLayout.CENTER);
        add(panParam12Events, BorderLayout.SOUTH);
        appearance();
    }

    /**
     * Cosmetics
     */
    private void appearance() {
        // Borders
        panParam12.setBorder(new TitledBorder(MESSAGES.getString("border4")));
        scrollTable.setBorder(new TitledBorder(MESSAGES.getString("border5")));
        scrollEvents.setBorder(new TitledBorder(MESSAGES.getString("border6")));

        scrollTable.getViewport().setBackground(Color.white);
        panParam1.setBackground(new Color(204, 255, 204));// Light green
        panParam2.setBackground(new Color(255, 255, 153));// Yellowish
        panEast.setBackground(new Color(147, 204, 210));// Pale blue
        panButtons.setBackground(new Color(147, 210, 192));

        // The second table of parameters (yellowish) will not be edited
        panParam2.getTfOfTable().forEach((comp) -> comp.setEditable(false));
        Stream.of(taInput, taEvents).forEach(comp -> {
            comp.setBackground(Color.white);
            comp.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        });
        taEvents.setEditable(false);
        scrollEvents.setPreferredSize(new Dimension(350, 0));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        KsGui.setFormatStartOfLine(true);
        try {
            System.gc();
            System.gc();
            System.gc();
            taEvents.setBackground(Color.white);
            Object source = ae.getSource();

            if (source instanceof JButton) {
                handleButtons(source);
            } else {
                boolean comboEquals = source.equals(cmbCollisionTypes) || source.equals(cmbHashFunctions);
                if (source instanceof JComboBox && comboEquals) {
                    IntStream.of(1, 2).forEach(p -> panButtons.getButtons().get(p).setEnabled(false));
                }
            }
        } catch (ValidationException e) {
            KsGui.ounerr(taEvents, MESSAGES.getString(e.getMessage()), e.getValue());
        } catch (UnsupportedOperationException e) {
            KsGui.ounerr(taEvents, e.getMessage());
        } catch (Exception e) {
            KsGui.ounerr(taEvents, MESSAGES.getString("systemError"));
            e.printStackTrace(System.out);
        }
    }

    private void handleButtons(Object source) {
        if (source.equals(panButtons.getButtons().get(0))) {
            mapGeneration(null);
        } else if (source.equals(panButtons.getButtons().get(1))) {
            mapPut();
        } else if (source.equals(panButtons.getButtons().get(2))) {
            KsGui.ounerr(taEvents, MESSAGES.getString("notImplemented"));
        }
    }

    public void mapGeneration(String filePath) {
        // Buttons 2 and 3 are disabled
        IntStream.of(1, 2).forEach(p -> panButtons.getButtons().get(p).setEnabled(false));
        // Duomenų nuskaitymas iš parametrų lentelės (žalios)
        readMapParameters();
        // Empty hash map is created depending on the type of collision handling
        createMap();
        // If no file is specified - cars are generated and placed in the hash map
        if (filePath == null) {
            carsGenerator.generateShuffleIdsAndCars(sizeOfGenSet);//, sizeOfInitialSubSet);
            for (int i = 0; i < Math.min(sizeOfGenSet, sizeOfInitialSubSet); i++) {
                map.put(
                        carsGenerator.getCarId(), //key
                        carsGenerator.getCar()    //value
                );
            }
            KsGui.ounArgs(taEvents, MESSAGES.getString("mapPuts"), map.size());
            IntStream.of(1, 2).forEach(p -> panButtons.getButtons().get(p).setEnabled(true));
        } else { // If a file is specified read from file
            map.load(filePath);
            if (map.isEmpty()) {
                KsGui.ounerr(taEvents, MESSAGES.getString("fileWasNotReadOrEmpty"), filePath);
            } else {
                KsGui.ou(taEvents, MESSAGES.getString("fileWasRead"), filePath);
            }
        }

        // Hash map displayed in the table
        table.setModel(
                map.getMapModel(),
                panParam1.getTfOfTable().get(5).getText(),
                map.getMaxChainSize() == -1 ? 1 : map.getMaxChainSize(),
                colWidth
        );
        // Hash table parameters to be updated (yellow table)
        updateHashtableParameters(false);
        // Buttons 2 and 3 are enabled
        IntStream.of(1, 2).forEach(p -> panButtons.getButtons().get(p).setEnabled(true));
    }

    public void mapPut() throws ValidationException {
        Car car = carsGenerator.getCar();
        String id = carsGenerator.getCarId();
        map.put(
                id, // key
                car // value
        );
        table.setModel(
                map.getMapModel(),
                panParam1.getTfOfTable().get(5).getText(),
                map.getMaxChainSize() == -1 ? 1 : map.getMaxChainSize(),
                colWidth
        );
        updateHashtableParameters(true);
        KsGui.oun(taEvents, id + "=" + car, MESSAGES.getString("mapPut"));
    }

    private void readMapParameters() {
        int i = 0;
        List<JTextField> tfs = panParam1.getTfOfTable();

        String[] errorMessages = {"badSizeOfInitialSubSet", "badSizeOfGenSet",
                "badInitialCapacity", "badLoadFactor", "badColumnWidth"};
        //Verification of the correctness of the parameters
        for (JTextField tf : tfs) {
            if (tf.getInputVerifier() != null && !tf.getInputVerifier().verify(tf)) {
                throw new ValidationException(errorMessages[i], tf.getText());
            }
        }

        // When the parameters are correct, we read them
        sizeOfInitialSubSet = Integer.parseInt(tfs.get(i++).getText());
        sizeOfGenSet = Integer.parseInt(tfs.get(i++).getText());
        initialCapacity = Integer.parseInt(tfs.get(i++).getText());
        loadFactor = Float.parseFloat(tfs.get(i++).getText());
        colWidth = Integer.parseInt(tfs.get(i).getText());

        switch (cmbHashFunctions.getSelectedIndex()) {
            case 0:
                ht = HashManager.HashType.DIVISION;
                break;
            case 1:
                ht = HashManager.HashType.MULTIPLICATION;
                break;
            case 2:
                ht = HashManager.HashType.JCF7;
                break;
            case 3:
                ht = HashManager.HashType.JCF;
                break;
            default:
                ht = HashManager.HashType.DIVISION;
                break;
        }
    }

    private void createMap() {
        switch (cmbCollisionTypes.getSelectedIndex()) {
            case 0:
                map = new ParsableHashMap<>(String::new, Car::new, initialCapacity, loadFactor, ht);
                break;
            case 1:
                map = new ParsableHashMapOa<>(String::new, Car::new, initialCapacity, loadFactor, ht, HashMapOa.OpenAddressingType.LINEAR);
                break;
            case 2:
                map = new ParsableHashMapOa<>(String::new, Car::new, initialCapacity, loadFactor, ht, HashMapOa.OpenAddressingType.QUADRATIC);
                break;
            case 3:
                map = new ParsableHashMapOa<>(String::new, Car::new, initialCapacity, loadFactor, ht, HashMapOa.OpenAddressingType.DOUBLE_HASHING);
                break;
            default:
                IntStream.of(1, 2).forEach(p -> panButtons.getButtons().get(p).setEnabled(false));
                throw new ValidationException("notImplemented");
        }
    }

    /**
     * Updates the parameters in the second table.
     * Also checks if the value of the parameter has changed
     * since last time. If it has changed, colours its value red
     *
     * @param colorize determines whether to colour parameter values red, or not
     */
    private void updateHashtableParameters(boolean colorize) {
        String[] parameters = new String[]{
                String.valueOf(map.size()),
                String.valueOf(map.getTableCapacity()),
                String.valueOf(map.getMaxChainSize()),
                String.valueOf(map.getRehashesCounter()),
                String.valueOf(map.getLastUpdated()),
                // Percentage of hash table elements occupied
                String.format("%3.2f", (double) map.getNumberOfOccupied() / map.getTableCapacity() * 100) + "%"
                // .. the new parameters continue here ..
        };
        for (int i = 0; i < parameters.length; i++) {
            String str = panParam2.getTfOfTable().get(i).getText();
            if (!str.equals(parameters[i]) && !str.equals(MESSAGES.getString("notExists")) && !str.equals("") && colorize) {
                panParam2.getTfOfTable().get(i).setForeground(Color.RED);
            } else {
                panParam2.getTfOfTable().get(i).setForeground(Color.BLACK);
            }
            panParam2.getTfOfTable().get(i).setText(parameters[i].equals("-1") ? MESSAGES.getString("notExists") : parameters[i]);
        }
    }


    /**
     * Class for checking the number entered in the JTextField object.
     * Checks whether a non-negative number has been entered
     */
    private static class NotNegativeNumberVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
            try {
                int result = Integer.parseInt(text);
                input.setBackground(result >= 0 ? Color.WHITE : Color.RED);
                return result >= 0;
            } catch (NumberFormatException e) {
                input.setBackground(Color.RED);
                return false;
            }
        }
    }

    public static void createAndShowGUI() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName()
                        // Alternatively, the appearance of the swing components will depend on
                        // the OS used:
                        // UIManager.getSystemLookAndFeelClassName()
                        // Either that:
                        // "com.sun.java.swing.plaf.motif.MotifLookAndFeel"
                        // On Linux, also like this:
                        // "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
                );
                UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Ks.ou(ex.getMessage());
            }
            MainWindow window = new MainWindow();
            window.setIconImage(new ImageIcon(MESSAGES.getString("icon")).getImage());
            window.setTitle(MESSAGES.getString("title"));
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            window.setPreferredSize(new Dimension(1300, 700));
            window.pack();
            window.setVisible(true);
        });
    }
}
