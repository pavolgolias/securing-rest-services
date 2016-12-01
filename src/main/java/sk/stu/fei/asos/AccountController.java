package sk.stu.fei.asos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final InMemoryAccountDao dao;

    @Autowired
    public AccountController(InMemoryAccountDao dao) {
        this.dao = dao;
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@RequestBody Account account) {
        return dao.persist(account);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Account getAccount(@PathVariable Long id) {
        return dao.findById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Account updateAccount(@PathVariable Long id, @RequestBody Account account) {
        Account storedAccount = dao.findById(id);
        storedAccount.setFirstName(account.getFirstName());
        storedAccount.setLastName(account.getLastName());
        storedAccount.setUsername(account.getUsername());
        storedAccount.setPassword(account.getPassword());
        return dao.persist(storedAccount);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id) {
        final Account account = dao.findById(id);
        dao.delete(account);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Account> getAllAccounts() {
        return dao.findAll();
    }
}
