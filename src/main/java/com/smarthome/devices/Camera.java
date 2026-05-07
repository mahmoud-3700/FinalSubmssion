package com.smarthome.devices;


// Camera device with recording state.
public class Camera extends Device {
    private boolean recording;

    public Camera(String id, String name) {
        super(id, name);
    }

    public boolean isRecording() {
        return recording;
    }

    public void startRecording() {
        if (!recording) {
            recording = true;
        }
    }

    public void stopRecording() {
        if (recording) {
            recording = false;
        }
    }
}
