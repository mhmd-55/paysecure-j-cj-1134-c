package com.paysecure.controller;

import com.paysecure.entity.Account;
import com.paysecure.repository.AccountRepository;
import com.paysecure.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

/**
 * FINDING 9 (Part I) — REMEDIATED.
 * fromAccount is now verified to belong to the authenticated caller before any
 * transfer executes. CSRF protection is re-enabled separately in SecurityConfig.
 * Mohammad Ismail CJ-1134-C
 */
@Controller
public class TransferController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/transfer")
    @ResponseBody
    public String transferMoney(@RequestParam Long fromAccount, @RequestParam Long toAccount,
                                 @RequestParam double amount, HttpSession session) {
        Object sessionUsername = session.getAttribute("username");
        if (sessionUsername == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to transfer funds.");
        }

        Account source = accountRepository.findById(fromAccount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source account not found."));

        if (!source.getOwnerUsername().equals(sessionUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "fromAccount does not belong to you.");
        }

        accountService.transfer(fromAccount, toAccount, amount);
        return "Transfer successful.";
    }
}