// SmartDevice.java - ABSTRACT BASE CLASS
// OOP Pillar: ABSTRACTION + ENCAPSULATION
public abstract class SmartDevice {

    // Private fields - ENCAPSULATION (data is hidden)
    private String deviceId;
    private String name;
    private String room;
    private boolean isOn;

    // Constructor
    public SmartDevice(String deviceId, String name, String room) {
        this.deviceId = deviceId;
        this.name = name;
        this.room = room;
        this.isOn = false; // All devices start OFF
    }

    // --- Getters (Public access to private data) ---
    public String getDeviceId() { return deviceId; }
    public String getName()     { return name; }
    public String getRoom()     { return room; }
    public boolean isOn()       { return isOn; }

    // --- Setters ---
    public void setName(String name)   { this.name = name; }
    public void setRoom(String room)   { this.room = room; }

    // --- Concrete Methods (shared by all devices) ---
    public void turnOn()  { this.isOn = true; }
    public void turnOff() { this.isOn = false; }

    // Toggle ON/OFF
    public void toggle() {
        this.isOn = !this.isOn;
    }

    // --- Abstract Methods - ABSTRACTION ---
    // Every device MUST implement these differently
    public abstract String operate();     // What the device does when ON
    public abstract String getStatus();   // Detailed status string
    public abstract String getType();     // Returns "Light", "Fan", etc.

    // For saving to file - basic info
    @Override
    public String toString() {
        return "Device[" + deviceId + "] " + name + " in " + room + " | " + (isOn ? "ON" : "OFF");
    }
}
