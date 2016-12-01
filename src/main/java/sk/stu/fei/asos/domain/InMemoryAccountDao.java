package sk.stu.fei.asos.domain;

import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

import static sk.stu.fei.asos.controllers.ExceptionHandlerController.NotFound;

@Repository
public class InMemoryAccountDao {
    private static long autoCompleteCounter = 0;
    private final Map<Long, Account> accounts;

    public InMemoryAccountDao() {
        this.accounts = new HashMap<>();
    }

    @PostConstruct
    public void createTestAccounts() {
        accounts.put(1L, new Account(1L, "John", "User", "johnuser", "password", AccountRole.User));
        accounts.put(2L, new Account(2L, "John", "Admin", "johnadmin", "password", AccountRole.Admin));
        accounts.put(3L, new Account(3L, "John", "Doe", "johndoe", "password", AccountRole.PrivilegedUser));
        accounts.put(4L, new Account(4L, "Jane", "Doe", "janedoe", "password", AccountRole.User));
    }

    public Account findById(Long id) {
        return Optional.ofNullable(accounts.get(id)).orElseThrow(
                () -> new NotFound(String.format("Account id=%d not found", id)));
    }

    public Account findByUsername(String username) {
        Account result = null;
        for ( Account account : accounts.values() ) {
            if ( account.getUsername().equals(username) ) {
                result = account;
                break;
            }
        }
        return Optional.ofNullable(result).orElseThrow(
                () -> new NotFound(String.format("Account username=%s not found", username)));
    }

    public Account persist(Account account) {
        if ( account.getId() == null ) {
            autoCompleteCounter++;
            account.setId(autoCompleteCounter);
        }
        accounts.put(account.getId(), account);
        return account;
    }

    public void delete(Account account) {
        accounts.remove(account.getId());
    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }
}
