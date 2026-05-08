import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OSProject extends JFrame {

    static class Process {
        String id;
        int at, bt, rt, priority; 
        int startTime = -1, completionTime = 0, wt = 0, tat = 0, responseTime = -1;

        public Process(String id, int at, int bt, int priority) {
            this.id = id;
            this.at = at;
            this.bt = bt;
            this.rt = bt;
            this.priority = priority;
        }

        public Process clone() {
            return new Process(id, at, bt, priority);
        }
    }

    static class GanttRecord {
        String id;
        int start, end;
        public GanttRecord(String id, int start, int end) {
            this.id = id; this.start = start; this.end = end;
        }
    }

    static class SimulationResult {
        List<Process> processes;
        List<GanttRecord> gantt;
        double avgWT, avgTAT, avgRT;

        public SimulationResult(List<Process> p, List<GanttRecord> g) {
            this.processes = p; 
            this.gantt = g;

            double sumWT = 0;
            double sumTAT = 0;
            double sumRT = 0;

            for (Process proc : processes) {
                sumWT += proc.wt;
                sumTAT += proc.tat;
                sumRT += proc.responseTime;
            }

            if (processes.size() > 0) {
                avgWT = sumWT / processes.size();
                avgTAT = sumTAT / processes.size();
                avgRT = sumRT / processes.size();
            } else {
                avgWT = 0;
                avgTAT = 0;
                avgRT = 0;
            }
        }
    }

    public static SimulationResult simulatePriority(List<Process> original) {
        List<Process> processes = new ArrayList<>();
        for (Process p : original) processes.add(p.clone());
        
        List<GanttRecord> gantt = new ArrayList<>();
        int currentTime = 0, completed = 0, n = processes.size();
        String currentRunningId = null;
        int blockStart = 0;

        while (completed != n) {
            Process best = null;
            int minPriority = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.at <= currentTime && p.rt > 0 && p.priority < minPriority) {
                    minPriority = p.priority;
                    best = p;
                }
            }

            if (best == null) {
                if (currentRunningId != null) gantt.add(new GanttRecord(currentRunningId, blockStart, currentTime));
                currentRunningId = null;
                currentTime++; continue;
            }

            if (best.startTime == -1) best.startTime = currentTime;
            if (best.responseTime == -1) best.responseTime = currentTime - best.at;

            if (currentRunningId == null || !currentRunningId.equals(best.id)) {
                if (currentRunningId != null) gantt.add(new GanttRecord(currentRunningId, blockStart, currentTime));
                currentRunningId = best.id; blockStart = currentTime;
            }

            best.rt--; currentTime++;
            if (best.rt == 0) {
                best.completionTime = currentTime;
                best.tat = best.completionTime - best.at;
                best.wt = best.tat - best.bt;
                completed++;
            }
        }
        if (currentRunningId != null) gantt.add(new GanttRecord(currentRunningId, blockStart, currentTime));
        return new SimulationResult(processes, gantt);
    }

    public static SimulationResult simulateSRTF(List<Process> original) {
        List<Process> processes = new ArrayList<>();
        for (Process p : original) processes.add(p.clone());
        
        List<GanttRecord> gantt = new ArrayList<>();
        int currentTime = 0, completed = 0, n = processes.size();
        String currentRunningId = null;
        int blockStart = 0;

        while (completed != n) {
            Process best = null;
            int minRt = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.at <= currentTime && p.rt > 0 && p.rt < minRt) {
                    minRt = p.rt;
                    best = p;
                }
            }

            if (best == null) {
                if (currentRunningId != null) gantt.add(new GanttRecord(currentRunningId, blockStart, currentTime));
                currentRunningId = null;
                currentTime++; continue;
            }

            if (best.startTime == -1) best.startTime = currentTime;
            if (best.responseTime == -1) best.responseTime = currentTime - best.at;

            if (currentRunningId == null || !currentRunningId.equals(best.id)) {
                if (currentRunningId != null) gantt.add(new GanttRecord(currentRunningId, blockStart, currentTime));
                currentRunningId = best.id; blockStart = currentTime;
            }

            best.rt--; currentTime++;
            if (best.rt == 0) {
                best.completionTime = currentTime;
                best.tat = best.completionTime - best.at;
                best.wt = best.tat - best.bt;
                completed++;
            }
        }
        if (currentRunningId != null) gantt.add(new GanttRecord(currentRunningId, blockStart, currentTime));
        return new SimulationResult(processes, gantt);
    }

    private List<Process> processList = new ArrayList<>();
    private JTextField idField, atField, btField, priorityField;
    private DefaultTableModel comparisonTableModel;
    private JTable priorityResultTable, srtfResultTable;
    private JPanel priorityGanttPanel, srtfGanttPanel;

    public OSProject() {
        setTitle("OS Scheduling Simulator - Priority vs SRTF");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Process Configuration"));
        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField(4); inputPanel.add(idField);
        inputPanel.add(new JLabel("Arrival (AT):"));
        atField = new JTextField(4); inputPanel.add(atField);
        inputPanel.add(new JLabel("Burst (BT):"));
        btField = new JTextField(4); inputPanel.add(btField);
        inputPanel.add(new JLabel("Priority (Pri):"));
        priorityField = new JTextField(4); inputPanel.add(priorityField);
        
        JButton addButton = new JButton("Add Process");
        JButton runButton = new JButton("Compare Both");
        JButton clearButton = new JButton("Clear All");
        inputPanel.add(addButton); 
        inputPanel.add(runButton); 
        inputPanel.add(clearButton);

        JPanel scenariosPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        scenariosPanel.setBorder(BorderFactory.createTitledBorder("Test Scenarios"));
        JButton btnScenA = new JButton("Scenario A: Basic Mixed");
        JButton btnScenB = new JButton("Scenario B: Urgency");
        JButton btnScenC = new JButton("Scenario C: Fairness");
        JButton btnScenD = new JButton("Scenario D: Validation Error");

        scenariosPanel.add(btnScenA);
        scenariosPanel.add(btnScenB);
        scenariosPanel.add(btnScenC);
        scenariosPanel.add(btnScenD);

        topContainer.add(inputPanel);
        topContainer.add(scenariosPanel);
        add(topContainer, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] compCols = {"Metric", "Priority", "SRTF", "Winner"};
        Object[][] compData = {
            {"Avg Waiting Time (WT)", "", "", ""},
            {"Avg Turnaround (TAT)", "", "", ""},
            {"Avg Response Time (RT)", "", "", ""}
        };
        comparisonTableModel = new DefaultTableModel(compData, compCols);
        JTable comparisonTable = new JTable(comparisonTableModel);
        comparisonTable.setRowHeight(25);
        JScrollPane compScrollPane = new JScrollPane(comparisonTable);
        compScrollPane.setBorder(BorderFactory.createTitledBorder("Algorithms Comparison Analysis"));
        compScrollPane.setPreferredSize(new Dimension(1200, 120));
        centerPanel.add(compScrollPane);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        priorityResultTable = new JTable();
        JScrollPane pTableScroll = new JScrollPane(priorityResultTable);
        pTableScroll.setBorder(BorderFactory.createTitledBorder("Priority Full Results"));
        
        srtfResultTable = new JTable();
        JScrollPane sTableScroll = new JScrollPane(srtfResultTable);
        sTableScroll.setBorder(BorderFactory.createTitledBorder("SRTF Full Results"));

        tablesPanel.add(pTableScroll);
        tablesPanel.add(sTableScroll);
        tablesPanel.setPreferredSize(new Dimension(1200, 200));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(tablesPanel);

        JPanel ganttContainerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        priorityGanttPanel = new GanttPanel();
        priorityGanttPanel.setBorder(BorderFactory.createTitledBorder("Priority Gantt"));
        
        srtfGanttPanel = new GanttPanel();
        srtfGanttPanel.setBorder(BorderFactory.createTitledBorder("SRTF Gantt"));

        ganttContainerPanel.add(priorityGanttPanel);
        ganttContainerPanel.add(srtfGanttPanel);
        ganttContainerPanel.setPreferredSize(new Dimension(1200, 150));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(ganttContainerPanel);

        add(new JScrollPane(centerPanel), BorderLayout.CENTER);

        addButton.addActionListener(e -> addProcessWithValidation());

        clearButton.addActionListener(e -> clearAll());

        runButton.addActionListener(e -> runSimulation());

        btnScenA.addActionListener(e -> {
            clearAll();
            addProcessRaw("p1", 1, 5, 1);
            addProcessRaw("p2", 2, 4, 2);
            addProcessRaw("p3", 3, 6, 3);
            addProcessRaw("p4", 4, 9, 4);
            addProcessRaw("p5", 5, 10, 5);
            runSimulation();
        });

        btnScenB.addActionListener(e -> {
            clearAll();
            addProcessRaw("p1", 1, 5, 2);
            addProcessRaw("p2", 2, 8, 1);
            addProcessRaw("p3", 3, 2, 5);
            addProcessRaw("p4", 4, 7, 3);
            addProcessRaw("p5", 5, 4, 4);
            runSimulation();
        });

        btnScenC.addActionListener(e -> {
            clearAll();
            addProcessRaw("p1", 1, 4, 1);
            addProcessRaw("p2", 2, 3, 2);
            addProcessRaw("p3", 3, 7, 3);
            addProcessRaw("p4", 4, 8, 4);
            addProcessRaw("p5", 2, 2, 2);
            runSimulation();
        });

        btnScenD.addActionListener(e -> {
            clearAll();
            idField.setText("-5");
            atField.setText("1");
            btField.setText("2");
            priorityField.setText("4");
            addProcessWithValidation(); 
        });
    }

    private void addProcessRaw(String id, int at, int bt, int pr) {
        processList.add(new Process(id, at, bt, pr));
    }

    private void clearAll() {
        processList.clear();
        idField.setText(""); atField.setText(""); btField.setText(""); priorityField.setText("");
        priorityResultTable.setModel(new DefaultTableModel());
        srtfResultTable.setModel(new DefaultTableModel());
        
        for (int i=0; i<3; i++) {
            comparisonTableModel.setValueAt("", i, 1);
            comparisonTableModel.setValueAt("", i, 2);
            comparisonTableModel.setValueAt("", i, 3);
        }

        ((GanttPanel)priorityGanttPanel).setGantt(new ArrayList<>());
        ((GanttPanel)srtfGanttPanel).setGantt(new ArrayList<>());
    }

    private void addProcessWithValidation() {
        String id = idField.getText().trim();
        String atText = atField.getText().trim();
        String btText = btField.getText().trim();
        String prText = priorityField.getText().trim();

        if (id.isEmpty() || atText.isEmpty() || btText.isEmpty() || prText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required. Please fill in ID, Arrival, Burst, and Priority.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!id.matches("[a-zA-Z0-9]+")) {
            JOptionPane.showMessageDialog(this, "Process ID must contain only positive numbers and letters (e.g., 1, P1). No negative signs or symbols.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Process p : processList) {
            if (p.id.equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(this, "Duplicate Process ID '" + id + "'. Please use a unique ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            int at = Integer.parseInt(atText);
            if (at < 0) {
                JOptionPane.showMessageDialog(this, "Arrival Time cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int bt = Integer.parseInt(btText);
            if (bt <= 0) {
                JOptionPane.showMessageDialog(this, "Burst Time must be greater than zero.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int pr = Integer.parseInt(prText);
            if (pr < 0) {
                 JOptionPane.showMessageDialog(this, "Priority value cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            processList.add(new Process(id, at, bt, pr));
            JOptionPane.showMessageDialog(this, "Process " + id + " added successfully.", "Process Added", JOptionPane.INFORMATION_MESSAGE);
            idField.setText(""); atField.setText(""); btField.setText(""); priorityField.setText("");
            idField.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Arrival Time, Burst Time, and Priority must be valid whole numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runSimulation() {
        if (processList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No processes added! Please add at least one process before running.", "Simulation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SimulationResult pRes = simulatePriority(processList);
        SimulationResult sRes = simulateSRTF(processList);

        updateTable(priorityResultTable, pRes);
        updateTable(srtfResultTable, sRes);

        updateComparisonTable(pRes, sRes);

        ((GanttPanel)priorityGanttPanel).setGantt(pRes.gantt);
        ((GanttPanel)srtfGanttPanel).setGantt(sRes.gantt);
    }

    private void updateTable(JTable table, SimulationResult res) {
        String[] cols = {"ID", "AT", "BT", "Pri", "CT", "TAT", "WT", "RT"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Process p : res.processes) {
            model.addRow(new Object[]{p.id, p.at, p.bt, p.priority, p.completionTime, p.tat, p.wt, p.responseTime});
        }
        table.setModel(model);
    }

    private void updateComparisonTable(SimulationResult pRes, SimulationResult sRes) {
        comparisonTableModel.setValueAt(String.format("%.2f", pRes.avgWT), 0, 1);
        comparisonTableModel.setValueAt(String.format("%.2f", sRes.avgWT), 0, 2);
        comparisonTableModel.setValueAt(getWinner(pRes.avgWT, sRes.avgWT), 0, 3);

        comparisonTableModel.setValueAt(String.format("%.2f", pRes.avgTAT), 1, 1);
        comparisonTableModel.setValueAt(String.format("%.2f", sRes.avgTAT), 1, 2);
        comparisonTableModel.setValueAt(getWinner(pRes.avgTAT, sRes.avgTAT), 1, 3);

        comparisonTableModel.setValueAt(String.format("%.2f", pRes.avgRT), 2, 1);
        comparisonTableModel.setValueAt(String.format("%.2f", sRes.avgRT), 2, 2);
        comparisonTableModel.setValueAt(getWinner(pRes.avgRT, sRes.avgRT), 2, 3);
    }

    private String getWinner(double valPriority, double valSRTF) {
        if (valPriority < valSRTF) return "Priority";
        if (valSRTF < valPriority) return "SRTF";
        return "Tie";
    }

    class GanttPanel extends JPanel {
        List<GanttRecord> gantt = new ArrayList<>();
        void setGantt(List<GanttRecord> g) { this.gantt = g; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (gantt.isEmpty()) return;
            int x = 20, y = 30, h = 40;
            int totalTime = gantt.get(gantt.size()-1).end;
            if (totalTime == 0) return;
            double scale = (getWidth() - 60.0) / totalTime;

            for (GanttRecord r : gantt) {
                int w = (int)((r.end - r.start) * scale);
                g.setColor(new Color(173, 216, 230)); 
                g.fillRect(x, y, w, h);
                g.setColor(Color.BLACK); 
                g.drawRect(x, y, w, h);
                g.drawString(r.id, x + w/2 - 5, y + 25);
                g.drawString(String.valueOf(r.start), x, y + h + 15);
                x += w;
            }
            g.drawString(String.valueOf(totalTime), x, y + h + 15);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> new OSProject().setVisible(true));
    }
}
