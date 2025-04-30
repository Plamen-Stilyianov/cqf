package mc;

/**
 * @(#) HJMmodel.java 1.0 24/08/2007
 *
 * Copyright (c) 2007 Mälardalen University
 * Högskoleplan Box 883, 721 23 Västerås, Sweden.
 * All Rights Reserved.
 *
 * The copyright to the computer program(s) herein
 * is the property of Mälardalen University.
 * The program(s) may be used and/or copied only with
 * the written permission of Mälardalen University
 * or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which
 * the program(s) have been supplied.
 *
 * Description: HJM framework for pricing caps
 * @version 1.0 Aug 07
 * @author Michail Kalavrezos
 * Mail: michail_kalavrezos@yahoo.se
 */


import java.awt.*;
import java.text.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;


public class HJMmodel extends JApplet implements FocusListener, ActionListener, TableModelListener {

    // class variables

    // panels
    private JPanel mainPanel = null;
    private JPanel inputPanel = null;
    private JPanel outputPanel = null;
    private JPanel output1Panel = null;

    private JPanel buttonPanel = null;
    private JPanel button1Panel = null;

    private JPanel dataPanel = null;
    private JPanel data1Panel = null;
    private JPanel button2Panel = null;

    private JPanel button3Panel = null;
    private ChartPanel graphPanel = null;

    // button groups
    private ButtonGroup fileGroup = null;
    private ButtonGroup methodGroup = null;
    private ButtonGroup graphGroup = null;
    private ButtonGroup periodGroup = null;

    // radio buttons
    private JRadioButton onedayButton = null;
    private JRadioButton oneweekButton = null;
    private JRadioButton onemonthButton = null;
    private JRadioButton threemonthsButton = null;
    private JRadioButton sixmonthsButton = null;

    // buttons
    private JButton calculateButton = null;
    private JButton resetButton = null;
    private JButton addButton = null;
    private JButton deleteButton = null;

    // Scroll panes
    private JScrollPane forwardPane = null;

    // table models
    private DefaultTableModel forwardModel = null;

    // tables
    private JTable forwardTable = null;

    // text fields
    // String constants
    private final String ONEDAY = "One day";
    private final String ONEWEEK = "One week";
    private final String ONEMONTH = "One month";
    private final String THREEMONTHS = "Three months";
    private final String SIXMONTHS = "Six months";
    private final String CALCULATE = "Calculate";
    private final String RESET = "Reset";
    private final String INPUT = "Input";
    private final String OUTPUT = "Output";
    private final String SELECT = "Select a row";
    private final String ERROR = "Error";

    // table columns names
    private final String TIME = "Time in years";
    private final String FORWARD = "Forward rate";
    // button names
    private final String ADD = "Add";
    private final String DELETE = "Delete";

    // Texts of labels
    private final String NUMBEROFITERATIONS_LABEL = " Number of Iterations = I";
    private JTextField numberofiterationsField = null;

    private final String PRINCIPAL_LABEL = " Principal";
    private JTextField principalField = null;

    private final String CAPRATE_LABEL = " Cap rate";
    private JTextField caprateField = null;

    private final String SIGMAONE_LABEL = " Sigma One";
    private JTextField sigmaoneField = null;

    private final String SIGMATWO_LABEL = " Sigma Two";
    private JTextField sigmatwoField = null;

    private final String ALPHAONE_LABEL = " Alpha One";
    private JTextField alphaoneField = null;

    private final String ALPHATWO_LABEL = " Alpha Two";
    private JTextField alphatwoField = null;

    private final String CAPPRICE_LABEL = "Cap Price (I)";
    private JTextField cappriceField = null;

    private final String CAPPRICE2_LABEL = "Cap Price (I/10)";
    private JTextField capprice2Field = null;

    private final String CAPPRICE4_LABEL = "Cap Price (I/100)";
    private JTextField capprice4Field = null;

    private final String PERIODSCOVERED_LABEL = "Periods covered (caplets)";
    private JTextField periodscoveredField = null;

    private final String SIMULATIONINTERVALS_LABEL = "Simulation intervals";
    private JTextField simulationintervalsField = null;

    private final String RESETPERIOD_LABEL = "RESET PERIOD";

