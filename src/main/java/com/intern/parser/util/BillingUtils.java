package com.intern.parser.util;

import com.intern.parser.exception.NotFoundTariffException;
import com.intern.parser.exception.RoundingException;
import com.intern.parser.pojo.PhoneBilling;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillingUtils {

    public static Logger logger = Logger.getLogger(BillingUtils.class.getName());
    public static String center(String text, int len){
        if (len <= text.length())
            return text.substring(0, len);
        int before = (len - text.length())/2;
        if (before == 0)
            return String.format("%-" + len + "s", text);
        int rest = len - before;
        return String.format("%" + before + "s%-" + rest + "s", "", text);
    }

    public static InputStream getResourcesInputStream(String input){
        InputStream ioStream = BillingUtils.class.getClassLoader()
                .getResourceAsStream(input);
        if (ioStream == null) {
            logger.log(Level.WARNING, "Cannot open file because of: " + input + " is not found!");
        } else {
            logger.info("Successfully opened resource file: "+ input);
        }
        return ioStream;
    }

    public static int getRounding(int seconds, int minutes) throws RoundingException {
        if(seconds<0 || seconds >= 60 || minutes < 0 || minutes >=60) throw new RoundingException("Cannot round talk time because of incorrect amount of seconds or minutes!");
        if (seconds >= 30) minutes++;
        return minutes;
    }

    public static double getCallCost(int duration, long resultTime, boolean isIncoming, String tariff) throws NotFoundTariffException {
        switch (tariff) {
            case "06":
                if (resultTime > 300) return duration;
                else return 0;
            case "03":
                return duration * 1.5;
            case "11":
                return isIncoming ? 0 : resultTime > 100 ? duration * 1.5 : duration * 0.5;
            default:
                throw new NotFoundTariffException("Not found tariff with type: " + tariff);
        }
    }

    public static PhoneBilling getBillsFromLine(String line) throws ParseException {
        String[] strings = line.split(",\\s+");
        PhoneBilling phoneBilling = new PhoneBilling();
        phoneBilling.setPhoneNumber(strings[1]);
        phoneBilling.setIncoming(strings[0].equals("02"));
        SimpleDateFormat parser = new SimpleDateFormat("yyyyMMddHHmmss");
        phoneBilling.setStartTime(parser.parse(strings[2]));
        phoneBilling.setEndTime(parser.parse(strings[3]));
        phoneBilling.setTariff(strings[4]);
        return phoneBilling;
    }
}
