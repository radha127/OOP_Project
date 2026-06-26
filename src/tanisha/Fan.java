// Fan.java - CHILD CLASS
// OOP Pillar: INHERITANCE + POLYMORPHISM
// =====================================================

public class Fan extends SmartDevice {

    // Extra field specific to Fan only
    private int speed; // 1 to 5

    // Constructor
    public Fan(String deviceId, String name, String room, int speed) {
        super(deviceId, name, room); // Call parent constructor - INHERITANCE
        this.speed = speed;
    }

    // Getter and Setter for speed
    public int getSpeed()        { return speed; }
    public void setSpeed(int speed) {
        if (speed >= 1 && speed <= 5) {
            this.speed = speed;
        }
    }

    // --- POLYMORPHISM: Overriding abstract methods ---

    @Override
    public String operate() {
        if (isOn()) {
            return getName() + " is spinning at speed " + speed + ".";
        } else {
            return getName() + " is OFF.";
        }
    }

    @Override
    public String getStatus() {
        return (isOn() ? "ON" : "OFF") + " | Speed: " + speed + "/5";
    }

    @Override
    public String getType() {
        return "Fan";
    }

    // For saving to file
    // Format: FAN|id|name|room|isOn|speed
    public String toFileString() {
        return "FAN|" + getDeviceId() + "|" + getName() + "|" + getRoom() + "|" + isOn() + "|" + speed;
    }
}