    // Tooltips
    private final String NUMBEROFITERATIONS_TOOLTIP = "The Recommended value is>10000";
    private final String PRINCIPAL_TOOLTIP = "The initial amount borrowed";
    private final String CAPRATE_TOOLTIP = "Interest rate expressed in percentage points";
    private final String SIGMAONE_TOOLTIP = "Estimated or observed value of sigma one";
    private final String SIGMATWO_TOOLTIP = "Estimated or observed value of sigma two";
    private final String ALPHAONE_TOOLTIP = "Estimated or observed value of alpha one";
    private final String ALPHATWO_TOOLTIP = "Estimated or observed value of alpha two";
    private final String CAPPRICE_TOOLTIP = "DO NOT INSERT ANY VALUE HERE";
    private final String CAPPRICE2_TOOLTIP = "DO NOT INSERT ANY VALUE HERE";

    private final String CAPPRICE4_TOOLTIP = "DO NOT INSERT ANY VALUE HERE";
    private final String SIMULATIONINTERVALS_TOOLTIP = "Integer number of simulation intervals within one reset period";
    private final String PERIODSCOVERED_TOOLTIP = "Insert the integer number of the reset periods covered by the cap";

    //Error messages
    private final String NOT_A_NUMBER = " Enter a number";
    private final String NOT_INTEGER = " Enter an integer number";
    private final String NOT_DOUBLE = " Enter a double number";
    private final String NOT_POSITIVE = "Enter a positive number";

    // numerical constants
    private final int NUMBEROFITERATIONS = 100000;
    private final int PRINCIPAL = 1000000;
    private static double CAPRATE = 4;
    private static double SIGMAONE = 0.02;
    private static double SIGMATWO = 0.03;
    private static double ALPHAONE = 0.022;
    private static double ALPHATWO = 0.017;
    private static double CAPPRICE = 0.0;
    private static double CAPPRICE2 = 0.0;
    private static double CAPPRICE4 = 0.0;

    private int PERIODSCOVERED = 4;
    private int SIMULATIONINTERVALS = 3;

    private final double[] KNOWN_TIME_POINTS = {0.00, 1.00, 2.00, 3.00, 6.00, 10.00};
    private final double[] FORWARD1_RATES = {4.00, 4.51, 4.82, 5.14, 5.54, 5.85};

    // numerical variables
    // numerical variables
    static int r;
    private int numberofiterations = NUMBEROFITERATIONS;
    static int n; //numberofiterations;
    private int principal = PRINCIPAL;

    //static int prin;//principal;
    private double caprate = CAPRATE;

    //static int c;//caprate;
    private static double sigmaone = SIGMAONE;
    private static double sigmatwo = SIGMATWO;
    private static double alphaone = ALPHAONE;
    private static double alphatwo = ALPHATWO;

    private double capprice = CAPPRICE;
    private double capprice2 = CAPPRICE2;
    private double capprice4 = CAPPRICE4;

    private int periodscovered = PERIODSCOVERED;
    static int p;//periodscovered;
    private int simulationintervals = SIMULATIONINTERVALS;
    static int s;//=simulationintervals;

    //boolean variable
    // number formatters
    private DecimalFormat numberFormatter = null;

    private DecimalFormat numberFormatter1 = null;

