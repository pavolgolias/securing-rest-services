package sk.stu.fei.asos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.stu.fei.asos.domain.Account;
import sk.stu.fei.asos.domain.InMemoryAccountDao;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final InMemoryAccountDao dao;

    @Autowired
    public AccountController(InMemoryAccountDao dao) {
        this.dao = dao;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@RequestBody Account account) {
        return dao.persist(account);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PRIVILEGEDUSER', 'USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Account getAccount(@PathVariable Long id) {
        return dao.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Account updateAccount(@PathVariable Long id, @RequestBody Account account) {
        Account storedAccount = dao.findById(id);
        storedAccount.setFirstName(account.getFirstName());
        storedAccount.setLastName(account.getLastName());
        storedAccount.setUsername(account.getUsername());
        storedAccount.setPassword(account.getPassword());
        return dao.persist(storedAccount);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id) {
        final Account account = dao.findById(id);
        dao.delete(account);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PRIVILEGEDUSER')")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Account> getAllAccounts() {
        return dao.findAll();
    }
}
