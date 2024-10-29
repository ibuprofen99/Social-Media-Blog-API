package Service;
import DAO.MessageDAO;
import Model.Message;
import DAO.AccountDAO;
import java.util.List;

public class MessageService {
    MessageDAO messageDAO;
    AccountDAO accountDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
        this.accountDAO = new AccountDAO();
    }

    public MessageService(MessageDAO messageDAO, AccountDAO accountDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = accountDAO;
    }

    public Message creatMessage(Message message){
        if(!accountDAO.accountIdCheck(message.getPosted_by())){
            return null;
        }

        if(message.message_text.length() != 0 && message.message_text.length() < 255){
            return messageDAO.createNewMessage(message);
        }

        return null;
    }

    public List<Message> returnAllMessages(){
        return messageDAO.returnAllMessages();
    }

    public List<Message> getMessagesByAccountId(int accountId) {
        return messageDAO.getAllMessagesByAccountId(accountId);
    }
    
    public Message returnMessageById(int message_id){
        return messageDAO.returnMessageById(message_id);
    }
    public Message delMessage(int message_id){
        return messageDAO.deleteMessageByID(message_id);
    }
    
    public Message updateMessageText(int message_id, String newMessageText) {
        // Validate message_text
        if (newMessageText == null || newMessageText.trim().isEmpty() || newMessageText.length() > 255) {
            return null; 
        }
        
        // Call DAO to update message text
        return messageDAO.updateMessageTextById(message_id, newMessageText);
    }
    
}
