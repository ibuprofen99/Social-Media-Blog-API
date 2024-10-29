package Service;
import Model.Account;
import DAO.AccountDAO;


public class AccountService {
    private AccountDAO accountDAO;

    public AccountService(){
        accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO; 
    }
    

    public Account addAccount(Account account){
        Account existingAccount = accountDAO.getAccountByUsername(account.username);
            
        if (existingAccount != null) {
            // Handle the case where the username already exists
            return null;
        }
        //if account is not blank
        if (account.getUsername() != null && !account.getUsername().isBlank() && account.getPassword().length() >= 4 ) {
            return accountDAO.createAccount(account);
        }
        return null;
    }

    public Account logIn(String name, String pass){
        Account account = accountDAO.getAccountByUsernameAndPassword(name, pass);
        return account; 
    }
}
