package net.noderunner.management;

/**
 * Thrown when management methods fail.
 */
public class ManagementException extends RuntimeException {

    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = 1570040921519420808L;

    /**
     * Constructs a ManagementException wrapping a throwable.
     * @param throwable cause
     * @param message the detail message
     */
    public ManagementException(Throwable throwable, String message) {
        super(message, throwable);
    }

    /**
     * Constructs a ManagementException wrapping a throwable.
     * @param throwable cause
     */
    public ManagementException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs an ManagementException that contains a message.
     */
    public ManagementException(String message) {
        super(message);
    }
}
