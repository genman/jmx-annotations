package net.noderunner.management.rhq;

import javax.management.DescriptorKey;

/**
 * Activation policy for this attribute (when changed).
 */
public @interface ActivationPolicy {

    /**
     * Activation policy. One of:
     * <li>immediate
     * <li>restart
     * <li>shutdown
     */
    @DescriptorKey("activationPolicy")
    String value();

}
