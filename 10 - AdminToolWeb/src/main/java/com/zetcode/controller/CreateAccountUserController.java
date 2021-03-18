package com.zetcode.controller;

import com.zetcode.model.Employee;
import com.zetcode.model.EncryptPasswords;
import com.zetcode.model.FileLogWrite;
import com.zetcode.model.LoginData;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class CreateAccountUserController {

    @RequestMapping(value = "/CreateUserAccount", method = RequestMethod.POST)

    public String createAccount(
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            Model model,
            HttpServletRequest request
    ) throws SQLException, NamingException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUsername("mssqllogin");
        dataSource.setPassword("mssqllogin$");
        dataSource.setUrl("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=AdminToolWeb");
        Connection connection = dataSource.getConnection();
        PreparedStatement st2 = connection.prepareStatement(
                "SELECT * FROM Accounts where Username_DB = ?");
        st2.setString(1, username);
        ResultSet resultSet = st2.executeQuery();
        Employee emp = new Employee();
        emp.setFirstName(fullName);
        emp.setEmail(email);
        emp.setUsername(username);
        emp.setPassword(password);

        if (resultSet.next()) {
            st2.close();
            connection.close();
            returnData("usernameExist", model, emp);
            return "SignupCinema";
        }

        if (!emp.getFirstName().matches("[A-Za-z]*") || emp.getFirstName().equals("")) {
            returnData("firstNameNull", model, emp);
            return "SignupCinema";
        } else if (emp.getUsername().equals("")) {
            returnData("usernameNull", model, emp);
            return "SignupCinema";
        } else if (emp.getPassword().length() < 8) {
            returnData("passwordsLength", model, emp);
            return "SignupCinema";
        } else if (!emp.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$") || emp.getEmail().equals("")) {
            returnData("emailNull", model, emp);
            return "SignupCinema";
        }



        try {
            EncryptPasswords td = new EncryptPasswords();
            emp.setPassword(td.encrypt(emp.getPassword()));
            Connection connection2 = dataSource.getConnection();
            PreparedStatement st = connection2.prepareStatement(
                    "INSERT INTO Accounts (FirstName_DB, Surname_DB, FamilyName_DB, Address_DB, Username_DB, Password_DB, RepeatPassword_DB, Email_DB, Type_DB) VALUES (?,?,?,?,?,?,?,?,?)");
            st.setString(1, emp.getFirstName());
            st.setString(2, "null");
            st.setString(3, "null");
            st.setString(4, "null");
            st.setString(5, emp.getUsername());
            st.setString(6, emp.getPassword());
            st.setString(7, "null");
            st.setString(8, emp.getEmail());
            st.setString(9, "2");
            st.executeQuery();
            st.close();
            connection2.close();
        } catch (Exception e) {

        }
        model.addAttribute("createSuccessfully", "createSuccessfully");
        return "LoginCinema";
    }



    public void returnData(String error, Model model, Employee emp){
        model.addAttribute(error,error);
        model.addAttribute("firstName", emp.getFirstName());
        model.addAttribute("username", emp.getUsername());
        model.addAttribute("password", emp.getPassword());
        model.addAttribute("email", emp.getEmail());
    }

    @RequestMapping(value = "/CreateUserAccount", method = RequestMethod.GET)
    public String createAccount(Model model) throws SQLException, NamingException {
        return "CreateUserAccount";
    }

}
