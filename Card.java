import java.util.Random;

public class Card {
    // each card object keeps track of number, pin, and balance
    int balance;
    private final String cardNumber;
    private final String pin;
    private Random random = new Random();

    // constructor for new cards, balance is 0
    public Card() {
        balance = 0;
        cardNumber = genCardNumber();
        pin = genPin();
    }
    // constructor used for logging in
    // for cards that already exist in the db
    public Card(String cardNumber, String pin, int balance) {
        this.balance = balance;
        this.cardNumber = cardNumber;
        this.pin = pin;
    }
    // card number generation is as designated by hs
    // first 6 digits are the same for every card
    // next 9 digits are random, last digit is luhn algo checksum
    // see gh, hs, and https://www.geeksforgeeks.org/luhn-algorithm/
    public String genCardNumber() {
        StringBuilder cnum = new StringBuilder("400000");
        for (int i = 0; i < 9; i++) {
            cnum.append(random.nextInt(10));
        }
        cnum.append(lastDigit(cnum.toString()));
        return cnum.toString();
    }
    // randomly generated pin
    public String genPin() {
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }
    // luhn algo implementation
    public static int luhnAlgo(String partialCardNum) {
        int check = 0;
        for (int i = 0; i < partialCardNum.length(); i++) {
            int digit = Integer.parseInt(String.valueOf(partialCardNum.charAt(i)));
            if (i % 2 == 0) {
                digit *= 2;
            }
            if (digit > 9) {
                digit -= 9;
            }
            check += digit;
        }
        return check;
    }
    // luhn algo implementation helps generate last digit of card number
    public int lastDigit(String partialCardNum) {
        int digit = luhnAlgo(partialCardNum) % 10;
        return digit == 0 ? 0 : 10 - digit;
    }
    // account transfers make use of luhn algo verification, see bank app
    public static boolean isCardNumberValid(String cardNumber) {
        return luhnAlgo(cardNumber) % 10 == 0;
    }

    public String getPin() {
        return pin;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getBalance() {
        return balance;
    }
    // balance change done here in card object, then change is registered to db
    public Card addBalance(int incomeToAdd) {
        return new Card(cardNumber, pin, balance + incomeToAdd);
    }
}