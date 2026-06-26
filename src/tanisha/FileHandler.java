
// Saves and loads all devices to/from a text file
// =====================================================

import java.io.*;

public class FileHandler {

    // File path - data folder, one level up from src/
    private static final String FILE_PATH = "data/home_data.txt";

    // -------------------------------------------------------
    // SAVE: Write all devices to file, one line per device
    // -------------------------------------------------------
    public static void saveHome(SmartHome home) {
        // Create the data folder if it does not exist
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        try {
            // FileWriter + BufferedWriter for writing text
            FileWriter fw = new FileWriter(FILE_PATH);
            BufferedWriter bw = new BufferedWriter(fw);

            // Write home name on first line
            bw.write("HOME|" + home.getHomeName());
            bw.newLine();

            // Write each device on its own line
            for (SmartDevice device : home.getAllDevices()) {

                // Decide format based on type - POLYMORPHISM in action!
                if (device instanceof Light) {
                    bw.write(((Light) device).toFileString());

                } else if (device instanceof Fan) {
                    bw.write(((Fan) device).toFileString());

                } else if (device instanceof AirConditioner) {
                    bw.write(((AirConditioner) device).toFileString());

                } else if (device instanceof SecurityCamera) {
                    bw.write(((SecurityCamera) device).toFileString());
                }

                bw.newLine(); // New line after each device
            }

            bw.close(); // Always close the writer!
            System.out.println("Home saved successfully to " + FILE_PATH);

        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // LOAD: Read file and recreate device objects
    // -------------------------------------------------------
    public static SmartHome loadHome() {
        SmartHome home = null;

        try {
            // FileReader + BufferedReader for reading text
            FileReader fr = new FileReader(FILE_PATH);
            BufferedReader br = new BufferedReader(fr);

            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split("\\|"); // Split by | character

                if (lineNumber == 1) {
                    // First line is the home name
                    // Format: HOME|My Home
                    home = new SmartHome(parts[1]);
                    continue;
                }

                // Remaining lines are devices
                String type = parts[0]; // LIGHT, FAN, AC, CAMERA

                if (type.equals("LIGHT")) {
                    // Format: LIGHT|id|name|room|isOn|brightness
                    Light light = new Light(parts[1], parts[2], parts[3], Integer.parseInt(parts[5]));
                    if (parts[4].equals("true")) light.turnOn();
                    home.addDevice(light);

                } else if (type.equals("FAN")) {
                    // Format: FAN|id|name|room|isOn|speed
                    Fan fan = new Fan(parts[1], parts[2], parts[3], Integer.parseInt(parts[5]));
                    if (parts[4].equals("true")) fan.turnOn();
                    home.addDevice(fan);

                } else if (type.equals("AC")) {
                    // Format: AC|id|name|room|isOn|temperature|mode
                    AirConditioner ac = new AirConditioner(
                        parts[1], parts[2], parts[3],
                        Double.parseDouble(parts[5]), parts[6]
                    );
                    if (parts[4].equals("true")) ac.turnOn();
                    home.addDevice(ac);

                } else if (type.equals("CAMERA")) {
                    // Format: CAMERA|id|name|room|isOn|isRecording
                    SecurityCamera cam = new SecurityCamera(parts[1], parts[2], parts[3]);
                    if (parts[4].equals("true")) cam.turnOn();
                    if (parts[5].equals("true")) cam.startRecording();
                    home.addDevice(cam);
                }
            }

            br.close(); // Always close the reader!
            System.out.println("Home loaded successfully from " + FILE_PATH);

        } catch (FileNotFoundException e) {
            System.out.println("No saved file found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }

        return home;
    }

    // Check if a saved file exists
    public static boolean hasSavedFile() {
        return new File(FILE_PATH).exists();
    }
}
