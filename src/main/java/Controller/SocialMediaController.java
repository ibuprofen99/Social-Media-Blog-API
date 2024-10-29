package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import static org.mockito.ArgumentMatchers.matches;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Service.AccountService;
import Model.Message;
import Service.MessageService;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController(){
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("/messages", this::getMessagesHandler);
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::messageHandler);
        app.get("messages/{message_id}", this::getMessageByIdHandler);
        app.delete("messages/{message_id}", this::delMessageByIdHandler);
        app.patch("messages/{message_id}", this::updateMessageTextHandler);
        app.get("accounts/{account_id}/messages", this::getAllMessagesByAccountIdHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getMessagesHandler(Context context) {
        // Retrieve all messages from the "database"
        List<Message> messages = messageService.returnAllMessages();
        context.json(messages);

    }

    private void updateMessageTextHandler(Context ctx) {
        try {
            // Get the message_id from the path parameter
            String messageIdParam = ctx.pathParam("message_id");
            int messageId = Integer.parseInt(messageIdParam); 
    
            String newMessageText = ctx.bodyAsClass(Map.class).get("message_text").toString();
            Message updatedMessage = messageService.updateMessageText(messageId, newMessageText);
    
            if (updatedMessage != null) {
                ctx.json(updatedMessage); 
            } else {
                ctx.status(400).json(""); 
            }
            
        } catch (NumberFormatException e) {
            ctx.status(400).json("Invalid message ID format"); 
        } catch (Exception e) {
            ctx.status(500).json("An error occurred: " + e.getMessage()); 
            e.printStackTrace(); 
        }
    }
    
    private void delMessageByIdHandler(Context ctx) {
        try {
            String messageIdParam = ctx.pathParam("message_id");
            int messageId = Integer.parseInt(messageIdParam); // Convert to int
    
            // Attempt to delete the message by ID
            Message message = messageService.delMessage(messageId);
    
            if (message != null) {
                ctx.json(message); 
            } else {
                // Return a 200 status with an empty body if no message was found
                ctx.status(200).json(""); 
            }
            
        } catch (NumberFormatException e) {
            ctx.status(400).json("Invalid message ID format"); 
        } catch (Exception e) {
            ctx.status(500).json( e.getMessage()); 
            e.printStackTrace(); 
        }
    }
    
    
    public void getMessageByIdHandler(Context ctx) {
        try {
            // Get the message_id from the path parameter
            String messageIdParam = ctx.pathParam("message_id");
            int messageId = Integer.parseInt(messageIdParam); 
                Message message = messageService.returnMessageById(messageId);
            
            if (message != null) {
                ctx.json(message); 
            } 
        } catch (NumberFormatException e) {
            ctx.status(400); 
        } catch (Exception e) {
            ctx.status(500); 
            ctx.json("An error occurred: " + e.getMessage());
            e.printStackTrace(); 
        }
    }
    
    private void getAllMessagesByAccountIdHandler(Context ctx) {
        try {
            // Get the account_id from the path parameter
            String accountIdParam = ctx.pathParam("account_id");
            int accountId = Integer.parseInt(accountIdParam); // Convert to int
    
            // Fetch messages by account ID
            List<Message> messages = messageService.getMessagesByAccountId(accountId);
    
            ctx.json(messages); // Return messages as JSON
        } catch (NumberFormatException e) {
            ctx.status(400); 
        } catch (Exception e) {
            ctx.status(500).json("An error occurred: " + e.getMessage()); // log the error
            e.printStackTrace(); 
        }
    }
    
   // private void updateMessageHandler(Context context){

    //}
    private void loginHandler(Context ctx) {
        ObjectMapper objectMapper = new ObjectMapper();
        
        try {
            Account account = objectMapper.readValue(ctx.body(), Account.class);
            
            // Authenticate user
            Account login = accountService.logIn(account.username, account.password);
            
            if (login != null) {
                ctx.status(200).json(login);
            } else {
                ctx.status(401).result(""); 
            }
        } catch (Exception e) {
            ctx.status(500).result("error"); 
            e.printStackTrace(); // log exception 
        }
    }
    
private void registerHandler(Context ctx) {
    ObjectMapper objectMapper = new ObjectMapper();
    Account account;

    try {
        // Deserialize request body into Account object
        account = objectMapper.readValue(ctx.body(), Account.class);
        Account addedAccount = accountService.addAccount(account);

        if (addedAccount != null) {
            ctx.status(200).json(addedAccount);
        } else {
            ctx.status(400);
        }
    } catch (IllegalArgumentException e) {
        ctx.status(405).result(e.getMessage()); 
    } catch (Exception e) {
        ctx.status(500).result("error."); // catch other exceptions
    }
}

private void messageHandler(Context ctx){
    ObjectMapper objectMapper = new ObjectMapper();
    Message message;

    try{
        message = objectMapper.readValue(ctx.body(), Message.class);
        Message newMessage = messageService.creatMessage(message);

        if(newMessage != null){
            ctx.status(200).json(newMessage);
        }
        else{
            ctx.status(400);

        }
    }catch (IllegalArgumentException e) {
        ctx.status(409).result(e.getMessage());
    } catch (Exception e) {
        ctx.status(500).result("error."); 
    }
}

}