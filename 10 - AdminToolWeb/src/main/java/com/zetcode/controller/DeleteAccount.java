package com.zetcode.controller;

import com.zetcode.model.Employee;
import com.zetcode.model.FileLogWrite;
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
public class DeleteAccount {
    @RequestMapping(value = "/Delete", method = RequestMethod.GET)
    public String deleteAccount(
            @RequestParam(value = "usernameSearch", required = false) String usernameSearch,
            Model model, HttpServletRequest request
    ) throws SQLException, NamingException {
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
        Connection connection = dataSource.getConnection();
        PreparedStatement st2 = connection.prepareStatement(
                "DELETE FROM Accounts where Username_DB = ?");
        st2.setString(1, usernameSearch);
        int del = st2.executeUpdate();
        boolean resultSet = st2.execute();
        if (del > 0) {
            st2.close();
            connection.close();
            model.addAttribute("deleteSuccessfully", "deleteSuccessfully");
            FileLogWrite.FileWrite("Account is deleted successfully with username: " + usernameSearch,"log.txt");
            return "DeleteAccount";
        } else {
            connection.close();
            model.addAttribute("usernameSearch", usernameSearch);
            model.addAttribute("usernameNotFound", "usernameNotFound");
            return "DeleteAccount";
        }

    }


}
