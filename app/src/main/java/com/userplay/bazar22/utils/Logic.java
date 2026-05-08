package com.userplay.bazar22.utils;

import java.util.ArrayList;
import java.util.List;

public class Logic {

    private void  singlePanaBulk()
    {
        int number = 0;
        ArrayList<Integer> lst = new ArrayList<Integer>();

        for (int i = 100; i < 1000; i++) {
            int[] digits = new int[3];
            digits[0] = i / 100; // get the first digit
            digits[1] = (i % 100) / 10; // get the second digit
            digits[2] = i % 10; // get the third digit

            if ((digits[0] < digits[1] && digits[1] < digits[2] || digits[2] == 0 && digits[1] != 0 && digits[0] < digits[1]) && (digits[0] + digits[1] + digits[2]) % 10 == 0) {
                lst.add(i);
            }
        }

        System.out.println("List of three-digit numbers where the first digit is smaller than the second digit and the second digit is smaller than the third digit, or the third digit is zero and the second digit is not zero, and whose sum of digits equals the input number or whose last digit of the sum equals the last digit of the input number:");
        System.out.println(lst);
    }


    private void doublePanaBulk()
    {
        int number = 0;
        ArrayList<Integer> lst = new ArrayList<Integer>();

        for (int i = 100; i < 1000; i++) {
            int[] digits = new int[3];
            digits[0] = i / 100; // get the first digit
            digits[1] = (i % 100) / 10; // get the second digit
            digits[2] = i % 10; // get the third digit

            if (((digits[0] == digits[1] || digits[1] == digits[2]) && (digits[2] >= digits[0])) || ((digits[0] == digits[1] || digits[1] == digits[2]) && digits[2] == 0)) {
                if (digits[0] != digits[1] || digits[1] != digits[2]) {
                    int sum = digits[0] + digits[1] + digits[2];
                    if (sum == number || sum % 10 == number % 10) {
                        lst.add(i);
                    }
                }
            }
        }

        System.out.println("List of three-digit numbers where the first two digits are the same or the last two digits are the same, and whose sum of digits equals the input number or whose last digit of the sum equals the last digit of the input number:");
        System.out.println(lst);
    }


    private void doublePana()
    {
        int num = 410;

        int firstDigit = num / 100;
        int secondDigit = (num / 10) % 10;
        int lastDigit = num % 10;

        if ((firstDigit == secondDigit || secondDigit == lastDigit) && (lastDigit == 0 || secondDigit > firstDigit && secondDigit > lastDigit)) {
            System.out.println(num + " meets the conditions.");
        } else {
            System.out.println(num + " does not meet the conditions.");
        }
    }


    private void pairTwoValue()
    {
        int num1 = 123;
        int num2 = 321;

        List<Integer> pairs = new ArrayList<Integer>();

// Iterate through digits of the first number
        for (int i = 0; i < num1; i++) {
            int digit1 = (num1 / (int)Math.pow(10, 2-i)) % 10;

            // Iterate through digits of the second number
            for (int j = 0; j < num2; j++) {
                int digit2 = (num2 / (int)Math.pow(10, 2-j)) % 10;

                // Combine the two digits and add to list
                int pair = (digit1 * 10) + digit2;
                pairs.add(pair);
            }
        }

        System.out.println(pairs);
    }
}