    // class methods
    // initialising
    public void init() {

        // Initialise formatter
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        numberFormatter = new DecimalFormat("##.#####", symbols);
        numberFormatter1 = new DecimalFormat("##,###.##", symbols);

        // get content pane
        Container contentPane = getContentPane();

        // create main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setLayout(new GridLayout(0, 1));

        // add main panel to content pane
        contentPane.add(mainPanel);

        // create input panel
        inputPanel = new JPanel(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(500, 200));
        inputPanel.setBorder(new TitledBorder(INPUT));

        // add it to the main panel
        mainPanel.add(inputPanel);
        button2Panel = new JPanel();
        button2Panel.setLayout(new BoxLayout(button2Panel, BoxLayout.X_AXIS));
        button2Panel.setPreferredSize(new Dimension(200, 20));

        // add it to the input panel
        inputPanel.add(button2Panel, BorderLayout.NORTH);
        JLabel label = new JLabel(RESETPERIOD_LABEL);
        button2Panel.add(label);

        // create button group
        periodGroup = new ButtonGroup();

        // create oneday button
        onedayButton = new JRadioButton(ONEDAY);
        periodGroup.add(onedayButton);

        // add action listener
        onedayButton.addActionListener(this);

        // add it to button panel
        button2Panel.add(onedayButton);

        // create oneweek button
        oneweekButton = new JRadioButton(ONEWEEK);
        periodGroup.add(oneweekButton);

        // add action listener
        oneweekButton.addActionListener(this);

        // add it to button panel
        button2Panel.add(oneweekButton);

        // create onemonth button
        onemonthButton = new JRadioButton(ONEMONTH);
        periodGroup.add(onemonthButton);
        onemonthButton.setSelected(true);

        // add action listener
        onemonthButton.addActionListener(this);

        // add it to button panel
        button2Panel.add(onemonthButton);
        threemonthsButton = new JRadioButton(THREEMONTHS);
        periodGroup.add(threemonthsButton);

        // add action listener
        threemonthsButton.addActionListener(this);

        // add it to button panel
        button2Panel.add(threemonthsButton);
        sixmonthsButton = new JRadioButton(SIXMONTHS);
        periodGroup.add(sixmonthsButton);

        // add action listener
        sixmonthsButton.addActionListener(this);

        // add it to button panel
        button2Panel.add(sixmonthsButton);

        // create button 1 panel
        button1Panel = new JPanel();

        // add it to the input panel
        inputPanel.add(button1Panel, BorderLayout.SOUTH);

        // create calculate button
        calculateButton = new JButton(CALCULATE);

        // add action listener
        calculateButton.addActionListener(this);

        // add it to button panel
        button1Panel.add(calculateButton);

        //create reset button
        resetButton = new JButton(RESET);

        //add action listener
        resetButton.addActionListener(this);

        // add it to button panel
        button1Panel.add(resetButton);

        // create data panel for the inputs
        dataPanel = new JPanel(new GridLayout(0, 2));
        dataPanel.setPreferredSize(new Dimension(300, 100));

        // add it to input panel
        inputPanel.add(dataPanel, BorderLayout.WEST);

        //create labels,create text field,add focus listener and then add the labels and text field to data panel
        label = new JLabel(PRINCIPAL_LABEL);
        principalField = new JTextField();
        dataPanel.add(label);
        principalField.addFocusListener(this);
        dataPanel.add(principalField);

        label = new JLabel(CAPRATE_LABEL);
        caprateField = new JTextField();
        dataPanel.add(label);
        caprateField.addFocusListener(this);
        dataPanel.add(caprateField);

        label = new JLabel(PERIODSCOVERED_LABEL);
        periodscoveredField = new JTextField();
        dataPanel.add(label);
        periodscoveredField.addFocusListener(this);
        dataPanel.add(periodscoveredField);

        label = new JLabel(SIMULATIONINTERVALS_LABEL);
        simulationintervalsField = new JTextField();
        dataPanel.add(label);
        simulationintervalsField.addFocusListener(this);
        dataPanel.add(simulationintervalsField);

        label = new JLabel(NUMBEROFITERATIONS_LABEL);
        numberofiterationsField = new JTextField();
        dataPanel.add(label);
        numberofiterationsField.addFocusListener(this);
        dataPanel.add(numberofiterationsField);

        label = new JLabel(SIGMAONE_LABEL);
        sigmaoneField = new JTextField();
        dataPanel.add(label);
        sigmaoneField.addFocusListener(this);
        dataPanel.add(sigmaoneField);

        label = new JLabel(ALPHAONE_LABEL);
        alphaoneField = new JTextField();
        dataPanel.add(label);
        alphaoneField.addFocusListener(this);
        dataPanel.add(alphaoneField);

        label = new JLabel(SIGMATWO_LABEL);
        sigmatwoField = new JTextField();
        dataPanel.add(label);
        sigmatwoField.addFocusListener(this);
        dataPanel.add(sigmatwoField);

        label = new JLabel(ALPHATWO_LABEL);
        alphatwoField = new JTextField();
        dataPanel.add(label);
        alphatwoField.addFocusListener(this);
        dataPanel.add(alphatwoField);

        //create data panel
        data1Panel = new JPanel(new BorderLayout());
        data1Panel.setPreferredSize(new Dimension(180, 100));

        // add it to input panel
        inputPanel.add(data1Panel, BorderLayout.EAST);

        // create forward model
        forwardModel = new DefaultTableModel();

        // add columns
        forwardModel.addColumn(TIME);
        forwardModel.addColumn(FORWARD);
        // add rows
        for (int i = 0; i < KNOWN_TIME_POINTS.length; i++) {
            Object[] row = {new Double(KNOWN_TIME_POINTS[i]), new Double(FORWARD1_RATES[i])};

            //new String(numberFormatter.format(FORWARD1_RATES[i]));
            forwardModel.addRow(row);
        }



        // add table model listener
        forwardModel.addTableModelListener(this);

        // create forward table
        forwardTable = new JTable(forwardModel);

        // put it into the scroll pane
        forwardPane = new JScrollPane(forwardTable);

        forwardTable.setPreferredScrollableViewportSize(new Dimension(100, 100));

        // install the custom editors on the columns
        TableColumn col = forwardTable.getColumnModel().getColumn(0);
//        col.setCellEditor(new FirstColumnCellEditor());
        col.setCellEditor(new DefaultCellEditor(new JTextField()));
        col = forwardTable.getColumnModel().getColumn(1);
//        col.setCellEditor(new SecondColumnCellEditor());
        col.setCellEditor(new DefaultCellEditor(new JTextField()));

        // add it to forward panel
        data1Panel.add(forwardPane, BorderLayout.CENTER);
        button3Panel = new JPanel();
        data1Panel.add(button3Panel, BorderLayout.SOUTH);

        // create add button
        addButton = new JButton(ADD);

        // add action listener
        addButton.addActionListener(this);

        // add it to control panel
        button3Panel.add(addButton);

        // create delete button
        deleteButton = new JButton(DELETE);

        // add action listener
        deleteButton.addActionListener(this);

        // add it to control panel
        button3Panel.add(deleteButton);

        // create output panel
        outputPanel = new JPanel();
        outputPanel.setBorder(new TitledBorder(OUTPUT));

        // add it to the main panel
        mainPanel.add(outputPanel);

        // create output1 panel
        output1Panel = new JPanel();
        output1Panel.setLayout(new BoxLayout(output1Panel, BoxLayout.X_AXIS));
        output1Panel.setPreferredSize(new Dimension(480, 20));

        // add it to the output panel
        outputPanel.add(output1Panel, BorderLayout.NORTH);

        // add label and field to output1 panel
        label = new JLabel(CAPPRICE_LABEL);

        cappriceField = new JTextField();

        output1Panel.add(label);
        output1Panel.add(cappriceField);

        // add label and field to outputONE panel
        label = new JLabel(CAPPRICE2_LABEL);

        capprice2Field = new JTextField();

        output1Panel.add(label);
        output1Panel.add(capprice2Field);

        // add label and field to outputONE panel
        label = new JLabel(CAPPRICE4_LABEL);

        capprice4Field = new JTextField();

        output1Panel.add(label);
        output1Panel.add(capprice4Field);

        // create output panel
        graphPanel = new ChartPanel(null);
        graphPanel.setPreferredSize(new Dimension(550, 250));

        // graphPanel.setBorder(new TitledBorder(GRAPH));
        // add it to the main panel
        outputPanel.add(graphPanel, BorderLayout.SOUTH);

        // add tooltip
        numberofiterationsField.setToolTipText(NUMBEROFITERATIONS_TOOLTIP);
        principalField.setToolTipText(PRINCIPAL_TOOLTIP);
        caprateField.setToolTipText(CAPRATE_TOOLTIP);

        // forwardratefileField.setToolTipText(FORWARDRATEFILE_TOOLTIP);
        sigmaoneField.setToolTipText(SIGMAONE_TOOLTIP);
        sigmatwoField.setToolTipText(SIGMATWO_TOOLTIP);
        alphaoneField.setToolTipText(ALPHAONE_TOOLTIP);
        alphatwoField.setToolTipText(ALPHATWO_TOOLTIP);
        cappriceField.setToolTipText(CAPPRICE_TOOLTIP);
        capprice2Field.setToolTipText(CAPPRICE2_TOOLTIP);
        capprice4Field.setToolTipText(CAPPRICE4_TOOLTIP);
        periodscoveredField.setToolTipText(PERIODSCOVERED_TOOLTIP);
        simulationintervalsField.setToolTipText(SIMULATIONINTERVALS_TOOLTIP);

        //set value
//        numberofiterations - Field.setText(numberFormatter.format(NUMBEROFITERATIONS));
        principalField.setText(numberFormatter.format(PRINCIPAL));
        caprateField.setText(numberFormatter.format(CAPRATE));
        sigmaoneField.setText(numberFormatter.format(SIGMAONE));
        sigmatwoField.setText(numberFormatter.format(SIGMATWO));
        alphaoneField.setText(numberFormatter.format(ALPHAONE));
        alphatwoField.setText(numberFormatter.format(ALPHATWO));
        cappriceField.setText(numberFormatter.format(CAPPRICE));
        periodscoveredField.setText(numberFormatter.format(PERIODSCOVERED));
//        simulationintervals - Field.setText(numberFormatter.format(SIMULATIONINTERVALS));
    }

