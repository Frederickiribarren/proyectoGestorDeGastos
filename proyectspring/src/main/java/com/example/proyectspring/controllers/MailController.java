package com.example.proyectspring.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyectspring.dto.EmailDTO;
import com.example.proyectspring.dto.EmailFileDTO;
import com.example.proyectspring.service.IEmailService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private IEmailService mailService;

    @PostMapping("/sendMail")
    public ResponseEntity<?> reciveResquestEmail(@RequestBody EmailDTO emailDTO) {

        mailService.sendEmail(emailDTO.getToUser(), emailDTO.getSubject(), emailDTO.getMassage());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email Enviado");

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/sendMailFile")
    public ResponseEntity<?> reciveResquestEmailFile(@ModelAttribute EmailFileDTO emailFileDTO) {

       

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email con archivo Enviado");

        return ResponseEntity.ok(response);
    }
    
    
}
