package com.example.proyectspring.entity;

import java.util.Date;

public class Usuario {
    private String nombre;
    private String rut;
    private String apellido;
    private String email;
    private String password;
    private String rol;
    private Date creatAt;
    private Date updateAt;

    public Usuario(String nombre, String rut, String apellido, String email, String password, String rol) {
        this.nombre = nombre;
        this.rut = rut;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
       
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Date getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(Date creatAt) {
        this.creatAt = creatAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

}
