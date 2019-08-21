package be.formatech.training.formationrefactoring.exercice1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

class ExcelAnomalieArgumentsValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelAnomalieArgumentsValidator.class);

    public boolean isValid(Properties properties) {
        /* Tester les arguments manquants */
        if (properties.getProperty("ONSSTRIMESTRE") == null) {
            LOGGER.error("*** FATAL ERROR missing arg 'ONSSTRIMESTRE'.");
            return false;
        } else if (!validateQuarter(properties.getProperty("ONSSTRIMESTRE"))) {
            return false;
        }

        if (",true,false,".indexOf(properties.getProperty("ISORIGINAL").toLowerCase()) == -1) {
            LOGGER.error("*** FATAL ERROR invalid value '" + properties.getProperty("ISORIGINAL").toLowerCase() + "' for arg 'ISORIGINAL'.");
            return false;
        }
        return true;
    }

    private boolean validateQuarter(String onssTrimestreProperty) {

        try {
            Integer.valueOf(onssTrimestreProperty);
        } catch (Exception e) {
            LOGGER.error("*** FATAL ERROR invalid value '" + onssTrimestreProperty + "' for arg 'ONSSTRIMESTRE'.");
            return false;
        }

        int yyyy0q = Integer.valueOf(onssTrimestreProperty);

        if (trimestreYear(yyyy0q) < 2000 || (trimestreYear(yyyy0q)) > 2099 || trimestreQuarter(yyyy0q) < 1 || (trimestreQuarter(yyyy0q)) > 4) {
            LOGGER.error("*** FATAL ERROR invalid value '" + onssTrimestreProperty + "' for arg 'ONSSTRIMESTRE'.");
            return false;
        }

        return true;
    }

    private int trimestreQuarter(int yyyy0q) {
        return yyyy0q % 100;
    }

    private int trimestreYear(int yyyy0q) {
        return yyyy0q / 100;
    }
}
