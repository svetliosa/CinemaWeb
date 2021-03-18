package com.zetcode.controller;

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
public class ForgotPasswordController {
    @RequestMapping(value = "/ForgotPassword", method = RequestMethod.GET)
    public String forgotPassword(Model model,  HttpServletRequest request) throws SQLException, NamingException {
        HttpSession mySession = request.getSession();
        model.addAttribute("stream", "Please enter your recovery code!");
        FileLogWrite.FileWrite("Forgot password","log.txt");
        return "EnterRecoveryCode";
    }
}
