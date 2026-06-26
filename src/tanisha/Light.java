// =====================================================
// Light.java - CHILD CLASS
// OOP Pillar: INHERITANCE + POLYMORPHISM
// =====================================================

public class Light extends SmartDevice {

    // Extra field specific to Light only
    private int brightness; // 0 to 100

    // Constructor
    public Light(String deviceId, String name, String room, int brightness) {
        super(deviceId, name, room); // Call parent constructor - INHERITANCE
        this.brightness = brightness;
    }

    // Getter and Setter for brightness
    public int getBrightness()          { return brightness; }
    public void setBrightness(int brightness) {
        // Validation inside setter - ENCAPSULATION
        if (brightness >= 0 && brightness <= 100) {
            this.brightness = brightness;
        }
    }

    // --- POLYMORPHISM: Overriding abstract methods ---

    @Override
    public String operate() {
        if (isOn()) {
            return getName() + " is glowing at " + brightness + "% brightness.";
        } else {
            return getName() + " is OFF.";
        }
    }

    @Override
    public String getStatus() {
        return (isOn() ? "ON" : "OFF") + " | Brightness: " + brightness + "%";
    }

    @Override
    public String getType() {
        return "Light";
    }

    // For saving to file
    // Format: LIGHT|id|name|room|isOn|brightness
    public String toFileString() {
        return "LIGHT|" + getDeviceId() + "|" + getName() + "|" + getRoom() + "|" + isOn() + "|" + brightness;
    }
}
