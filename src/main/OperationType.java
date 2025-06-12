package main;

public enum OperationType {
    INQUIRY("inquiry"),
    TRANSFERRED("transferred"),
    RECEIVED("received"),
    WITHDREW("withdrew"),
    FINAL_BALANCE("final balance"),
    ERROR_NO_INQUIRY_BALANCE("no inquiry balance"),
    ERROR_NO_EQUALS_BALANCE_AND_INQUIRY_BALANCE("no equal balance");


    private final String text;

    OperationType(String text) {
        this.text = text;
    }

    public String getOperation() {
        return text;
    }
}
