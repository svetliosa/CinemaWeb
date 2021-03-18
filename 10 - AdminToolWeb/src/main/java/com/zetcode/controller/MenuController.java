package com.zetcode.controller;

import com.zetcode.model.Employee;
import com.zetcode.model.EncryptPasswords;
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
import java.util.ArrayList;

@Controller
public class MenuController {

    @RequestMapping(value = "/Menu", method = RequestMethod.GET)
    public String menu(
            @RequestParam(value = "button", required = false) String button,
            Model model,  HttpServletRequest request) throws Exception {

        HttpSession mySession = request.getSession();
        if ("Create account".equals(button)) {
            if (mySession.isNew()) {
                mySession.invalidate();
                FileLogWrite.FileWrite("The session has expired","log.txt");
                return "index";
            }
            else
                return "redirect:/CreateAccount";

        } else if ("Edit account".equals(button)) {
            if (mySession.isNew()) {
                mySession.invalidate();
                FileLogWrite.FileWrite("The session has expired","log.txt");
                return "index";
            }
            else
                return "redirect:/EditAccount";

        } else if ("Search account".equals(button)) {
            if (mySession.isNew()) {
                mySession.invalidate();
                FileLogWrite.FileWrite("The session has expired","log.txt");
                return "index";
            }
            else
                return "SearchAccount";

        } else if ("Delete account".equals(button)) {
            if (mySession.isNew()) {
                mySession.invalidate();
                FileLogWrite.FileWrite("The session has expired","log.txt");
                return "index";
            }
            else
                return "DeleteAccount";

        } else if ("Show all".equals(button)) {
            if (mySession.isNew()) {
                mySession.invalidate();
                FileLogWrite.FileWrite("Show all employees","log.txt");
                return "index";
            } else {
                DriverManagerDataSource dataSource = new DriverManagerDataSource();
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUsername("mssqllogin");
                dataSource.setPassword("mssqllogin$");
                dataSource.setUrl("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=AdminToolWeb");
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT * FROM Accounts");
                ResultSet rs = st.executeQuery();
                ArrayList<Employee> allEmployees = new ArrayList<Employee>();
                EncryptPasswords dm = new EncryptPasswords();
                while (rs.next()) {
                    Employee emp = new Employee(rs.getInt("ID_DB"),
                            rs.getString("FirstName_DB"),
                            rs.getString("Surname_DB"),
                            rs.getString("FamilyName_DB"),
                            rs.getString("Address_DB"),
                            rs.getString("Username_DB"),
                            dm.decrypt(rs.getString("Password_DB")),
                            dm.decrypt(rs.getString("RepeatPassword_DB")),
                            rs.getString("Email_DB"));
                    allEmployees.add(emp);
                }
                    st.close();
                    connection.close();
                model.addAttribute("allEmployees", allEmployees);
                return "ShowAllEmployees";
            }
            
        } else if ("Exit".equals(button)) {
            HttpSession session = request.getSession(true);
            session.invalidate();
            FileLogWrite.FileWrite("The session was destroyed","log.txt");
            FileLogWrite.FileWrite("Exit","log.txt");
            return "index";
        }else {
            return "redirect:/Welcome";
        }
    }
}
