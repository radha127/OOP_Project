
// HomeGUI.java - SWING GUI (MAIN CLASS)
// This is the entry point of the application
// =====================================================

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class HomeGUI {

    // ---- Main application window ----
    private JFrame frame;

    // ---- The SmartHome object (holds all devices) ----
    private SmartHome home;

    // ---- Status bar at bottom ----
    private JLabel statusLabel;

    // ---- Sidebar panel for Rooms ----
    private JPanel sidebarPanel;

    // ---- Grid panel for Device Cards ----
    private JPanel cardGridPanel;

    // ---- Active room filter ----
    private String selectedRoom = "All Rooms";

    // ---- Theme Colors (Option 4: Glassmorphism Theme) ----
    private static final Color GRADIENT_START = new Color(46, 16, 101);   // Deep Purple
    private static final Color GRADIENT_END   = new Color(30, 58, 138);   // Deep Blue
    private static final Color CARD_GLASS     = new Color(255, 255, 255, 30); // Frosted Glass
    private static final Color BORDER_GLASS   = new Color(255, 255, 255, 60); // Glass Border
    private static final Color TEXT_LIGHT     = new Color(248, 250, 252);
    private static final Color TEXT_MUTED     = new Color(203, 213, 225);
    
    // Solid colors for dialogs
    private static final Color BG_DARK       = new Color(15, 23, 42); 
    private static final Color CARD_DARK     = new Color(30, 41, 59); 
    private static final Color BORDER_DARK   = new Color(51, 65, 85); 

    private static final Color ACCENT_CYAN    = new Color(34, 211, 238);   // Neon Cyan
    private static final Color ACCENT_GREEN   = new Color(16, 185, 129);   // Emerald
    private static final Color ACCENT_RED     = new Color(244, 63, 94);    // Rose
    private static final Color ACCENT_GLASS   = new Color(255, 255, 255, 40); // Button bg

    // =====================================================
    // MAIN METHOD - Program starts here
    // =====================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomeGUI app = new HomeGUI();
            app.start();
        });
    }

    // =====================================================
    // START - Initialize home and show GUI
    // =====================================================
    public void start() {
        // Apply Glass theme colors to standard dialog popups (OptionPanes)
        UIManager.put("OptionPane.background", CARD_DARK);
        UIManager.put("OptionPane.messageForeground", TEXT_LIGHT);
        UIManager.put("Panel.background", CARD_DARK);
        UIManager.put("Label.foreground", TEXT_LIGHT);
        UIManager.put("Button.background", ACCENT_GLASS);
        UIManager.put("Button.foreground", TEXT_LIGHT);

        // Ask user if they want to load from file
        if (FileHandler.hasSavedFile()) {
            int choice = JOptionPane.showConfirmDialog(
                null,
                "A saved home was found. Would you like to load it?",
                "Load Saved Data",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                home = FileHandler.loadHome();
            }
        }

        // If no file, or user said No, create a fresh home with sample data
        if (home == null) {
            home = new SmartHome("My Smart Home");
        }

        // Build and show the GUI
        buildMainWindow();
    }

    // =====================================================
    // BUILD THE MAIN WINDOW
    // =====================================================
    private void buildMainWindow() {
        frame = new JFrame(home.getHomeName() + " - Smart Home Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(940, 640);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setLayout(new BorderLayout(10, 10));

        // --- GRADIENT BACKGROUND ---
        JPanel mainContent = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, 0, getHeight(), GRADIENT_END);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        mainContent.setOpaque(false);
        frame.setContentPane(mainContent);

        // --- Build sections ---
        mainContent.add(buildHeader(),   BorderLayout.NORTH);
        mainContent.add(buildCenter(),   BorderLayout.CENTER);
        mainContent.add(buildFooter(),   BorderLayout.SOUTH);

        // Initial UI load
        refreshUI();

        frame.setVisible(true);
    }

    // =====================================================
    // HEADER PANEL - Title + Stats
    // =====================================================
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(CARD_GLASS);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GLASS),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Title label
        JLabel title = new JLabel(home.getHomeName().toUpperCase());
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_LIGHT);

        // Stats label (right side)
        statusLabel = new JLabel(getStatsText());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(TEXT_MUTED);

        header.add(title,       BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);

        return header;
    }

    // =====================================================
    // CENTER PANEL - Sidebar + Card Grid
    // =====================================================
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        // --- Top Bar (Buttons) ---
        center.add(buildButtonPanel(), BorderLayout.NORTH);

        // --- Left Sidebar (Rooms) ---
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_GLASS);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        sidebarPanel.setOpaque(false);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(180, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GLASS, 1, true),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        center.add(sidebarPanel, BorderLayout.WEST);

        // --- Center Card Grid ---
        cardGridPanel = new JPanel(new GridLayout(0, 2, 16, 16));
        cardGridPanel.setOpaque(false);
        cardGridPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JScrollPane scroll = new JScrollPane(cardGridPanel);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        center.add(scroll, BorderLayout.CENTER);

        return center;
    }
    // BUTTON PANEL - Action Buttons
    private JPanel buildButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnPanel.setOpaque(false);

        // --- Styled Buttons ---
        JButton btnAdd    = new StyledButton("Add Device",    ACCENT_GLASS);
        JButton btnSave   = new StyledButton("Save Home",     ACCENT_GLASS);
        JButton btnLoad   = new StyledButton("Load Home",     ACCENT_GLASS);

        // ADD DEVICE
        btnAdd.addActionListener(e -> showAddDeviceDialog());

        // SAVE TO FILE
        btnSave.addActionListener(e -> {
            FileHandler.saveHome(home);
            showStatus("Home saved to file successfully!");
        });

        // LOAD FROM FILE
        btnLoad.addActionListener(e -> {
            SmartHome loaded = FileHandler.loadHome();
            if (loaded != null) {
                home = loaded;
                selectedRoom = "All Rooms";
                refreshUI();
                showStatus("Home loaded from file!");
            } else {
                showStatus("No saved file found.");
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(Box.createHorizontalStrut(15));
        btnPanel.add(btnSave);
        btnPanel.add(btnLoad);

        return btnPanel;
    }
    // REFRESH UI - Updates Sidebar, Grid, and Header Stats
    private void refreshUI() {
        refreshSidebar();
        refreshCardGrid();
        updateStats();
    }

    // Refresh Room Sidebar
    private void refreshSidebar() {
        sidebarPanel.removeAll();

        // Section Title
        JLabel titleLabel = new JLabel("ROOMS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(TEXT_MUTED);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        sidebarPanel.add(titleLabel);

        // "All Rooms" tab
        addSidebarButton("All Rooms");

        // Dynamically add unique room tabs
        for (String room : home.getRooms()) {
            addSidebarButton(room);
        }

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    // Helper: Add button to sidebar
    private void addSidebarButton(String roomName) {
        String displayText = roomName;
        if (roomName.equals("All Rooms")) displayText = "🏠 All Rooms";
        else if (roomName.equalsIgnoreCase("Living Room")) displayText = "🛋️ " + roomName;
        else if (roomName.equalsIgnoreCase("Bedroom")) displayText = "🛏️ " + roomName;
        else if (roomName.equalsIgnoreCase("Kitchen")) displayText = "🍳 " + roomName;
        else if (roomName.equalsIgnoreCase("Garage")) displayText = "🚗 " + roomName;
        else if (roomName.equalsIgnoreCase("Outdoor")) displayText = "🌳 " + roomName;
        else displayText = "📍 " + roomName;

        JButton btn = new JButton(displayText) {
            @Override
            protected void paintComponent(Graphics g) {
                if (selectedRoom.equals(roomName)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillRoundRect(5, 0, getWidth()-10, getHeight(), 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(selectedRoom.equals(roomName) ? Color.WHITE : TEXT_MUTED);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        btn.addActionListener(e -> {
            selectedRoom = roomName;
            refreshUI();
        });

        sidebarPanel.add(btn);
    }

    // Refresh Devices Grid
    private void refreshCardGrid() {
        cardGridPanel.removeAll();

        ArrayList<SmartDevice> devicesToDisplay;
        if (selectedRoom.equals("All Rooms")) {
            devicesToDisplay = home.getAllDevices();
        } else {
            devicesToDisplay = home.getDevicesByRoom(selectedRoom);
        }

        // Render card for each device
        for (SmartDevice d : devicesToDisplay) {
            cardGridPanel.add(new DeviceCardPanel(d));
        }

        // Placeholders to keep grid alignment consistent if there are < 4 devices
        int count = devicesToDisplay.size();
        if (count < 4) {
            for (int i = 0; i < 4 - count; i++) {
                JPanel placeholder = new JPanel();
                placeholder.setOpaque(false);
                cardGridPanel.add(placeholder);
            }
        }

        cardGridPanel.revalidate();
        cardGridPanel.repaint();
    }
    // ADD DEVICE DIALOG
    private void showAddDeviceDialog() {
        JDialog dialog = new JDialog(frame, "Add New Device", true);
        dialog.setSize(440, 400);
        dialog.setLocationRelativeTo(frame);
        dialog.getContentPane().setBackground(CARD_DARK);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(CARD_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField txtId   = createStyledTextField();
        JTextField txtName = createStyledTextField();

        JComboBox<String> cmbRoom = new JComboBox<>();
        cmbRoom.setEditable(true);
        cmbRoom.setBackground(CARD_DARK);
        cmbRoom.setForeground(TEXT_LIGHT);
        cmbRoom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRoom.setBorder(BorderFactory.createLineBorder(BORDER_DARK));
        for (String room : home.getRooms()) {
            cmbRoom.addItem(room);
        }

        String[] types = {"Light", "Fan", "AC", "Camera"};
        JComboBox<String> cmbType = new JComboBox<>(types);
        cmbType.setBackground(CARD_DARK);
        cmbType.setForeground(TEXT_LIGHT);
        cmbType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbType.setBorder(BorderFactory.createLineBorder(BORDER_DARK));

        JTextField txtExtra1 = createStyledTextField();
        txtExtra1.setText("80");
        JLabel lblExtra1 = createLabel("Brightness (0-100):");

        // AC Mode Slider
        JSlider modeSlider = new JSlider(JSlider.HORIZONTAL, 0, 2, 0);
        modeSlider.setMajorTickSpacing(1);
        modeSlider.setPaintTicks(true);
        modeSlider.setPaintLabels(true);
        modeSlider.setSnapToTicks(true);
        modeSlider.setBackground(CARD_DARK);
        modeSlider.setForeground(TEXT_LIGHT);
        modeSlider.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
        labelTable.put(0, makeSliderLabel("Cool",  new Color(96, 165, 250)));
        labelTable.put(1, makeSliderLabel("Auto",  new Color(52, 211, 153)));
        labelTable.put(2, makeSliderLabel("Heat",  new Color(248, 113, 113)));
        modeSlider.setLabelTable(labelTable);

        JLabel modeValueLabel = new JLabel("Mode: Cool", SwingConstants.CENTER);
        modeValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        modeValueLabel.setForeground(new Color(96, 165, 250));

        modeSlider.addChangeListener(e -> {
            String[] modeNames  = {"Cool", "Auto", "Heat"};
            Color[]  modeColors = {new Color(96, 165, 250), new Color(52, 211, 153), new Color(248, 113, 113)};
            int val = modeSlider.getValue();
            modeValueLabel.setText("Mode: " + modeNames[val]);
            modeValueLabel.setForeground(modeColors[val]);
        });

        JLabel lblMode = createLabel("AC Mode:");
        
        JPanel sliderPanel = new JPanel(new BorderLayout(4, 2));
        sliderPanel.setBackground(CARD_DARK);
        sliderPanel.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1, true));
        sliderPanel.add(modeSlider,     BorderLayout.CENTER);
        sliderPanel.add(modeValueLabel, BorderLayout.SOUTH);

        lblMode.setVisible(false);
        sliderPanel.setVisible(false);

        cmbType.addActionListener(e -> {
            String t = (String) cmbType.getSelectedItem();
            if (t.equals("Light"))  { lblExtra1.setText("Brightness (0-100):"); txtExtra1.setText("80"); }
            if (t.equals("Fan"))    { lblExtra1.setText("Speed (1-5):");         txtExtra1.setText("3"); }
            if (t.equals("AC"))     { lblExtra1.setText("Temperature (16-30):"); txtExtra1.setText("22"); }
            if (t.equals("Camera")) { lblExtra1.setText("(No extra setting)");   txtExtra1.setText("-"); }
            
            boolean isAC = t.equals("AC");
            lblMode.setVisible(isAC);
            sliderPanel.setVisible(isAC);
            dialog.revalidate();
            dialog.repaint();
        });

        gbc.gridx = 0; gbc.gridy = 0; mainPanel.add(createLabel("Device ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; mainPanel.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; mainPanel.add(createLabel("Device Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; mainPanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; mainPanel.add(createLabel("Room:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; mainPanel.add(cmbRoom, gbc);

        gbc.gridx = 0; gbc.gridy = 3; mainPanel.add(createLabel("Device Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; mainPanel.add(cmbType, gbc);

        gbc.gridx = 0; gbc.gridy = 4; mainPanel.add(lblExtra1, gbc);
        gbc.gridx = 1; gbc.gridy = 4; mainPanel.add(txtExtra1, gbc);

        gbc.gridx = 0; gbc.gridy = 5; mainPanel.add(lblMode, gbc);
        gbc.gridx = 1; gbc.gridy = 5; mainPanel.add(sliderPanel, gbc);

        JButton btnConfirm = new StyledButton("Add Device", ACCENT_GREEN);
        JButton btnCancel  = new StyledButton("Cancel", ACCENT_GLASS);

        btnConfirm.addActionListener(e -> {
            try {
                String id   = txtId.getText().trim();
                String name = txtName.getText().trim();
                Object selectedRoomObj = cmbRoom.getSelectedItem();
                String room = selectedRoomObj != null ? selectedRoomObj.toString().trim() : "";
                String type = (String) cmbType.getSelectedItem();

                if (id.isEmpty() || name.isEmpty() || room.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
                    return;
                }

                String[] modeNames = {"Cool", "Auto", "Heat"};
                String selectedMode = modeNames[modeSlider.getValue()];

                SmartDevice newDevice = null;
                if (type.equals("Light")) {
                    newDevice = new Light(id, name, room, Integer.parseInt(txtExtra1.getText()));
                } else if (type.equals("Fan")) {
                    newDevice = new Fan(id, name, room, Integer.parseInt(txtExtra1.getText()));
                } else if (type.equals("AC")) {
                    newDevice = new AirConditioner(
                        id, name, room,
                        Double.parseDouble(txtExtra1.getText()),
                        selectedMode
                    );
                } else if (type.equals("Camera")) {
                    newDevice = new SecurityCamera(id, name, room);
                }

                home.addDevice(newDevice);
                refreshUI();
                showStatus(name + " added successfully!");
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format. Please check your inputs.");
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel buttonContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonContainer.setBackground(CARD_DARK);
        buttonContainer.add(btnConfirm);
        buttonContainer.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 6, 6, 6);
        mainPanel.add(buttonContainer, gbc);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // =====================================================
    // EDIT DEVICE DIALOG
    // =====================================================
    private void showEditDeviceDialog(SmartDevice device) {
        JDialog dialog = new JDialog(frame, "Edit Device", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(frame);
        dialog.getContentPane().setBackground(CARD_DARK);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(CARD_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField txtName = createStyledTextField();
        txtName.setText(device.getName());

        JComboBox<String> cmbRoom = new JComboBox<>();
        cmbRoom.setEditable(true);
        cmbRoom.setBackground(CARD_DARK);
        cmbRoom.setForeground(TEXT_LIGHT);
        cmbRoom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRoom.setBorder(BorderFactory.createLineBorder(BORDER_DARK));
        for (String room : home.getRooms()) {
            cmbRoom.addItem(room);
        }
        cmbRoom.setSelectedItem(device.getRoom());

        gbc.gridx = 0; gbc.gridy = 0; mainPanel.add(createLabel("Device Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; mainPanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; mainPanel.add(createLabel("Room:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; mainPanel.add(cmbRoom, gbc);

        JButton btnConfirm = new StyledButton("Save Changes", ACCENT_GREEN);
        JButton btnCancel  = new StyledButton("Cancel", ACCENT_GLASS);

        btnConfirm.addActionListener(e -> {
            String newName = txtName.getText().trim();
            Object selectedRoomObj = cmbRoom.getSelectedItem();
            String newRoom = selectedRoomObj != null ? selectedRoomObj.toString().trim() : "";

            if (newName.isEmpty() || newRoom.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
                return;
            }

            device.setName(newName);
            device.setRoom(newRoom);
            
            refreshUI();
            showStatus(newName + " updated successfully!");
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel buttonContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonContainer.setBackground(CARD_DARK);
        buttonContainer.add(btnConfirm);
        buttonContainer.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 6, 6, 6);
        mainPanel.add(buttonContainer, gbc);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // Helper: create styled labels
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_LIGHT);
        return lbl;
    }

    // Helper: create styled text fields
    private JTextField createStyledTextField() {
        JTextField tf = new JTextField(15);
        tf.setBackground(BG_DARK);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_DARK, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    // Helper: create a coloured label for the slider tick marks
    private JLabel makeSliderLabel(String text, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(color);
        return lbl;
    }

    // =====================================================
    // FOOTER - Bottom status bar
    // =====================================================
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(CARD_GLASS);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_GLASS),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        JLabel footerLabel = new JLabel("Smart Home Manager | OOP Project");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(TEXT_MUTED);

        footer.add(footerLabel, BorderLayout.WEST);
        return footer;
    }

    // =====================================================
    // HELPER: Update stats in header
    // =====================================================
    private String getStatsText() {
        return "Total: " + home.getTotalDevices() + " devices | Active: " + home.getActiveDevices() + " ON";
    }

    private void updateStats() {
        if (statusLabel != null) {
            statusLabel.setText(getStatsText());
        }
    }

    // =====================================================
    // HELPER: Show a status message
    // =====================================================
    private void showStatus(String message) {
        JOptionPane.showMessageDialog(frame, message, "Status", JOptionPane.INFORMATION_MESSAGE);
        updateStats();
    }

    // =====================================================
    // CUSTOM COMPONENT: Styled Rounded Button
    // =====================================================
    private static class StyledButton extends JButton {
        private final Color baseColor;
        private final Color hoverColor;
        private boolean isHovered = false;

        public StyledButton(String text, Color baseColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = new Color(
                Math.min(255, baseColor.getRed() + 24),
                Math.min(255, baseColor.getGreen() + 24),
                Math.min(255, baseColor.getBlue() + 24)
            );
            
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isHovered) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(baseColor);
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // =====================================================
    // CUSTOM COMPONENT: Device Card Panel
    // Represents one smart device card in the grid
    // =====================================================
    private class DeviceCardPanel extends JPanel {

        public DeviceCardPanel(SmartDevice device) {
            
            setLayout(new BorderLayout(10, 10));
            setOpaque(false);
            
            // Highlight card border if device is ON (Neon Cyan for Glassmorphism)
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(device.isOn() ? ACCENT_CYAN : BORDER_GLASS, device.isOn() ? 2 : 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
            ));

            // --- Top Row: Name, Room, and Quick Toggle Switch ---
            JPanel topRow = new JPanel(new BorderLayout(5, 5));
            topRow.setOpaque(false);

            JLabel nameLabel = new JLabel(device.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setForeground(TEXT_LIGHT);

            JLabel roomLabel = new JLabel(device.getRoom());
            roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            roomLabel.setForeground(TEXT_MUTED);

            JPanel textContainer = new JPanel(new GridLayout(2, 1, 2, 2));
            textContainer.setOpaque(false);
            textContainer.add(nameLabel);
            textContainer.add(roomLabel);

            // Directly clickable ON/OFF Button
            JButton toggleBtn = new StyledButton(device.isOn() ? "ON" : "OFF", device.isOn() ? ACCENT_GREEN : ACCENT_RED);
            toggleBtn.setPreferredSize(new Dimension(60, 30));
            toggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            toggleBtn.addActionListener(e -> {
                device.toggle();
                refreshCardGrid(); // Refresh grid layout live
                updateStats();     // Refresh header stats
            });

            topRow.add(textContainer, BorderLayout.WEST);
            topRow.add(toggleBtn, BorderLayout.EAST);
            add(topRow, BorderLayout.NORTH);

            // --- Center Panel: Specific device adjustments (Polymorphism) ---
            JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
            centerPanel.setOpaque(false);

            if (device instanceof Light) {
                Light light = (Light) device;
                JLabel valLabel = new JLabel("Brightness: " + light.getBrightness() + "%");
                valLabel.setForeground(TEXT_LIGHT);
                valLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                JSlider slider = new JSlider(0, 100, light.getBrightness());
                slider.setOpaque(false);
                slider.setForeground(ACCENT_CYAN);
                slider.setEnabled(device.isOn());
                slider.addChangeListener(e -> {
                    light.setBrightness(slider.getValue());
                    valLabel.setText("Brightness: " + light.getBrightness() + "%");
                });

                centerPanel.add(valLabel, BorderLayout.NORTH);
                centerPanel.add(slider, BorderLayout.CENTER);

            } else if (device instanceof Fan) {
                Fan fan = (Fan) device;
                JLabel valLabel = new JLabel("Speed: " + fan.getSpeed() + " / 5");
                valLabel.setForeground(TEXT_LIGHT);
                valLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                JSlider slider = new JSlider(1, 5, fan.getSpeed());
                slider.setOpaque(false);
                slider.setForeground(ACCENT_CYAN);
                slider.setSnapToTicks(true);
                slider.setPaintTicks(true);
                slider.setMajorTickSpacing(1);
                slider.setEnabled(device.isOn());
                slider.addChangeListener(e -> {
                    fan.setSpeed(slider.getValue());
                    valLabel.setText("Speed: " + fan.getSpeed() + " / 5");
                });

                centerPanel.add(valLabel, BorderLayout.NORTH);
                centerPanel.add(slider, BorderLayout.CENTER);

            } else if (device instanceof AirConditioner) {
                AirConditioner ac = (AirConditioner) device;
                JLabel valLabel = new JLabel("Temp: " + ac.getTemperature() + "°C | Mode: " + ac.getMode());
                valLabel.setForeground(TEXT_LIGHT);
                valLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                JPanel acControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                acControls.setOpaque(false);

                JButton decBtn = new StyledButton("-", ACCENT_GLASS);
                decBtn.setPreferredSize(new Dimension(32, 28));
                decBtn.setMargin(new Insets(0, 0, 0, 0));
                decBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                decBtn.setEnabled(device.isOn());

                JButton incBtn = new StyledButton("+", ACCENT_GLASS);
                incBtn.setPreferredSize(new Dimension(32, 28));
                incBtn.setMargin(new Insets(0, 0, 0, 0));
                incBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                incBtn.setEnabled(device.isOn());

                decBtn.addActionListener(e -> {
                    ac.setTemperature(ac.getTemperature() - 1.0);
                    valLabel.setText("Temp: " + ac.getTemperature() + "°C | Mode: " + ac.getMode());
                });

                incBtn.addActionListener(e -> {
                    ac.setTemperature(ac.getTemperature() + 1.0);
                    valLabel.setText("Temp: " + ac.getTemperature() + "°C | Mode: " + ac.getMode());
                });

                String[] modes = {"Cool", "Auto", "Heat"};
                JComboBox<String> modeCmb = new JComboBox<>(modes);
                modeCmb.setSelectedItem(ac.getMode());
                modeCmb.setBackground(new Color(255, 255, 255, 40));
                modeCmb.setForeground(TEXT_LIGHT);
                modeCmb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                modeCmb.setEnabled(device.isOn());
                modeCmb.addActionListener(e -> {
                    ac.setMode((String) modeCmb.getSelectedItem());
                    valLabel.setText("Temp: " + ac.getTemperature() + "°C | Mode: " + ac.getMode());
                });

                acControls.add(decBtn);
                acControls.add(incBtn);
                acControls.add(modeCmb);

                centerPanel.add(valLabel, BorderLayout.NORTH);
                centerPanel.add(acControls, BorderLayout.CENTER);

            } else if (device instanceof SecurityCamera) {
                SecurityCamera cam = (SecurityCamera) device;
                JLabel valLabel = new JLabel(cam.isRecording() ? "Recording Live..." : "Standby");
                valLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                valLabel.setForeground(cam.isRecording() ? ACCENT_RED : TEXT_MUTED);

                JButton recBtn = new StyledButton(cam.isRecording() ? "Stop Recording" : "Start Recording", 
                                                   cam.isRecording() ? ACCENT_GLASS : ACCENT_RED);
                recBtn.setEnabled(device.isOn());
                recBtn.addActionListener(e -> {
                    if (cam.isRecording()) {
                        cam.stopRecording();
                    } else {
                        cam.startRecording();
                    }
                    valLabel.setText(cam.isRecording() ? "Recording Live..." : "Standby");
                    valLabel.setForeground(cam.isRecording() ? ACCENT_RED : TEXT_MUTED);
                    recBtn.setText(cam.isRecording() ? "Stop Recording" : "Start Recording");
                    recBtn.setBackground(cam.isRecording() ? ACCENT_GLASS : ACCENT_RED);
                });

                centerPanel.add(valLabel, BorderLayout.NORTH);
                centerPanel.add(recBtn, BorderLayout.CENTER);
            }

            add(centerPanel, BorderLayout.CENTER);

            // --- Bottom Row: Type and Delete Button ---
            JPanel bottomRow = new JPanel(new BorderLayout());
            bottomRow.setOpaque(false);

            String typeStr = device.getType().toUpperCase();
            if (device instanceof Light) typeStr = "💡 " + typeStr;
            else if (device instanceof Fan) typeStr = "🌀 " + typeStr;
            else if (device instanceof AirConditioner) typeStr = "❄️ " + typeStr;
            else if (device instanceof SecurityCamera) typeStr = "📹 " + typeStr;

            JLabel typeLabel = new JLabel(typeStr);
            typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            typeLabel.setForeground(TEXT_MUTED);

            JButton editBtn = new StyledButton("Edit", ACCENT_GLASS);
            editBtn.setPreferredSize(new Dimension(60, 24));
            editBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            editBtn.addActionListener(e -> showEditDeviceDialog(device));

            JButton deleteBtn = new StyledButton("Remove", ACCENT_GLASS);
            deleteBtn.setPreferredSize(new Dimension(80, 24));
            deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(frame, 
                    "Remove device " + device.getDeviceId() + "?", 
                    "Confirm Remove", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    home.removeDevice(device.getDeviceId());
                    refreshUI();
                }
            });

            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            actionPanel.setOpaque(false);
            actionPanel.add(editBtn);
            actionPanel.add(deleteBtn);

            bottomRow.add(typeLabel, BorderLayout.WEST);
            bottomRow.add(actionPanel, BorderLayout.EAST);
            add(bottomRow, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_GLASS);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}

