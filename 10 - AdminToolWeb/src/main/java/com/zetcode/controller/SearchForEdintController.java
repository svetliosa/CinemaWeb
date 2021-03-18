package com.zetcode.controller;

import com.zetcode.model.Employee;
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
public class SearchForEdintController {
    @RequestMapping(value = "/SearchForEdit", method = RequestMethod.GET)
    public String search(
            @RequestParam(value = "usernameSearch", required = false) String usernameSearch,
            Model model, HttpServletRequest request
    ) throws Exception {
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
        Connection connection = dataSource.getConnection();
        PreparedStatement st2 = connection.prepareStatement(
                "SELECT * FROM Accounts where Username_DB = ?");
        st2.setString(1, usernameSearch);
        ResultSet resultSet = st2.executeQuery();
        Employee emp = new Employee();
        if (resultSet.next()) {
            emp.setId(resultSet.getInt("ID_DB"));
            emp.setFirstName(resultSet.getString("FirstName_DB"));
            emp.setSurname(resultSet.getString("Surname_DB"));
            emp.setFamilyName(resultSet.getString("FamilyName_DB"));
            emp.setAddress(resultSet.getString("Address_DB"));
            emp.setUsername(resultSet.getString("Username_DB"));
            emp.setPassword(resultSet.getString("Password_DB"));
            emp.setRepeatPassword(resultSet.getString("RepeatPassword_DB"));
            emp.setEmail(resultSet.getString("Email_DB"));
            st2.close();
            connection.close();
            EncryptPasswords td = new EncryptPasswords();
            emp.setPassword(td.decrypt(emp.getPassword()));
            emp.setRepeatPassword(td.decrypt(emp.getRepeatPassword()));
            model.addAttribute("firstNameVisible", "true");
            model.addAttribute("secondNameVisible","true");
            model.addAttribute("lastNameVisible","true");
            model.addAttribute("addressVisible","true");
            model.addAttribute("usernameVisible","true");
            model.addAttribute("passwordVisible","true");
            model.addAttribute("repeatPasswordVisible","true");
            model.addAttribute("emailVisible","true");
            model.addAttribute("buttonSaveVisible","true");
            model.addAttribute("iddb",emp.getId());
            model.addAttribute("firstName",emp.getFirstName());
            model.addAttribute("secondName",emp.getSurname());
            model.addAttribute("lastName",emp.getFamilyName());
            model.addAttribute("address",emp.getAddress());
            model.addAttribute("username",emp.getUsername());
            model.addAttribute("password",emp.getPassword());
            model.addAttribute("repeatPassword",emp.getRepeatPassword());
            model.addAttribute("email",emp.getEmail());
            return "EditAccount";
        } else {
            model.addAttribute("usernameSearch",usernameSearch);
            model.addAttribute("usernameNotFound", "usernameNotFound");
            return "EditAccount";
        }
    }
}
