import javax.swing.*;
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
    private DefaultTableModel tableModel;
    private JTextField idField, atField, btField, priorityField;
    private JPanel priorityGanttPanel, srtfGanttPanel;
    private JTable priorityResultTable, srtfResultTable;
    private JLabel priorityAvgLabel, srtfAvgLabel;

    public OSProject() {
        setTitle("OS Project C1: Priority vs SRTF Comparison");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Process Configuration"));
        
        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField(4); inputPanel.add(idField);
        inputPanel.add(new JLabel("Arrival:"));
        atField = new JTextField(4); inputPanel.add(atField);
        inputPanel.add(new JLabel("Burst:"));
        btField = new JTextField(4); inputPanel.add(btField);
        inputPanel.add(new JLabel("Priority:"));
        priorityField = new JTextField(4); inputPanel.add(priorityField);
        
        JButton addButton = new JButton("Add Process");
        JButton runButton = new JButton("Run Comparison");
        JButton clearButton = new JButton("Clear");
        inputPanel.add(addButton); inputPanel.add(runButton); inputPanel.add(clearButton);
        add(inputPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        tableModel = new DefaultTableModel(new String[]{"ID", "Arrival", "Burst", "Priority"}, 0);
        tabbedPane.addTab("Input List", new JScrollPane(new JTable(tableModel)));

        JPanel pPanel = createResultPanel(priorityGanttPanel = new GanttPanel(), priorityResultTable = new JTable(), priorityAvgLabel = new JLabel("Averages:"));
        tabbedPane.addTab("Priority (Preemptive)", pPanel);

        JPanel sPanel = createResultPanel(srtfGanttPanel = new GanttPanel(), srtfResultTable = new JTable(), srtfAvgLabel = new JLabel("Averages:"));
        tabbedPane.addTab("SRTF", sPanel);

        add(tabbedPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> addProcessWithValidation());

        runButton.addActionListener(e -> {
            if (processList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No processes added! Please add at least one process before running.", "Simulation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SimulationResult pRes = simulatePriority(processList);
            SimulationResult sRes = simulateSRTF(processList);

            updateTable(priorityResultTable, pRes);
            priorityAvgLabel.setText(formatAvg(pRes));
            ((GanttPanel)priorityGanttPanel).setGantt(pRes.gantt);

            updateTable(srtfResultTable, sRes);
            srtfAvgLabel.setText(formatAvg(sRes));
            ((GanttPanel)srtfGanttPanel).setGantt(sRes.gantt);
            
            JOptionPane.showMessageDialog(this, "Simulation Complete! Check the tabs for Priority and SRTF results.", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        clearButton.addActionListener(e -> {
            processList.clear(); tableModel.setRowCount(0);
            ((GanttPanel)priorityGanttPanel).setGantt(new ArrayList<>());
            ((GanttPanel)srtfGanttPanel).setGantt(new ArrayList<>());
            idField.setText(""); atField.setText(""); btField.setText(""); priorityField.setText("");
            priorityResultTable.setModel(new DefaultTableModel());
            srtfResultTable.setModel(new DefaultTableModel());
            priorityAvgLabel.setText("Averages:"); srtfAvgLabel.setText("Averages:");
        });
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
            tableModel.addRow(new Object[]{id, at, bt, pr});
            
            idField.setText(""); atField.setText(""); btField.setText(""); priorityField.setText("");
            idField.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Arrival Time, Burst Time, and Priority must be valid whole numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createResultPanel(JPanel gPanel, JTable table, JLabel label) {
        JPanel p = new JPanel(new BorderLayout());
        gPanel.setPreferredSize(new Dimension(800, 120));
        p.add(new JScrollPane(gPanel), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(label, BorderLayout.SOUTH);
        return p;
    }

    private void updateTable(JTable table, SimulationResult res) {
        String[] cols = {"ID", "AT", "BT", "CT", "TAT", "WT", "RT"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Process p : res.processes) model.addRow(new Object[]{p.id, p.at, p.bt, p.completionTime, p.tat, p.wt, p.responseTime});
        table.setModel(model);
    }

    private String formatAvg(SimulationResult r) {
        return String.format("Avg WT: %.2f | Avg TAT: %.2f | Avg RT: %.2f", r.avgWT, r.avgTAT, r.avgRT);
    }

    class GanttPanel extends JPanel {
        List<GanttRecord> gantt = new ArrayList<>();
        void setGantt(List<GanttRecord> g) { this.gantt = g; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (gantt.isEmpty()) return;
            int x = 30, y = 30, h = 40;
            int totalTime = gantt.get(gantt.size()-1).end;
            double scale = (getWidth() - 60.0) / totalTime;

            for (GanttRecord r : gantt) {
                int w = (int)((r.end - r.start) * scale);
                g.setColor(new Color(173, 216, 230)); g.fillRect(x, y, w, h);
                g.setColor(Color.BLACK); g.drawRect(x, y, w, h);
                g.drawString(r.id, x + w/2 - 5, y + 25);
                g.drawString(String.valueOf(r.start), x, y + h + 15);
                x += w;
            }
            g.drawString(String.valueOf(totalTime), x, y + h + 15);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OSProject().setVisible(true));
    }
}