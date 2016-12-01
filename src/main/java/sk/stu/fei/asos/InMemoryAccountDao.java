package sk.stu.fei.asos;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryAccountDao {
    private final Map<Long, Account> accounts;
    private static long autoCompleteCounter = 0;

    public InMemoryAccountDao() {
        this.accounts = new HashMap<>();
    }

    public Account findById(Long id) {
        return accounts.get(id);
    }

    public Account findByUsername(String username) {
        for ( Account account : accounts.values() ) {
            if ( account.getUsername().equals(username) ) {
                return account;
            }
        }
        return null;
    }

    public Account persist(Account account) {
        if (account.getId() == null) {
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
