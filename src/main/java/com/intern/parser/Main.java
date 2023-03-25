package com.intern.parser;

import com.intern.parser.pojo.PhoneBilling;
import com.intern.parser.util.Parser;

import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        HashMap<String, List<PhoneBilling>> phoneBillingHashMap = parser.parseFile("cdr.txt");
        for (List<PhoneBilling> phoneBilling :
                phoneBillingHashMap.values()) {
            parser.createReport(phoneBilling);
        }
    }
}