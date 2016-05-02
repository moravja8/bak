package GUI;

import dbConnectors.DbConnector;
import dbConnectors.HiveConnector;
import knimeEntities.KnimeWorkflow;
import knimeEntities.KnimeWorkflowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by cloudera on 4/10/16.
 */
public class MainFrame {
    private Logger log = LoggerFactory.getLogger(MainFrame.class);

    private JPanel mainPanel;
    private JPanel leftBar;
    private JPanel rightBar;
    private JPanel rightUpBar;
    private JPanel rightDownBar;

    private JComboBox dbCombobox;
    private JComboBox tableCombobox;
    private JComboBox workflowsCombobox;
    private JTextArea customSqlTextarea = new JTextArea();
    private JTextArea outputTextarea = new JTextArea(5, 30);
    private JTextArea costsTextarea = new JTextArea(5, 30);
    private JCheckBox useCustomSqlCheckbox = new JCheckBox();

    private DbConnector db = HiveConnector.getInstance();

    private GridBagConstraints gridBagConstraints = new GridBagConstraints();

    public void init() {
        JFrame frame = new JFrame("MainFrame");


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(640, 480);
        frame.setResizable(false);
        frame.setVisible(true);


        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1,2));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        frame.setContentPane(this.mainPanel);

        leftBar = new JPanel();
        leftBar.setLayout(new GridBagLayout());
        leftBar.setBorder(BorderFactory.createTitledBorder("Input"));
        leftBar.setMinimumSize(new Dimension(350, 480));
        leftBar.setMaximumSize(new Dimension(350, 480));

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(5,5,5,5);

        rightBar = new JPanel();
        rightBar.setLayout(new GridLayout(2,1));

        rightUpBar = new JPanel();
        rightUpBar.setBorder(BorderFactory.createTitledBorder("Output"));
        rightUpBar.setLayout(new BoxLayout(rightUpBar, BoxLayout.Y_AXIS));

        rightDownBar = new JPanel();
        rightDownBar.setBorder(BorderFactory.createTitledBorder("Costs evaluation"));
        rightDownBar.setLayout(new BoxLayout(rightDownBar, BoxLayout.Y_AXIS));

        dbComponentsInit();
        tablesInit();
        workflowsInit();
        useCustomSqlCheckboxInit();
        customSqlTextareaInit();
        runWorkflowButtonInit();
        saveWorkflowButtonInit();
        outputTextareaInit();
        costsTextareaInit();

        rightBar.add(BorderLayout.NORTH, rightUpBar);
        rightBar.add(BorderLayout.SOUTH, rightDownBar);
        mainPanel.add(BorderLayout.EAST, leftBar);
        mainPanel.add(BorderLayout.WEST, rightBar);
        mainPanel.updateUI();
    }

    private void saveWorkflowButtonInit() {
        JButton saveWorkflowButton = new JButton("Save workflow");

        saveWorkflowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outputTextareaRefresh();
                saveWorkflow();
                outputTextarea.append("Saving workflow " + workflowsCombobox.getSelectedItem() + " ... \n");
                outputTextarea.append("Workflow " + workflowsCombobox.getSelectedItem() + " was saved successfully. \n");
            }
        });

        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        leftBar.add(saveWorkflowButton, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }

    private JComboBox initCombobox(JComboBox comboBox){
        comboBox = new JComboBox();
        comboBox.setSize(200, 30);
        return comboBox;
    }

    private void dbComponentsInit(){
        dbCombobox = initCombobox(dbCombobox);

        ResultSet res = db.getDatabases();
        try {
            while(res.next()){
                dbCombobox.addItem(res.getString("database_name"));
            }
        } catch (SQLException e) {
            log.error("The database list was not read correctly.", e);
            outputTextarea.append("The database list was not read correctly. For further information see the log file. \n");
        }

        dbCombobox.setSelectedItem(db.getCurrentDatabase());

        dbCombobox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String dbName = (String) dbCombobox.getSelectedItem();
                db.setCurrentDatabase(dbName);
                tablesRefresh();
            }
        });

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        leftBar.add(new JLabel("Select database:"), gridBagConstraints);

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 1;
        leftBar.add(dbCombobox, gridBagConstraints);
    }

    private void tablesRefresh() {
        tableCombobox.removeAllItems();
        ResultSet res = db.getTables();
        try {
            while(res.next()){
                tableCombobox.addItem(res.getString("tab_name"));
            }
        } catch (SQLException e) {
            log.error("The table list was not read correctly.", e);
            outputTextarea.append("The table list was not read correctly. For further information see the log file. \n");
        }
    }

    private void tablesInit(){
        tableCombobox = initCombobox(tableCombobox);

        tablesRefresh();

        tableCombobox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                customSqlTextareaRefresh();
            }
        });

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        leftBar.add(new JLabel("Select table:"), gridBagConstraints);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 1;
        leftBar.add(tableCombobox, gridBagConstraints);
    }

    private void workflowsInit(){
        workflowsCombobox = initCombobox(workflowsCombobox);

        ArrayList<KnimeWorkflow> workflows = KnimeWorkflowManager.getInstance().getWorkflows();
        for (KnimeWorkflow workflow : workflows) {
            workflowsCombobox.addItem(workflow);
        }

        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 0;
        leftBar.add(new JLabel("Select workflow:"), gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 1;
        leftBar.add(workflowsCombobox, gridBagConstraints);
    }

    private void runWorkflowButtonInit(){
        JButton runWorkflowButton = new JButton("Save & Run workflow");

        runWorkflowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outputTextareaRefresh();
                costsTextareaRefresh();
                KnimeWorkflow knimeWorkflow = (KnimeWorkflow) workflowsCombobox.getSelectedItem();
                saveWorkflow();
                outputTextarea.append("Executing workflow " + workflowsCombobox.getSelectedItem() + " ... \n");
                outputTextarea.update(outputTextarea.getGraphics());
                String costs = knimeWorkflow.runWorkflow((String) dbCombobox.getSelectedItem(), (String) tableCombobox.getSelectedItem());
                outputTextarea.append("Workflow " + workflowsCombobox.getSelectedItem() + " executed successfully. \n");
                outputTextarea.update(outputTextarea.getGraphics());
                costsTextarea.append(costs);
                costsTextarea.update(costsTextarea.getGraphics());
            }
        });

        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        leftBar.add(runWorkflowButton, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }

    private void saveWorkflow(){
        String tableName = (String) tableCombobox.getSelectedItem();
        KnimeWorkflow knimeWorkflow = (KnimeWorkflow) workflowsCombobox.getSelectedItem();
        try {

            //nastavení změněných hodnot
            if(useCustomSqlCheckbox.isSelected()){
                knimeWorkflow.getSqlExecutor().setSQLCode(customSqlTextarea.getText());
                log.info("Setting sql command to: " + customSqlTextarea.getText());
            }else{
                knimeWorkflow.getSqlExecutor().setSQLCode(db.getBasicSelectSQL(tableName));
            }

            //nastavení Hive konektoru
            knimeWorkflow.getHiveConnector().refreshSettings();

            //uložení nastavených hodnot
            knimeWorkflow.saveWorkflow();

        } catch (NullPointerException e) {
            String err = "An error ocured during loading of selected workflos: " + e.getMessage() + "\n";
            outputTextarea.append(err);
            outputTextarea.update(outputTextarea.getGraphics());
            log.error(err, e);
        }
    }

    private void customSqlTextareaRefresh(){
        String tableName = (String) tableCombobox.getSelectedItem();
        customSqlTextarea.setText(db.getBasicSelectSQL(tableName));
    }

    private void customSqlTextareaInit(){
        customSqlTextarea.setVisible(true);
        customSqlTextarea.setEnabled(false);
        customSqlTextarea.setLineWrap (true);
        customSqlTextarea.setMinimumSize(new Dimension(290, 150));
        customSqlTextarea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JScrollPane sp = new JScrollPane(customSqlTextarea);
        sp.setMinimumSize(customSqlTextarea.getMinimumSize());


        customSqlTextareaRefresh();

        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridx = 0;
        leftBar.add(new JLabel("SQL query:"), gridBagConstraints);

        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        leftBar.add(sp, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }

    private void outputTextareaInit(){
        outputTextarea.setVisible(true);
        outputTextarea.setEnabled(false);
        outputTextarea.setLineWrap (true);
        outputTextarea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        outputTextarea.setDisabledTextColor(new Color(50, 50, 255));
        JScrollPane sp = new JScrollPane(outputTextarea);
        rightUpBar.add(sp);
    }

    private void outputTextareaRefresh() {
        outputTextarea.setText("");
        outputTextarea.update(outputTextarea.getGraphics());
    }

    private void costsTextareaInit(){
        costsTextarea.setVisible(true);
        costsTextarea.setEnabled(false);
        costsTextarea.setLineWrap (true);
        costsTextarea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        costsTextarea.setDisabledTextColor(new Color(50, 50, 255));
        JScrollPane sp = new JScrollPane(costsTextarea);
        rightDownBar.add(sp);
    }

    private void costsTextareaRefresh() {
        costsTextarea.setText("");
        costsTextarea.update(costsTextarea.getGraphics());
    }

    private void useCustomSqlCheckboxInit(){
        useCustomSqlCheckbox.setSelected(false);

        useCustomSqlCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(useCustomSqlCheckbox.isSelected()){
                    customSqlTextarea.setEnabled(true);
                }else{
                    customSqlTextarea.setEnabled(false);
                    customSqlTextareaRefresh();
                }
            }
        });

        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridx = 0;
        leftBar.add(new JLabel("Use custom SQL:"), gridBagConstraints);

        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridx = 1;
        leftBar.add(useCustomSqlCheckbox, gridBagConstraints);
    }
}
