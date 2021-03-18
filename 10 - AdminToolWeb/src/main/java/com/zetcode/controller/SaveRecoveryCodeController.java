package com.zetcode.controller;

import com.zetcode.model.EncryptPasswords;
import com.zetcode.model.FileLogWrite;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Controller
public class SaveRecoveryCodeController {
    @RequestMapping(value = "/SaveCode", method = RequestMethod.GET)
    public String saveCode(
            @RequestParam(value = "code", required = false) String code,
            Model model,  HttpServletRequest request
    ) throws SQLException, NamingException {
        HttpSession mySession = request.getSession();
        if (mySession.isNew()) {
            mySession.invalidate();
            return "index";
        }
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUsername("mssqllogin");
        dataSource.setPassword("mssqllogin$");
        dataSource.setUrl("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=AdminToolWeb");

        try {
            EncryptPasswords td = new EncryptPasswords();
            code = td.encrypt(code);
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "UPDATE Login set Code_DB=? where ID_DB=1");
            st.setString(1, code);
            st.executeQuery();
            st.close();
            connection.close();
        } catch (Exception e) {

        }
        FileLogWrite.FileWrite("Recovery code generated","log.txt");
        return "Welcome";

    }


    @RequestMapping(value = "/RecoveryCode", method = RequestMethod.GET)
    public String changePassword(Model model) throws SQLException, NamingException {
        return "RecoveryCode";
    }
}
