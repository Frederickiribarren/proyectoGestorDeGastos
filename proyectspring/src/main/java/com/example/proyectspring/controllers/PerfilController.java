package com.example.proyectspring.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyectspring.dto.PerfilDTO;
import com.example.proyectspring.dto.SeguridadDTO;
import com.example.proyectspring.dto.PalabraSeguridadDTO;
import com.example.proyectspring.dto.EliminarCuentaDTO;
import com.example.proyectspring.entity.ConfiguracionUsuario;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.ConfiguracionUsuarioService;
import com.example.proyectspring.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class PerfilController {
        @Autowired
        private com.example.proyectspring.service.IngresoMonetarioService ingresoMonetarioService;
    @Autowired
    private com.example.proyectspring.service.IngresosService ingresosService;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ConfiguracionUsuarioService configuracionService;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new java.beans.PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    setValue(LocalDate.parse(text, DateTimeFormatter.ISO_DATE));
                }
            }
        });
    }

    @GetMapping("/perfil")
    public String getMostrarPerfil(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        // Obtener el usuario autenticado
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Crear DTO con los datos del usuario
        PerfilDTO perfilDTO = new PerfilDTO();
        perfilDTO.setNombre(usuario.getNombre());
        perfilDTO.setApellido(usuario.getApellido());
        perfilDTO.setEmail(usuario.getEmail());
        perfilDTO.setTelefono(usuario.getTelefono());
        perfilDTO.setPais(usuario.getPais());
        perfilDTO.setBiografia(usuario.getBiografia());
        perfilDTO.setFechaNacimiento(usuario.getFechaNacimiento());
        
        model.addAttribute("perfilDTO", perfilDTO);
        model.addAttribute("seguridadDTO", new SeguridadDTO());
        model.addAttribute("palabraSeguridadDTO", new PalabraSeguridadDTO());
        model.addAttribute("eliminarCuentaDTO", new EliminarCuentaDTO());
        model.addAttribute("usuario", usuario);

        // Estadísticas rápidas: número de transacciones y balance acumulado
        // Transacciones del mes actual
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.YearMonth mesActual = java.time.YearMonth.from(hoy);
        int numTransaccionesMes = (int) ingresosService.obtenerIngresosPorUsuario(usuario).stream()
            .filter(i -> i.getFecha() != null && java.time.YearMonth.from(i.getFecha()).equals(mesActual))
            .count();

        // Calcular balance acumulado: suma de ingresos monetarios menos gastos
        double totalIngresos = ingresoMonetarioService.obtenerIngresosPorUsuario(usuario).stream()
            .filter(i -> i.getFecha() != null && !i.getFecha().isAfter(hoy))
            .mapToDouble(i -> i.getMonto() != null ? i.getMonto() : 0.0)
            .sum();
        double totalGastos = ingresosService.obtenerIngresosPorUsuario(usuario).stream()
            .filter(i -> i.getFecha() != null && !i.getFecha().isAfter(hoy))
            .mapToDouble(i -> i.getMonto() != null ? i.getMonto() : 0.0)
            .sum();
        double balanceAcumulado = totalIngresos - totalGastos;

        model.addAttribute("numTransacciones", numTransaccionesMes);
        model.addAttribute("balanceAcumulado", balanceAcumulado);

        // Verificar si tiene 2FA activado en configuración
        ConfiguracionUsuario config = configuracionService.obtenerOCrearConfiguracion(usuario);
        boolean requiere2FA = config.isAutenticacionDosFactores() && usuario.getPalabraSeguridad() != null;

        model.addAttribute("tienePalabraSeguridad", usuario.getPalabraSeguridad() != null);
        model.addAttribute("requiere2FA", requiere2FA);

        return "perfil/perfil";
    }
    
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@Valid @ModelAttribute PerfilDTO perfilDTO,
                                   BindingResult result,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email).orElse(null);
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "Por favor, corrige los errores en el formulario");
            return "perfil/perfil";
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usuarioService.actualizarPerfil(usuario.getId(), perfilDTO);
            
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
            return "redirect:/perfil";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "perfil/perfil";
        }
    }
    
    @PostMapping("/perfil/cambiar-password")
    public String cambiarPassword(@Valid @ModelAttribute SeguridadDTO seguridadDTO,
                                  BindingResult result,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorSeguridad", "Por favor, corrige los errores");
            return "redirect:/perfil";
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            boolean cambioExitoso = usuarioService.cambiarPassword(usuario.getId(), seguridadDTO);
            
            if (cambioExitoso) {
                redirectAttributes.addFlashAttribute("mensajeSeguridad", "Contraseña actualizada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("errorSeguridad", "La contraseña actual es incorrecta o las nuevas contraseñas no coinciden");
            }
            
            return "redirect:/perfil";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorSeguridad", "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/perfil";
        }
    }
    
    @PostMapping("/perfil/configurar-palabra-seguridad")
    public String configurarPalabraSeguridad(@Valid @ModelAttribute PalabraSeguridadDTO palabraSeguridadDTO,
                                             BindingResult result,
                                             Principal principal,
                                             RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorPalabraSeguridad", "Por favor, corrige los errores");
            return "redirect:/perfil";
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usuarioService.configurarPalabraSeguridad(usuario.getId(), palabraSeguridadDTO.getPalabraSeguridad());
            
            redirectAttributes.addFlashAttribute("mensajePalabraSeguridad", "Palabra de seguridad configurada correctamente");
            return "redirect:/perfil";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorPalabraSeguridad", "Error al configurar palabra de seguridad: " + e.getMessage());
            return "redirect:/perfil";
        }
    }
    
    @PostMapping("/perfil/eliminar-cuenta")
    public String eliminarCuenta(@Valid @ModelAttribute EliminarCuentaDTO eliminarCuentaDTO,
                                 BindingResult result,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorEliminacion", "Datos incompletos");
            return "redirect:/perfil";
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Eliminar cuenta permanentemente
            usuarioService.eliminarCuentaPermanentemente(usuario.getId(), eliminarCuentaDTO);
            
            // Cerrar sesión manualmente
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            
            // Redirigir al login con mensaje de cuenta eliminada
            redirectAttributes.addFlashAttribute("mensajeEliminacion", "Tu cuenta ha sido eliminada permanentemente");
            return "redirect:/login?cuentaEliminada";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorEliminacion", e.getMessage());
            return "redirect:/perfil";
        }
    }
}
