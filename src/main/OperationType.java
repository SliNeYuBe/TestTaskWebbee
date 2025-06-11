package main;

public enum OperationType {
    INQUIRY("inquiry"),
    TRANSFERRED("transferred"),
    RECEIVED("received"),
    WITHDREW("withdrew");

    private final String text;

    OperationType(String text) {
        this.text = text;
    }

    public String getOperation() {
        return text;
    }
}
