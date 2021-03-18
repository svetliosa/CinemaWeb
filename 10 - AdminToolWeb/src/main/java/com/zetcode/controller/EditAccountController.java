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
public class EditAccountController {
    @RequestMapping(value = "/Edit", method = RequestMethod.POST)
    public String editAccount(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "secondName", required = false) String secondName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "repeatPassword", required = false) String repeatPassword,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "iddb", required = false) int id,
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
        Employee emp = new Employee(id,firstName, secondName, lastName, address, username, password, repeatPassword, email);

        try {
            PreparedStatement st2 = connection.prepareStatement(
                    "SELECT * FROM Accounts where Username_DB = ? and ID_DB != ?");
            st2.setString(1, emp.getUsername());
            st2.setInt(2, emp.getId());
            ResultSet resultSet = st2.executeQuery();
            LoginData user = new LoginData();
            if (resultSet.next()) {
                st2.close();
                connection.close();
                returnData("usernameExist", model, emp);
                return "EditAccount";
            }
        } catch (Exception e) {

        }

        if (!emp.getFirstName().matches("[A-Za-z]*") || emp.getFirstName().equals("")) {
            returnData("firstNameNull", model, emp);
            return "EditAccount";
        } else if (!emp.getSurname().matches("[A-Za-z]*") || emp.getSurname().equals("")) {
            returnData("secondNameNull", model, emp);
            return "EditAccount";
        } else if (!emp.getFamilyName().matches("[A-Za-z]*") || emp.getFamilyName().equals("")) {
            returnData("lastNameNull", model, emp);
            return "EditAccount";
        } else if (emp.getAddress().equals("")) {
            returnData("addressNull", model, emp);
            return "EditAccount";
        } else if (emp.getUsername().equals("")) {
            returnData("usernameNull", model, emp);
            return "EditAccount";
        } else if (emp.getPassword().equals("")) {
            returnData("passwordNull", model, emp);
            return "EditAccount";
        } else if (emp.getRepeatPassword().equals("")) {
            returnData("password2Null", model, emp);
            return "EditAccount";
        } else if (emp.getPassword().length() < 8 || emp.getRepeatPassword().length() < 8) {
            returnData("passwordsLength", model, emp);
            return "EditAccount";
        } else if (!emp.getPassword().equals(emp.getRepeatPassword())) {
            returnData("passwordsNotSame", model, emp);
            return "EditAccount";
        } else if (!emp.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$") || emp.getEmail().equals("")) {
            returnData("emailNull", model, emp);
            return "EditAccount";
        }


        try {
            EncryptPasswords td = new EncryptPasswords();
            emp.setPassword(td.encrypt(emp.getPassword()));
            emp.setRepeatPassword(td.encrypt(emp.getRepeatPassword()));
            Connection connection2 = dataSource.getConnection();
            PreparedStatement st = connection2.prepareStatement(
                    "UPDATE Accounts set FirstName_DB = ?, Surname_DB = ?, FamilyName_DB = ?, Address_DB = ?, Username_DB = ?, Password_DB = ?, RepeatPassword_DB = ?, Email_DB = ?  where ID_DB = ?");
            st.setString(1, emp.getFirstName());
            st.setString(2, emp.getSurname());
            st.setString(3, emp.getFamilyName());
            st.setString(4, emp.getAddress());
            st.setString(5, emp.getUsername());
            st.setString(6, emp.getPassword());
            st.setString(7, emp.getRepeatPassword());
            st.setString(8, emp.getEmail());
            st.setInt(9, emp.getId());
            st.executeQuery();
            st.close();
            connection2.close();
        } catch (Exception e) {

        }
        model.addAttribute("editSuccessfully", "editSuccessfully");
        FileLogWrite.FileWrite("Account is edited successfully with username: " + emp.getUsername(),"log.txt");
        return "redirect:/EditAccount";
    }



    public void returnData(String error, Model model, Employee emp){
        model.addAttribute(error,error);
        model.addAttribute("firstName", emp.getFirstName());
        model.addAttribute("secondName", emp.getSurname());
        model.addAttribute("lastName", emp.getFamilyName());
        model.addAttribute("address", emp.getAddress());
        model.addAttribute("username", emp.getUsername());
        model.addAttribute("password", emp.getPassword());
        model.addAttribute("repeatPassword", emp.getRepeatPassword());
        model.addAttribute("email", emp.getEmail());
        model.addAttribute("firstNameVisible", "true");
        model.addAttribute("secondNameVisible","true");
        model.addAttribute("lastNameVisible","true");
        model.addAttribute("addressVisible","true");
        model.addAttribute("usernameVisible","true");
        model.addAttribute("passwordVisible","true");
        model.addAttribute("repeatPasswordVisible","true");
        model.addAttribute("emailVisible","true");
        model.addAttribute("buttonSaveVisible","true");
    }

    @RequestMapping(value = "/EditAccount", method = RequestMethod.GET)
    public String editAccount(Model model) throws SQLException, NamingException {
        return "EditAccount";
    }
}
