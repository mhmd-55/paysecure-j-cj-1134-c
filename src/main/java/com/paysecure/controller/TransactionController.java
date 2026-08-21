package com.paysecure.controller;

import com.paysecure.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * FINDING 4 (Part D) — REMEDIATED.
 * Uses a PreparedStatement with a bound parameter instead of string concatenation,
 * so user input is always treated as literal data, never as SQL syntax.
 * Mohammad Ismail CJ-1134-C
 */
@Controller
public class TransactionController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/transactions/search")
    public String searchTransactions(@RequestParam String keyword, Model model) throws SQLException {
        String sql = "SELECT id, description, amount FROM transactions WHERE description LIKE ?";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%"); // bound as data, never parsed as SQL
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(resultSet.getLong("id"));
                    transaction.setDescription(resultSet.getString("description"));
                    transaction.setAmount(resultSet.getDouble("amount"));
                    transactions.add(transaction);
                }
            }
        }
        model.addAttribute("transactions", transactions);
        return "transactions";
    }
}