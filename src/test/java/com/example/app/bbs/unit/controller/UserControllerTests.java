package com.example.app.bbs.unit.controller;

import com.example.app.bbs.app.controller.UserController;
import com.example.app.bbs.domain.entity.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private
    MockMvc mockMvc = null;

    @Autowired
    UserController target;

    @Test
    public void getUserSignupTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/user/signup")
        )
        .andExpect(status().isOk());
    }

    @Test
    public void postUserSignupNormalTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test1")
                        .param("email","test1@example.com")
                        .param("password","test1")
                        .param("role", UserRole.USER.name())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/user/login"));
    }

    @Test
    @Sql(statements = "INSERT INTO users (name, email, password, role) VALUES ('test2', 'test2@example.com', 'test2', 'USER');")
    public void postUserSignupRegisteredErrorTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test2")
                        .param("email","test2@example.com")
                        .param("password","test2")
                        .param("role", UserRole.USER.name())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/user/signup"))
        .andExpect(flash().attributeExists("errors"))
        .andExpect(flash().attributeExists("request"));
    }

    @Test
    public void postUserSignupPasswordShortTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test2")
                        .param("email","test2@example.com")
                        .param("password","a")
                        .param("role", UserRole.USER.name())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/user/signup"))
        .andExpect(flash().attributeExists("errors"))
        .andExpect(flash().attributeExists("request"));
    }

    @Test
    public void postUserSignupPasswordLongTest() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test2")
                        .param("email","test2@example.com")
                        .param("password","123456789012345")
                        .param("role", UserRole.USER.name())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/user/signup"))
        .andExpect(flash().attributeExists("errors"))
        .andExpect(flash().attributeExists("request"));
    }

    @Test
    public void postUserSignupPasswordCharErrorTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test2")
                        .param("email","test2@example.com")
                        .param("password","あいうえお")
                        .param("role", UserRole.USER.name())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/user/signup"))
        .andExpect(flash().attributeExists("errors"))
        .andExpect(flash().attributeExists("request"));
    }

    @Test
    public void postUserSignupAdminRegistrationTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test2")
                        .param("email","test2@example.com")
                        .param("password","1234")
                        .param("role", UserRole.ADMIN.name())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/user/signup"))
        .andExpect(flash().attributeExists("errors"))
        .andExpect(flash().attributeExists("request"));
    }

    @Test
    public void getUserLoginTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/user/login")
        )
                .andExpect(status().isOk());
    }

    @Test
    @Sql(statements = "INSERT INTO users (name, email, password, role) VALUES ('test3', 'test3@example.com', '$2a$10$MMnbIXYB4BQI88yiKpiR2eiIIHiUEymGMyWqWlp01Iz.aqbD3ud4i', 'USER');")
    public void userLoginAuthTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/login/auth")
                        .with(csrf())
                        .param("email","test3@example.com")
                        .param("password","test3")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
    }

    @Test
    @Sql(statements = "INSERT INTO users (name, email, password, role) VALUES ('test4', 'test4@example.com', 'dummy', 'USER');")
    @WithUserDetails(value = "test4")
    public void authenticationTest() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.get("/user/index")
        )
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("page"))
        .andExpect(model().attributeExists("user"));
    }

    @Test
    @Sql(statements = "INSERT INTO users (name, email, password, role) VALUES ('test5', 'test5@example.com', 'dummy', 'USER');")
    @WithUserDetails(value = "test5")
    public void getUserIndexTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/user/index")
        )
        .andExpect(status().isOk());
    }

    @Test
    @Sql(statements = "INSERT INTO users (name, email, password, role) VALUES ('test6', 'test6@example.com', 'dummy', 'USER');")
    @WithUserDetails(value = "test6")
    public void registerArticleTest() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/article/register")
                        .with(csrf())
                        .param("name", "test")
                        .param("title", "test")
                        .param("contents", "test")
                        .param("articleKey", "test")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/user/index"))
        .andExpect(flash().attributeExists("message"))
        .andExpect(flash().attribute("message",
                target.MESSAGE_REGISTER_NORMAL));
    }

    @Test
    @Sql(statements = "INSERT INTO users (name, email, password, role) VALUES ('test7', 'test7@example.com', 'dummy', 'USER');")
    @WithUserDetails(value = "test7")
    public void registerArticleRequestErrorTest() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/article/register")
                        .with(csrf())
                        .param("name", "")
                        .param("title", "")
                        .param("contents", "")
                        .param("articleKey", "")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/user/index"))
        .andExpect(flash().attributeExists("errors"))
        .andExpect(flash().attributeExists("request"));
    }
}