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
public class LoginController {


    @RequestMapping(value = "/Login", method = RequestMethod.GET)
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
            return "index";
        } else if (password.equals("")) {
            model.addAttribute("passwordNull","passwordNull");
            model.addAttribute("username",username);
            model.addAttribute("password",password);
            return "index";
        }
        EncryptPasswords td = new EncryptPasswords();
        password = td.encrypt(password);
        Connection connection = dataSource.getConnection();
        PreparedStatement st = connection.prepareStatement(
                "SELECT * FROM Login where Username_DB = ? and Password_DB = ?");
        st.setString(1, username);
        st.setString(2, password);
        ResultSet resultSet = st.executeQuery();
        LoginData user = new LoginData();
        if (resultSet.next()) {
            HttpSession mySession = request.getSession();
            mySession.setMaxInactiveInterval(60*30);
            FileLogWrite.FileWrite("The session is created with a duration: " +mySession.getMaxInactiveInterval()/60 + " minutes","log.txt");

            user.setId(resultSet.getInt("ID_DB"));
            user.setUsername(resultSet.getString("Username_DB"));
            user.setPassword(resultSet.getString("Password_DB"));
            user.setCode(resultSet.getString("Code_DB"));
            user.setPassword(td.decrypt(user.getPassword()));

           if (user.getUsername().equals("admin") && user.getPassword().equals("admin")){
               st.close();
               connection.close();
               FileLogWrite.FileWrite("Login for first time","log.txt");
               return "changePassword";
           } else {
               st.close();
               connection.close();
               FileLogWrite.FileWrite("Login successfully","log.txt");
               return "Welcome";

           }


        }
        st.close();
        connection.close();
        model.addAttribute("invalid","invalid");
        model.addAttribute("username",username);
        model.addAttribute("password",password = td.decrypt(password));
        FileLogWrite.FileWrite("Login failed","log.txt");
        return "index";
    }
}

