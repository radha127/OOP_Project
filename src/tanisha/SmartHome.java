// =====================================================
// SmartHome.java - CONTAINER CLASS
// Manages all devices using ArrayList
// =====================================================

import java.util.ArrayList;

public class SmartHome {

    // Private fields - ENCAPSULATION
    private String homeName;
    private ArrayList<SmartDevice> devices; // Holds ALL devices (any subtype)

    // Constructor
    public SmartHome(String homeName) {
        this.homeName = homeName;
        this.devices = new ArrayList<>();
    }

    // --- Getter ---
    public String getHomeName() { return homeName; }

    // --- Add a device to the home ---
    public void addDevice(SmartDevice device) {
        devices.add(device);
    }

    // --- Remove a device by its ID ---
    public boolean removeDevice(String deviceId) {
        for (SmartDevice d : devices) {
            if (d.getDeviceId().equals(deviceId)) {
                devices.remove(d);
                return true; // Removed successfully
            }
        }
        return false; // Not found
    }

    // --- Find a device by ID ---
    public SmartDevice getDevice(String deviceId) {
        for (SmartDevice d : devices) {
            if (d.getDeviceId().equals(deviceId)) {
                return d;
            }
        }
        return null; // Not found
    }

    // --- Get ALL devices ---
    public ArrayList<SmartDevice> getAllDevices() {
        return devices;
    }

    // --- Get devices in a specific room ---
    public ArrayList<SmartDevice> getDevicesByRoom(String room) {
        ArrayList<SmartDevice> roomDevices = new ArrayList<>();
        for (SmartDevice d : devices) {
            if (d.getRoom().equalsIgnoreCase(room)) {
                roomDevices.add(d);
            }
        }
        return roomDevices;
    }

    // --- Get list of unique room names ---
    public ArrayList<String> getRooms() {
        ArrayList<String> rooms = new ArrayList<>();
        for (SmartDevice d : devices) {
            if (!rooms.contains(d.getRoom())) {
                rooms.add(d.getRoom());
            }
        }
        return rooms;
    }

    // --- Count total devices ---
    public int getTotalDevices() {
        return devices.size();
    }

    // --- Count how many devices are ON ---
    public int getActiveDevices() {
        int count = 0;
        for (SmartDevice d : devices) {
            if (d.isOn()) count++;
        }
        return count;
    }

    // --- Clear all devices (used when loading from file) ---
    public void clearDevices() {
        devices.clear();
    }
}
