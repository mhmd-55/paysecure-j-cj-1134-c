package com.paysecure.controller;

import com.paysecure.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * FINDING 9 (Part I): CSRF disabled globally (see SecurityConfig), and no check
 * that fromAccount belongs to the authenticated caller.
 */
@Controller
public class TransferController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/transfer")
    public String transferMoney(@RequestParam Long fromAccount, @RequestParam Long toAccount, @RequestParam double amount) {
        accountService.transfer(fromAccount, toAccount, amount);
        return "transfer-success";
    }
}