    //method of Action listener
    public void actionPerformed(ActionEvent e) {

        //determine,who called action listener
        Object source = e.getSource();

        if (source == resetButton) {

            //reset all TextFields and variables to the initial values
            numberofiterations = NUMBEROFITERATIONS;
   //         numberofiterations - Field.setText(numberFormatter.format(NUMBEROFITERATIONS));

            principal = PRINCIPAL;
            principalField.setText(numberFormatter.format(PRINCIPAL));

            caprate = CAPRATE;
            caprateField.setText(numberFormatter.format(CAPRATE));

            sigmaone = SIGMAONE;
            sigmaoneField.setText(numberFormatter.format(SIGMAONE));

            alphaone = ALPHAONE;
            alphaoneField.setText(numberFormatter.format(ALPHAONE));

            sigmatwo = SIGMATWO;
            sigmatwoField.setText(numberFormatter.format(SIGMATWO));

            alphatwo = ALPHATWO;
            alphatwoField.setText(numberFormatter.format(ALPHATWO));

            periodscovered = PERIODSCOVERED;
//            periodscovered - Field.setText(numberFormatter.format(PERIODSCOVERED));

            simulationintervals = SIMULATIONINTERVALS;
//            simulationintervals - Field.setText(numberFormatter.format(SIMULATIONINTERVALS));

            capprice = CAPPRICE;
            cappriceField.setText(numberFormatter.format(CAPPRICE));

            capprice2 = CAPPRICE2;
            capprice2Field.setText(numberFormatter.format(CAPPRICE2));

            capprice4 = CAPPRICE4;
            capprice4Field.setText(numberFormatter.format(CAPPRICE4));
        }
        if (source == calculateButton) {

            // read table into memory
            int size = forwardModel.getRowCount();
            double[] knownTimePoints = new double[size];
            double[] forwardRates = new double[size];

            for (int i = 0; i < size; i++) {
                Object result = forwardModel.getValueAt(i, 0);
                knownTimePoints[i] = ((Double) result).doubleValue();
                result = forwardModel.getValueAt(i, 1);
                forwardRates[i] = ((Double) result).doubleValue();
            }

            //Here we call the methods that calculate the cap's price and display the price
            //First we set the reset period value according to the button pressed
            int r = 0;
            if (onedayButton.isSelected()) {
                r = 1;
            }
            if (oneweekButton.isSelected()) {
                r = 7;
            }
            if (onemonthButton.isSelected()) {
                r = 30;
            }
            if (threemonthsButton.isSelected()) {
                r = 90;
            }
            if (sixmonthsButton.isSelected()) {
                r = 180;
            }

            int size1 = (periodscovered * simulationintervals) + 1;

            //here we call the method that creates the relevant time points
            double[] knownTimePoints1 = (new Prohiro().
                    getTimePoints(periodscovered, simulationintervals, r));

            //here we call the method that creates the relevant simulated spot rates points
            double[] forwardRates1 = (new Prohiro().getfHatSpot(periodscovered, simulationintervals, r,
                    sigmaone, sigmatwo, alphaone, alphatwo,
                    numberofiterations, knownTimePoints, forwardRates
            )
            );

            double[] forwardRates2 = (new Prohiro().getfHatSpot(periodscovered, simulationintervals, r,
                    sigmaone, sigmatwo, alphaone, alphatwo, (numberofiterations / 10),
                    knownTimePoints, forwardRates
            )
            );

            double[] forwardRates3 = (new Prohiro().getfHatSpot(periodscovered, simulationintervals, r,
                    sigmaone, sigmatwo, alphaone, alphatwo, (numberofiterations / 100),
                    knownTimePoints, forwardRates
            )
            );

            //here we call the method that gives the present value of the cap
            cappriceField.setText(numberFormatter1.format((new Prohiro().getPrice(forwardRates1, periodscovered, r, simulationintervals, caprate) * principal)));

            capprice2Field.setText(numberFormatter1.format((new Prohiro().getPrice(forwardRates2, periodscovered, r, simulationintervals, caprate) * principal)));

            //capprice4Field.setText(numberFormatter.format((new Prohiro().getPrice(forwardRates3,periodscovered,r,simulationintervals,caprate)*principal)));

            capprice4Field.setText(numberFormatter1.format((new Prohiro().getPrice(forwardRates3, periodscovered, r, simulationintervals, caprate) * principal)));

            // Here we show the results graphically
            // create dataset
            XYSeriesCollection dataset = new XYSeriesCollection();

            // create series
            XYSeries forwardSeries = new XYSeries("Forward rate");
            XYSeries caprateSeries = new XYSeries("Cap rate");
            XYSeries simulationSeries = new XYSeries("Iterations=I ");
            XYSeries simulation2Series = new XYSeries("Iterations=I/2 ");
            XYSeries simulation3Series = new XYSeries("Iterations=I/4 ");

            double minResult = Double.POSITIVE_INFINITY;
            double maxResult = Double.parseDouble(caprateField.getText());

            // fill series
            for (int i = 0; i < size1; i++) {

                //forwardSeries.add(knownTimePoints[i], forwardRates[i]);
                caprateSeries.add(knownTimePoints1[i], Double.parseDouble(caprateField.getText()));
                simulationSeries.add(knownTimePoints1[i], forwardRates1[i]);
                simulation2Series.add(knownTimePoints1[i], forwardRates2[i]);
                simulation3Series.add(knownTimePoints1[i], forwardRates3[i]);
                if (forwardRates1[i] < minResult) minResult = forwardRates1[i];
                if (forwardRates1[i] > maxResult) maxResult = forwardRates1[i];
            }

            // add series to data set
            dataset.addSeries(simulationSeries);
            dataset.addSeries(simulation2Series);
            dataset.addSeries(simulation3Series);
            dataset.addSeries(caprateSeries);
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "STIMATED SPOT RATES", // chart title
                    "Time (years)", // x axis label
                    "Interest rate (%)", // y axis label
                    dataset, // data
                    PlotOrientation.VERTICAL,
                    true, // include legend
                    true, // tooltips
                    false // urls
            );
            // change y axis
            XYPlot plot = (XYPlot) chart.getPlot();
            ValueAxis rangeAxis = plot.getRangeAxis();
            rangeAxis.setRange(minResult - 0.2, maxResult + 0.2);

            // show graph
            graphPanel.setChart(chart);
            graphPanel.setVisible(true);
        }

