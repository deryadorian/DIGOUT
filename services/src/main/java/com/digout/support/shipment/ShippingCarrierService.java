package com.digout.support.shipment;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ShippingCarrierService {
    private static final Properties CARRIER_SITES_MAPPING;
    private static final Properties CARRIER_NAMES_MAPPING;
    private static final String SITES_FILENAME = "shipment_carriers_sites.properties";
    private static final String NAMES_FILENAME = "shipment_carriers_names.properties";

    static {
        try {
            CARRIER_SITES_MAPPING = PropertiesLoaderUtils.loadProperties(new ClassPathResource(SITES_FILENAME));
        } catch (final IOException exc) {
            throw new IllegalStateException(String.format(
                    "Couldn't load carrier sites mapping file '%s' from classpath", SITES_FILENAME), exc);
        }
    }

    static {
        try {
            CARRIER_NAMES_MAPPING = PropertiesLoaderUtils.loadProperties(new ClassPathResource(NAMES_FILENAME));
        } catch (final IOException exc) {
            throw new IllegalStateException(String.format(
                    "Couldn't load carrier sites mapping file '%s' from classpath", NAMES_FILENAME), exc);
        }
    }

    public String getCarrirerWebsiteByCode(final String carrierCode) {
        return CARRIER_SITES_MAPPING.getProperty(carrierCode, StringUtils.EMPTY);
    }

    public String getCarrirerNameByCode(final String carrierCode) {
        return CARRIER_NAMES_MAPPING.getProperty(carrierCode, "Unknown");
    }
}
