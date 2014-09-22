package javax.management;

import javax.management.MBeanOperationInfo;

/**
 * Defines the impact of an MBean operation, in particular whether it
 * has an effect on the MBean or simply returns information.
 */
public enum Impact {

    /**
     * @see MBeanOperationInfo#ACTION
     */
    ACTION(MBeanOperationInfo.ACTION),

    /**
     * @see MBeanOperationInfo#INFO
     */
    INFO(MBeanOperationInfo.INFO),

    /**
     * @see MBeanOperationInfo#ACTION_INFO
     */
    ACTION_INFO(MBeanOperationInfo.ACTION_INFO),

    /**
     * @see MBeanOperationInfo#UNKNOWN
     */
    UNKNOWN(MBeanOperationInfo.UNKNOWN);

    private final int code;

    private Impact(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
