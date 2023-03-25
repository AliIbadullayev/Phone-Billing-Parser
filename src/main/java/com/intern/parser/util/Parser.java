package com.intern.parser.util;

import com.intern.parser.exception.NotFoundTariffException;
import com.intern.parser.pojo.PhoneBilling;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {

    public HashMap<String, List<PhoneBilling>> parseFile(String inputFile){
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getResourcesInputStream(inputFile)));
            HashMap<String, List<PhoneBilling>> phoneBillingHashMap = new HashMap<>();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                PhoneBilling phoneBilling = getBillsFromLine(line);
                List<PhoneBilling> list = phoneBillingHashMap.getOrDefault(phoneBilling.getPhoneNumber(), new ArrayList<>());
                list.add(phoneBilling);
                phoneBillingHashMap.put(phoneBilling.getPhoneNumber(), list);
            }
            bufferedReader.close();
            return phoneBillingHashMap;
        } catch (IOException | ParseException ioException){
            System.err.println(ioException.getMessage());
            return null;
        }
    }

    private PhoneBilling getBillsFromLine(String line) throws ParseException {
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
    public void createReport(List<PhoneBilling> phoneBillingList) {
        try {
            long resultTime = 0L;
            double resultPrice = phoneBillingList.get(0).getTariff().equals("06") ? 100d: 0d;
            phoneBillingList = phoneBillingList.stream()
                    .sorted(Comparator.comparing(PhoneBilling::isIncoming))
                    .sorted(Comparator.comparing(PhoneBilling::getStartTime))
                    .collect(Collectors.toList());
            File file = new File("reports/report_" + phoneBillingList.get(0).getPhoneNumber() + ".txt");
            if (!file.exists()) file.createNewFile();
            BufferedWriter bufferedWriter = null;
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(String.format("Tariff index: %s\n", phoneBillingList.get(0).getTariff()));
            bufferedWriter.write("-".repeat(76).concat("\n"));
            bufferedWriter.write(String.format("Report for phone number %s:\n", phoneBillingList.get(0).getPhoneNumber()));
            bufferedWriter.write("-".repeat(76).concat("\n"));
            bufferedWriter.write(String.format("|%-11s|%-21s|%-21s|%-10s|%-7s|\n",center("Call Type", 11), center("Start Time", 21), center("End Time", 21),center("Duration", 10) , center("Cost", 7)));
            bufferedWriter.write("-".repeat(76).concat("\n"));
            bufferedWriter.flush();
            for (PhoneBilling phoneBilling: phoneBillingList){
                Date duration = new Date(phoneBilling.getEndTime().getTime() - phoneBilling.getStartTime().getTime());
                int durationMinutes = getRounding(duration.getSeconds(), duration.getMinutes());
                resultTime += durationMinutes;
                double currCost = getCallCost(durationMinutes, resultTime, phoneBilling.isIncoming(), phoneBilling.getTariff());
                resultPrice += currCost;
                SimpleDateFormat startEndParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat durationParser = new SimpleDateFormat("HH:mm:ss");
                durationParser.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
                bufferedWriter.write(String.format("|%-11s|%-21s|%-21s|%-10s|%-7s|\n",center(phoneBilling.isIncoming()?"02":"01", 11), center(startEndParser.format(phoneBilling.getStartTime()), 21), center(startEndParser.format(phoneBilling.getEndTime()), 21),center(durationParser.format(duration), 10) , center(String.format("%.2f",currCost), 7)));
                bufferedWriter.flush();
            }
            bufferedWriter.write("-".repeat(76).concat("\n"));
            bufferedWriter.write(String.format("|%55s|%10.2f rubles |\n","Total Cost:", resultPrice));
            bufferedWriter.write("-".repeat(76).concat("\n"));
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String center(String text, int len){
        if (len <= text.length())
            return text.substring(0, len);
        int before = (len - text.length())/2;
        if (before == 0)
            return String.format("%-" + len + "s", text);
        int rest = len - before;
        return String.format("%" + before + "s%-" + rest + "s", "", text);
    }

    private InputStream getResourcesInputStream(String input){
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(input);

        if (ioStream == null) {
            throw new IllegalArgumentException(input + " is not found");
        }
        return ioStream;
    }

    private int getRounding(int seconds, int minutes){
        if (seconds >= 30) minutes++;
        return minutes;
    }

    private double getCallCost(int duration, long resultTime, boolean isIncoming, String tariff){
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

}
