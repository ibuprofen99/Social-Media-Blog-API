package DAO;

import java.sql.*;

import Model.Account;
import Util.ConnectionUtil;


public class AccountDAO {
    
    public Account createAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();
    
        String sql = "INSERT INTO account (username, password) VALUES (?, ?);";
    
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // Set the username and password
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            
            // Execute the insert operation
            int rowsAffected = preparedStatement.executeUpdate();
            

            // Check if the insertion was successful
            if (rowsAffected == 0) {
                return null;
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                int generated_key = (int) generatedKeys.getLong(1);
                account.setAccount_id(generated_key); // Set the generated account_id
                return new Account(generated_key, account.getUsername(), account.getPassword() );
            }
            //return account;
        } catch (SQLException e) {
            e.printStackTrace(); 
        } 
        return null; // if account creation fails.
    }
    
    //log in class
    public Account getAccountByUsernameAndPassword(String username, String password) {
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT account_id, username, password FROM account WHERE username = ? AND password = ?;";
        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            // if an account is found 
            if (resultSet.next()) {
                // Create and return the Account object
                Account account = new Account();
                account.setAccount_id(resultSet.getInt("account_id"));
                account.setUsername(resultSet.getString("username"));
                account.setPassword(resultSet.getString("password")); // Consider hashing this instead
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        } 
        
        return null; 
    }
    //to retrieve account by username in order to check if a username is already taken
    public Account getAccountByUsername(String username) {
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT username FROM account WHERE username = ?;"; 
    
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                // Create and return the Account object without setting the password
                Account account = new Account();
                //account.setAccount_id(resultSet.getInt("account_id")); 
                account.setUsername(resultSet.getString("username"));
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        
        return null;
    }

    public boolean accountIdCheck(int accountId){
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT account_id FROM account WHERE account_id = ?";
    
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, accountId);
    
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Account does not exist or query failed
    }

}
