// =====================================================
// SecurityCamera.java - CHILD CLASS
// OOP Pillar: INHERITANCE + POLYMORPHISM
// =====================================================

public class SecurityCamera extends SmartDevice {

    // Extra field specific to SecurityCamera only
    private boolean isRecording;

    // Constructor
    public SecurityCamera(String deviceId, String name, String room) {
        super(deviceId, name, room); // Call parent constructor - INHERITANCE
        this.isRecording = false;
    }

    // Getter
    public boolean isRecording() { return isRecording; }

    // Start / Stop recording
    public void startRecording() { this.isRecording = true; }
    public void stopRecording()  { this.isRecording = false; }

    // --- POLYMORPHISM: Overriding abstract methods ---

    @Override
    public String operate() {
        if (isOn()) {
            String rec = isRecording ? " and RECORDING." : " (not recording).";
            return getName() + " is monitoring" + rec;
        } else {
            return getName() + " is OFF.";
        }
    }

    @Override
    public String getStatus() {
        String onOff = isOn() ? "ON" : "OFF";
        String rec   = isRecording ? "Recording" : "Not Recording";
        return onOff + " | " + rec;
    }

    @Override
    public String getType() {
        return "Camera";
    }

    // For saving to file
    // Format: CAMERA|id|name|room|isOn|isRecording
    public String toFileString() {
        return "CAMERA|" + getDeviceId() + "|" + getName() + "|" + getRoom() + "|" + isOn() + "|" + isRecording;
    }
}
