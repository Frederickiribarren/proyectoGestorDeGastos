package com.example.proyectspring.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailFileDTO {
    private String toUser;
    private String subject;
    private String massage;
    MultipartFile file;
}
