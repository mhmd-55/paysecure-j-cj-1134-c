package com.paysecure;

import com.paysecure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Automated security tests - evidence tag CJ-1134-C, student mhm0231134.
 * Both tests run against the FIXED code and confirm the vulnerable behavior
 * no longer occurs. Requires a running local MySQL instance matching
 * application.properties (same DB the app itself uses).
 * Mohammad Ismail CJ-1134-C
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityFindingsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    /**
     * Finding 6 retest: a caller with no ADMIN session attribute must NOT be able
     * to delete a user, even if they still supply role=ADMIN in the request the
     * way the original vulnerable code trusted. Also confirms the target account
     * still exists afterward - not just that the HTTP call was rejected.
     */
    @Test
    void adminDeleteUser_shouldNotTrustClientSuppliedRole_CJ1134C() throws Exception {
        mockMvc.perform(post("/admin/deleteUser")
                        .param("id", "1434")
                        .param("role", "ADMIN")) // old exploit shape - must now be ignored entirely
                .andExpect(status().isForbidden());

        assertTrue(userRepository.findById(1434L).isPresent(),
                "testuser1434 must still exist - the delete must have been blocked, not silently ignored");
    }

    /**
     * Finding 4 retest: a UNION-based SQL injection payload in the search keyword
     * must not return rows from the users table. The fixed PreparedStatement-based
     * query treats the entire payload as a literal search string.
     */
    @Test
    void transactionSearch_shouldRejectSqlInjectionPayload_CJ1134C() throws Exception {
        String payload = "zzzznomatch%' UNION SELECT id, id, 0, password FROM users -- ";

        mockMvc.perform(get("/transactions/search").param("keyword", payload))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("admin2434"))))
                .andExpect(content().string(not(containsString("$2b$"))))
                .andExpect(content().string(not(containsString("$2a$"))));
    }
}