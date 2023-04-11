import java.util.Optional;
import java.util.Scanner;
// bankapp displays menus to users, makes cards, handles transfers, use database class functions to query db. 
public class BankApp {
    // to get user input, menu interaction
    Scanner scanner = new Scanner(System.in);
    // check for db before creating one, see db class
    Database database = Database.getInstance();
    // who is logged in, needed for all account actions
    Card currentCard;
    // start method, invoked by Main
    void start(){
        database.createTable();
        showMainMenu();
    }
  
    // first thing user sees
    // need to determine if new or existing user
    private void showMainMenu() {
        System.out.println("1. Create an account\n2. Log in to account\n0. Exit");
        String mainMenuChoice = scanner.next();
        switch (mainMenuChoice){
            case "1": createAccount(); break;
            case "2": logInToAccount(); break;
            case "0": exitBankApp();
        }
    }

    // new user, use card class to create card
    // use db class addUser func to register to db table
    private void createAccount() {
        Card card = new Card();
        System.out.println("Your card has been created\nYour card number:\n" + card.getCardNumber() + "\nYour card PIN:\n" + card.getPin());
        database.addUser(card.getCardNumber(), card.getPin());
        // send user back to main menu, to log in
        showMainMenu();
    }
    // existing user, use db func getUserByLogIn to check their credentials
    private void logInToAccount() {
        System.out.println("Enter your card number:");
        String cardNumberInput  = scanner.next();
        System.out.println("Enter your PIN:");
        String PinInput  = scanner.next();
        Optional<Card> card = database.getUserByLogIn(cardNumberInput, PinInput);
        // credentials match? keep track of who is logged in with currentCard, show user logged in menu
        // credentials do not match? tell them, then back to main menu
        if (card.isPresent()) {
            this.currentCard = card.get();
            System.out.println("You have successfully logged in!");
            showLoggedInMenu();
        } else {
            System.out.println("Wrong card number or PIN!");
            showMainMenu();
        }
    }
    // determined user already exists, show them the account options
    private void showLoggedInMenu() {
        System.out.println("1. Balance\n2. Add income\n3. Do transfer\n4. Close account\n5. Log out\n0. Exit");
        String liMenuChoice = scanner.next();
        switch (liMenuChoice) {
            case "1": showBalance(); break;
            case "2": addIncome(); break;
            case "3": doTransfer(); break;
            case "4": closeAccount(); break;
            case "5": logOut(); break;
            case "0": exitBankApp();
        }
    }
    // straight forward, card class keeps track of balance
    private void showBalance() {
        System.out.println("Balance: " + currentCard.getBalance());
        showLoggedInMenu();
    }
    // when user changes balance, changes need to be registered to the card object and to the database
    private void addIncome() {
        System.out.println("Enter income");
        String inc = scanner.next();
        int incomeToAdd = Integer.parseInt(inc);
        currentCard = currentCard.addBalance(incomeToAdd);
        database.updateBalance(currentCard);
        System.out.println("Income was added!\n");
        showLoggedInMenu();
    }
    // several validations need to be done for a legal transfer
    // check if the card is luhn algo valid, if the card exists in the db, if the card is a different user, and if the current user has enough money for the transfer
    private void doTransfer() {
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String number = scanner.next();
        if (Card.isCardNumberValid(number)) {
            Optional<Card> card = database.getUserByCard(number);
            if (card.isPresent()) {
                Card transferCard = card.get();
                if (!transferCard.getCardNumber().equals(currentCard.getCardNumber())) {
                    System.out.println("Enter how much money you want to transfer:");
                    int money = Integer.parseInt(scanner.next());
                    if (currentCard.getBalance() >= money) {
                        transferCard = transferCard.addBalance(money);
                        database.updateBalance(transferCard);
                        currentCard = currentCard.addBalance(-money);
                        database.updateBalance(currentCard);
                        System.out.println("Success!\n");
                        showLoggedInMenu();
                    } else {
                        System.out.println("Not enough money!\n");
                        showLoggedInMenu();
                    }
                } else {
                    System.out.println("You cannot send money to yourself!\n");
                    showLoggedInMenu();
                }
            }
            else {
                System.out.println("Such a card does not exist.\n");
                showLoggedInMenu();
            }
        } else {
            System.out.println("Probably you made mistake in the card number. Please try again!\n");
            showLoggedInMenu();
        }
    }
    // remove card from db then send them back to main menu
    // login method checks db, so they cant access that card again
    private void closeAccount() {
        database.delete(currentCard);
        System.out.println("The account has been closed!\n");
        showMainMenu();
    }
    // keeping track of current user with current card, so logout just resets current card
    private void logOut() {
        currentCard = null;
        System.out.println("You have successfully logged out!");
        showMainMenu();
    }
    // bye :)
    private void exitBankApp() {
        System.out.println("Bye!");
        System.exit(0);
    }
}