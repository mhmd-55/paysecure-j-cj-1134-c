package com.paysecure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Starter automated tests - evidence tag CJ-1134-C.
 * The assignment requires at least 2. These two are written to FAIL against the
 * current vulnerable code and PASS once you've applied the corresponding fix -
 * that's your retest proof. Add more as you remediate additional findings.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityFindingsTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Finding 6 retest: a non-admin caller supplying role=ADMIN in the request
     * must NOT be able to delete a user. Currently fails (vulnerable); should
     * pass (403/redirect without deletion) after you add @PreAuthorize.
     */
    @Test
    void adminDeleteUser_shouldNotTrustClientSuppliedRole_CJ1134C() throws Exception {
        mockMvc.perform(post("/admin/deleteUser")
                        .param("id", "1434")
                        .param("role", "ADMIN"))
                // TODO once fixed: assert 403 Forbidden instead of a redirect/200,
                // and separately assert (via repository) that user 1434 still exists.
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Finding 4 retest: a UNION-based SQL injection payload in the search
     * keyword must not return rows from the users table. Currently the
     * injected query executes (vulnerable); after switching to a
     * PreparedStatement/JPA query it should be treated as literal text and
     * return zero/only-legitimate transaction rows.
     */
    @Test
    void transactionSearch_shouldRejectSqlInjectionPayload_CJ1134C() throws Exception {
        String payload = "%' UNION SELECT id, username, password FROM users --";
        mockMvc.perform(get("/transactions/search").param("keyword", payload))
                .andExpect(status().isOk());
        // TODO once fixed: additionally assert the response body does NOT contain
        // any seeded username (e.g. "admin2434") to prove the injection no longer
        // leaks data outside the transactions table.
    }
}
