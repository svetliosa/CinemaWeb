package com.zetcode.controller;


import com.zetcode.model.EncryptPasswords;
import com.zetcode.model.FileLogWrite;
import com.zetcode.model.LoginData;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.http.HttpRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class LoginUserController {


    @RequestMapping(value = "/LoginUserController", method = RequestMethod.GET)
    public String login(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            Model model,
            HttpServletRequest request
    ) throws Exception {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUsername("mssqllogin");
        dataSource.setPassword("mssqllogin$");
        dataSource.setUrl("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=AdminToolWeb");

        if (username.equals("")) {
            model.addAttribute("usernameNull","usernameNull");
            model.addAttribute("username",username);
            model.addAttribute("password",password);
            return "LoginCinema";
        } else if (password.equals("")) {
            model.addAttribute("passwordNull","passwordNull");
            model.addAttribute("username",username);
            model.addAttribute("password",password);
            return "LoginCinema";
        }
        EncryptPasswords td = new EncryptPasswords();
        password = td.encrypt(password);
        Connection connection = dataSource.getConnection();
        PreparedStatement st = connection.prepareStatement(
                "SELECT * FROM Accounts where Username_DB = ? and Password_DB = ? and Type_DB = 2");
        st.setString(1, username);
        st.setString(2, password);
        ResultSet resultSet = st.executeQuery();
        LoginData user = new LoginData();
        if (resultSet.next()) {
            st.close();
            connection.close();
            return "jsp/Movies";
        }
        st.close();
        connection.close();
        model.addAttribute("invalid","invalid");
        model.addAttribute("username",username);
        model.addAttribute("password",password = td.decrypt(password));
        return "LoginCinema";
    }

    @RequestMapping(value = "/LoginUserController", method = RequestMethod.POST)
    public String changeToSignUp() {
            return "SignupCinema";
    }
}

