package study.securitytest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDetails admin() {
         return User.builder().username("user1").password(passwordEncoder.encode("1234")).roles("ADMIN").build();
    }

    private UserDetails normal() {
        return User.builder().username("admin").password(passwordEncoder.encode("1234")).roles("USER").build();
    }

    @DisplayName("1. user 로 user 페이지에 접근할 수 있어야 한다.")
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void test1() throws Exception {

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/user")).andReturn().getResponse().getContentAsString();

        SecurityMessage securityMessage = mapper.readValue(result, SecurityMessage.class);

        assertEquals("user page", securityMessage.getMessage());
    }

    @DisplayName("2. user 로 admin 페이지에 접근할 수 없다.")
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void test2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin")).andExpect(status().is4xxClientError());
    }


    @Test
    @DisplayName("3. admin이 user와 admin 페이지에 접근할 수 있다")
    void test3() throws Exception {
        SecurityMessage message = mapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.get("/user").with(user(admin()))).andReturn().getResponse().getContentAsString(),
                SecurityMessage.class);

        assertEquals("user page", message.getMessage());

        message = mapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.get("/admin").with(user(admin()))).andReturn().getResponse().getContentAsString(),
                SecurityMessage.class
        );

        assertEquals("admin page", message.getMessage());
    }
}