        // if add button
        if (source == addButton) {
            // add line
            int rowNumber = forwardTable.getSelectedRow();
            if (rowNumber == -1) {
                JOptionPane.showMessageDialog(null, SELECT, ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                double date = ((Double) forwardModel.getValueAt(rowNumber, 0)).doubleValue();
                double rate = ((Double) forwardModel.getValueAt(rowNumber, 1)).doubleValue();
                Object[] row = {new Double(date + 0.01), new Double(rate)};
                forwardModel.insertRow(++rowNumber, row);
            }
            return;
        }
        if (source == deleteButton) {
            // delete line
            int rowNumber = forwardTable.getSelectedRow();

            if (rowNumber == -1) {
                JOptionPane.showMessageDialog(null, SELECT, ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                forwardModel.removeRow(rowNumber);
            }

            return;
        }

    }

    //if focus is gained,do nothing
    public void focusGained(FocusEvent e) {
    }

    //if focus is lost, do something
    public void focusLost(FocusEvent e) {
        //find the source which called focus lost
        Object source = e.getSource();

        //if the source is numberofiterations
        if (source == numberofiterationsField) {
            numberofiterations = readInt(numberofiterationsField, numberofiterations, "Number of Iterations");
            return;
        }

        //if the source is the simulation intervals
        if (source == simulationintervalsField) {
            simulationintervals = readInt(simulationintervalsField, simulationintervals, "Simulation Intervals Field");
            return;
        }

        //if the source is the periods covered
        if (source == periodscoveredField) {
            periodscovered = readInt(periodscoveredField, periodscovered, "Periods covered field");
            return;
        }

        //if the source is the principal
        if (source == principalField) {
            principal = readInt(principalField, principal, "Principal");
            return;
        }

        //if the source is the sigmaone
        if (source == sigmaoneField) {
            sigmaone = readPositive(sigmaoneField, sigmaone, "Sigma One Field");
            return;
        }

        //if the source is sigmatwo
        if (source == sigmatwoField) {
            sigmatwo = readPositive(sigmatwoField, sigmatwo, "Sigma Two Field");
            return;
        }

        //if the source is alphaone
        if (source == alphaoneField) {
            alphaone = readPositive(alphaoneField, alphaone, "Alpha One Field");
            return;
        }

        //if the source is alphatwo
        if (source == alphatwoField) {
            alphatwo = readPositive(alphatwoField, alphatwo, "Alpha Two Field");
            return;
        }

        //if the source is cap rate
        if (source == caprateField) {
            caprate = readPositive(caprateField,
                    caprate,
                    "Cap rate field");
            return;
        }
    }

    //read positive double numbers
    private double readPositive(JTextField field, double oldValue, String title) {

        boolean isOK = true;
        double newValue = 1;
        try { //test input
            newValue = Double.parseDouble(field.getText());
        }
        catch (NumberFormatException e) {//Error message
            JOptionPane.showMessageDialog(null,
                    NOT_A_NUMBER,
                    title,
                    JOptionPane.ERROR_MESSAGE);
            isOK = false;
        }

        if (newValue <= 0) {//ERROR message
            JOptionPane.showMessageDialog(null,
                    NOT_POSITIVE,
                    title,
                    JOptionPane.ERROR_MESSAGE);
            isOK = false;
        }
        if (isOK) {
            return newValue;
        } else {
            field.setText(numberFormatter.format(oldValue));
            return oldValue;
        }
    }

    //read double numbers
    private double readDouble(JTextField field, double oldValue, String title) {
        boolean isOK = true;
        double newValue = 1;
        try {// test input
            newValue = Double.parseDouble(field.getText());
        }
        catch (NumberFormatException e) {// ERROR message
            JOptionPane.showMessageDialog(null,
                    NOT_A_NUMBER,
                    title,
                    JOptionPane.ERROR_MESSAGE);
            isOK = false;
        }
        if (isOK) {
            return newValue;
        } else {
            field.setText(numberFormatter.format(oldValue));
            return oldValue;
        }
    }

    //Read integer numbers
    private int readInt(JTextField field, int oldValue, String title) {
        boolean isOK = true;
        int newValue = 1;
        try { // test input
            newValue = Integer.parseInt(field.getText());
        }
        catch (NumberFormatException e) {// ERROR message
            JOptionPane.showMessageDialog(null, NOT_INTEGER, title, JOptionPane.ERROR_MESSAGE);
            isOK = false;
        }
        if (newValue <= 0) {//ERROR message
            JOptionPane.showMessageDialog(null, NOT_POSITIVE, title, JOptionPane.ERROR_MESSAGE);
            isOK = false;
        }
        if (isOK) {
            return newValue;
        } else {
            field.setText(numberFormatter.format(oldValue));
            return oldValue;
        }
    }

    // public static void main(String[] args){
    //}

    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
    }
}
