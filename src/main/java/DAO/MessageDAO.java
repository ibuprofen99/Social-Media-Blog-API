package DAO;
import java.sql.*;
import Model.Message;
import Util.ConnectionUtil;
import java.util.*;

public class MessageDAO {

    public Message createNewMessage(Message message){

        //establish connection to db

        Connection connection = ConnectionUtil.getConnection();
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES(?, ?, ?);";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected == 0){
                // message was not inserted
                return null; 
            }
            //message_id retrieval
            ResultSet gen = preparedStatement.getGeneratedKeys();
            //if a message key exists
            if(gen.next()){
                //cast it from long to int
                int generated_key = (int) gen.getLong(1);
                //set the message id
                message.setMessage_id(generated_key);
                return new Message(generated_key, message.getPosted_by(), message. getMessage_text(), message.getTime_posted_epoch());
            }
            //message creation failed.
            return null;

        }catch(SQLException e){
            e.printStackTrace(); 
        }
        return null;
    }

    public List<Message> returnAllMessages(){
        //establish connection to db

        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT * FROM message;";
        List<Message> messages = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Message message = new Message(
                    resultSet.getInt("message_id"),
                    resultSet.getInt("posted_by"),
                    resultSet.getString("message_text"),
                    resultSet.getLong("time_posted_epoch")
                );
                messages.add(message);
            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return messages;
    }
    public List<Message> getAllMessagesByAccountId(int accountId) {
        List<Message> messages = new ArrayList<>();
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT * FROM message WHERE posted_by = ?;";
    
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, accountId);
            
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                Message message = new Message(
                    resultSet.getInt("message_id"),
                    resultSet.getInt("posted_by"),
                    resultSet.getString("message_text"),
                    resultSet.getLong("time_posted_epoch")
                );
                messages.add(message);
            }
    
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        
        return messages; 
    }
    
    public Message returnMessageById(int message_id) {

        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT * FROM message where message_id = ?;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);

            ResultSet resultSet = preparedStatement.executeQuery();
            // if a query is matched
            while (resultSet.next()) {
                return new Message(
                    resultSet.getInt("message_id"),
                    resultSet.getInt("posted_by"),
                    resultSet.getString("message_text"),
                    resultSet.getLong("time_posted_epoch")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        // no msg found
        return null;
    }

    public Message deleteMessageByID(int message_id) {
        String selectSql = "SELECT * FROM message WHERE message_id = ?;";
        String deleteSql = "DELETE FROM message WHERE message_id = ?;";
        
        Connection connection = ConnectionUtil.getConnection();
        Message message = null;

        try {            
            // Step 1: Retrieve the message
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, message_id);

            ResultSet resultSet = selectStatement.executeQuery();
    
            while (resultSet.next()) {
                message = new Message(
                    resultSet.getInt("message_id"),
                    resultSet.getInt("posted_by"),
                    resultSet.getString("message_text"),
                    resultSet.getLong("time_posted_epoch")
                );
                
            }
    
            // Step 2: Delete the message if it exists
            if (message != null) {
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                deleteStatement.setInt(1, message_id);
                deleteStatement.executeUpdate();
            }
    
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return message;
    }

    public Message updateMessageTextById(int message_id, String newMessageText) {
        Connection connection = ConnectionUtil.getConnection();
        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
    
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newMessageText);
            preparedStatement.setInt(2, message_id);
            
            int rowsAffected = preparedStatement.executeUpdate();
    
            if (rowsAffected == 0) {
                // Message with the given ID does not exist
                return null; 
            }
    
            // Retrieve the updated message
            String selectSql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, message_id);
            ResultSet resultSet = selectStatement.executeQuery();
    
            if (resultSet.next()) {
                return new Message(
                    resultSet.getInt("message_id"),
                    resultSet.getInt("posted_by"),
                    resultSet.getString("message_text"),
                    resultSet.getLong("time_posted_epoch")
                );
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return null;
    }
    

}
     

