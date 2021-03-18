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

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class EnterRecoveryCode {
    @RequestMapping(value = "/EnterCode", method = RequestMethod.GET)
    public String enterCode(
            @RequestParam(value = "code", required = false) String code,
            Model model,  HttpServletRequest request
    ) throws Exception {
        HttpSession mySession = request.getSession();
        if (mySession.isNew()) {
            mySession.invalidate();
            FileLogWrite.FileWrite("The session has expired","log.txt");
            return "index";
        }
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUsername("mssqllogin");
        dataSource.setPassword("mssqllogin$");
        dataSource.setUrl("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=AdminToolWeb");

        EncryptPasswords td = new EncryptPasswords();
        code = td.encrypt(code);
        Connection connection = dataSource.getConnection();
        PreparedStatement st = connection.prepareStatement(
                "SELECT * FROM Login where Code_DB = ?");
        st.setString(1, code);
        ResultSet resultSet = st.executeQuery();
        LoginData user = new LoginData();
        if (resultSet.next()) {
            st.close();
            connection.close();
            FileLogWrite.FileWrite("Recovery code is correct", "log.txt");
            return "changePassword";
        } else {
            st.close();
            connection.close();
            code = td.decrypt(code);
            model.addAttribute("stream", "Please enter your recovery code!");
            model.addAttribute("incorrectCode","incorrectCode");
            model.addAttribute("code",code);
            FileLogWrite.FileWrite("Recovery code is incorrect", "log.txt");
            return "EnterRecoveryCode";
        }
    }
}
