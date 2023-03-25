package com.intern.parser;

import com.intern.parser.pojo.PhoneBilling;
import com.intern.parser.util.Parser;

import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        HashMap<String, List<PhoneBilling>> phoneBillingHashMap = parser.parseFile("cdr.txt");
        parser.createReport(phoneBillingHashMap.values().stream().filter(phoneBillings -> phoneBillings.size()>1).findFirst().orElse(null));

    }
}