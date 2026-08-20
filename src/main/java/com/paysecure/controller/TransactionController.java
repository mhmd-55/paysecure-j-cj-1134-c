package com.paysecure.controller;

import com.paysecure.config.AppConfiguration;
import com.paysecure.entity.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FINDING 4 (Part D): classic SQL injection via string concatenation.
 * Try: keyword=%' UNION SELECT id, username, password FROM users --
 */
@Controller
public class TransactionController {

    @GetMapping("/transactions/search")
    public String searchTransactions(@RequestParam String keyword, Model model) throws SQLException {
        Connection connection = DriverManager.getConnection(
                AppConfiguration.DB_URL, AppConfiguration.DB_USER, AppConfiguration.DB_PASSWORD);
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM transactions WHERE description LIKE '%" + keyword + "%'";
        ResultSet resultSet = statement.executeQuery(query);

        List<Transaction> transactions = new ArrayList<>();
        while (resultSet.next()) {
            Transaction transaction = new Transaction();
            transaction.setId(resultSet.getLong("id"));
            transaction.setDescription(resultSet.getString("description"));
            transaction.setAmount(resultSet.getDouble("amount"));
            transactions.add(transaction);
        }
        connection.close();
        model.addAttribute("transactions", transactions);
        return "transactions";
    }
}
