// =====================================================
// AirConditioner.java - CHILD CLASS
// OOP Pillar: INHERITANCE + POLYMORPHISM
// =====================================================

public class AirConditioner extends SmartDevice {

    // Extra fields specific to AC only
    private double temperature; // 16.0 to 30.0
    private String mode;        // "Cool", "Heat", "Auto"

    // Constructor
    public AirConditioner(String deviceId, String name, String room, double temperature, String mode) {
        super(deviceId, name, room); // Call parent constructor - INHERITANCE
        this.temperature = temperature;
        this.mode = mode;
    }

    // Getters and Setters
    public double getTemperature()             { return temperature; }
    public String getMode()                    { return mode; }

    public void setTemperature(double temperature) {
        if (temperature >= 16.0 && temperature <= 30.0) {
            this.temperature = temperature;
        }
    }

    public void setMode(String mode) {
        if (mode.equals("Cool") || mode.equals("Heat") || mode.equals("Auto")) {
            this.mode = mode;
        }
    }

    // --- POLYMORPHISM: Overriding abstract methods ---

    @Override
    public String operate() {
        if (isOn()) {
            return getName() + " running in " + mode + " mode at " + temperature + "°C.";
        } else {
            return getName() + " is OFF.";
        }
    }

    @Override
    public String getStatus() {
        return (isOn() ? "ON" : "OFF") + " | " + temperature + "°C | Mode: " + mode;
    }

    @Override
    public String getType() {
        return "AC";
    }

    // For saving to file
    // Format: AC|id|name|room|isOn|temperature|mode
    public String toFileString() {
        return "AC|" + getDeviceId() + "|" + getName() + "|" + getRoom() + "|" + isOn() + "|" + temperature + "|" + mode;
    }
}
