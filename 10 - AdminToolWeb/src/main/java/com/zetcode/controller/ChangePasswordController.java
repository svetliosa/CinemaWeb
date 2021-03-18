package com.zetcode.controller;

import com.zetcode.model.EncryptPasswords;
import com.zetcode.model.FileLogWrite;
import com.zetcode.model.LoginData;
import com.zetcode.model.RecoveryCodeGenerator;
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class ChangePasswordController {
    @RequestMapping(value = "/ChangePassword", method = RequestMethod.POST)
    public String changePassword(
            @RequestParam(value = "newUsername", required = false) String newUsername,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            Model model,
            HttpServletRequest request
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
        if (newUsername.equals("")) {
            returnData("usernameNull", model, newUsername, newPassword);
            return "changePassword";
        } else if (newUsername.length() < 6) {
            returnData("usernameLength", model, newUsername, newPassword);
            return "changePassword";
        } else if (newPassword.equals("")) {
            returnData("passwordNull", model, newUsername, newPassword);
            return "changePassword";
        } else if (newPassword.length() < 8) {
            returnData("passwordLength", model, newUsername, newPassword);
            return "changePassword";
        }
        try {
            EncryptPasswords td = new EncryptPasswords();
            newPassword = td.encrypt(newPassword);
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "UPDATE Login set Username_DB = ?, Password_DB = ? where ID_DB=1");
            st.setString(1, newUsername);
            st.setString(2, newPassword);
            st.executeQuery();
            st.close();
            connection.close();
        } catch (Exception e){

        }
        String recoveryCode = RecoveryCodeGenerator.randomAlphaNumeric(8);
        model.addAttribute("code", recoveryCode);
        model.addAttribute("updateSuccessfully", "updateSuccessfully");
        FileLogWrite.FileWrite("Change password/username successfully","log.txt");
        return "RecoveryCode";

    }

    public void returnData(String error, Model model, String username, String password){
        model.addAttribute(error,error);
        model.addAttribute("newUsername",username);
        model.addAttribute("newPassword",password);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePassword(Model model) throws SQLException, NamingException {
        return "changePassword";
    }
}
