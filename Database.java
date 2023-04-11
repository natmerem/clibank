import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Optional;
// check if db instance exists before creating new one
public class Database {
    private static Database instance;
    SQLiteDataSource dataSource;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
  
    //plugged in command line arg directly for ease of use with replit
    public void createTable(){
        String url = "jdbc:sqlite:db.s3db";
        dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try(Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card (id INTEGER PRIMARY KEY, number TEXT NOT NULL, pin TEXT NOT NULL, balance INTEGER DEFAULT 0)");
            } catch (Exception e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}
    }

    // all db class functions invoked by bankapp class
    // adding a user/card = insert query
    public void addUser(String cardNumber, String pin) {
        try(Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("INSERT INTO card (number, pin) VALUES (" + "'" + cardNumber + "', '" + pin + "')");
            } catch (Exception e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}
    }
    // checking for a credentials match = select query
    public Optional<Card> getUserByLogIn(String cardNumberInput, String pinInput) {
        try(Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet currentUser = statement.executeQuery("SELECT * FROM card WHERE number = '" + cardNumberInput + "' AND pin = '" + pinInput + "'")) {
                    if (currentUser.next()) {
                        int balance = currentUser.getInt("balance");
                        return Optional.of(new Card(cardNumberInput, pinInput, balance));
                    }
                } catch (Exception e) {e.printStackTrace();}
            } catch (Exception e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}
        return Optional.empty();
    }
    // checking for a card match = select query
    // no pin needed, for transfers between accounts
    public Optional<Card> getUserByCard(String cardNumberInput) {
        try(Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet currentUser = statement.executeQuery("SELECT * FROM card WHERE number = '" + cardNumberInput + "'")) {
                    if (currentUser.next()) {
                        String pin = currentUser.getString("pin");
                        int balance = currentUser.getInt("balance");
                        return Optional.of(new Card(cardNumberInput, pin, balance));
                    }
                } catch (Exception e) {e.printStackTrace();}
            } catch (Exception e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}
        return Optional.empty();
    }
    // changing card balance = update query
    public void updateBalance(Card card) {
        String update = "UPDATE card SET balance = ? WHERE number = ?";
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(update)) {
                preparedStatement.setInt(1, card.getBalance());
                preparedStatement.setString(2, card.getCardNumber());
                preparedStatement.executeUpdate();
            } catch (Exception e) { e.printStackTrace();}
        } catch (Exception e) { e.printStackTrace();}
    }
    // deleting a card/user = delete query :)
    public void delete(Card card) {
        String delete = "DELETE FROM card WHERE number = ?";
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(delete)) {
                preparedStatement.setString(1, card.getCardNumber());
                preparedStatement.executeUpdate();
            } catch (Exception e) { e.printStackTrace();}
        } catch (Exception e) { e.printStackTrace();}
    }
}