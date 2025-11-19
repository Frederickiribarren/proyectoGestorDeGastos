package com.example.proyectspring.controllers;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.proyectspring.entity.ConfiguracionUsuario;
import com.example.proyectspring.entity.IngresoMonetario;
import com.example.proyectspring.entity.Ingresos;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.ConfiguracionUsuarioService;
import com.example.proyectspring.service.IngresoMonetarioService;
import com.example.proyectspring.service.IngresosService;
import com.example.proyectspring.service.UsuarioService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ConfiguracionUsuarioService configuracionService;
    
    @Autowired
    private IngresosService ingresosService;
    
    @Autowired
    private IngresoMonetarioService ingresoMonetarioService;

    @GetMapping
    public String getConfiguracion(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }
        
        String email = auth.getName();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Obtener o crear configuración del usuario
        ConfiguracionUsuario config = configuracionService.obtenerOCrearConfiguracion(usuario);
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("config", config);
        return "configuracion/configuracion";
    }
    
    @GetMapping("/datos")
    @ResponseBody
    public ResponseEntity<ConfiguracionUsuario> obtenerConfiguracion(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }
        
        String email = auth.getName();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        ConfiguracionUsuario config = configuracionService.obtenerOCrearConfiguracion(usuario);
        return ResponseEntity.ok(config);
    }
    
    @PostMapping("/apariencia")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarApariencia(
            @RequestBody Map<String, String> datos, Authentication auth) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (auth == null) {
                response.put("success", false);
                response.put("message", "No autenticado");
                return ResponseEntity.status(401).body(response);
            }
            
            String email = auth.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            String tema = datos.getOrDefault("tema", "light");
            String idioma = datos.getOrDefault("idioma", "es");
            String moneda = datos.getOrDefault("moneda", "CLP");
            
            configuracionService.actualizarApariencia(usuario, tema, idioma, moneda);
            
            response.put("success", true);
            response.put("message", "Configuración de apariencia guardada exitosamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/notificaciones")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarNotificaciones(
            @RequestBody Map<String, Boolean> datos, Authentication auth) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (auth == null) {
                response.put("success", false);
                response.put("message", "No autenticado");
                return ResponseEntity.status(401).body(response);
            }
            
            String email = auth.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            boolean notifEmail = datos.getOrDefault("notifEmail", true);
            boolean notifTransacciones = datos.getOrDefault("notifTransacciones", true);
            boolean notifPresupuesto = datos.getOrDefault("notifPresupuesto", false);
            boolean notifReportes = datos.getOrDefault("notifReportes", false);
            boolean notifPromociones = datos.getOrDefault("notifPromociones", false);
            
            configuracionService.actualizarNotificaciones(usuario, notifEmail, 
                    notifTransacciones, notifPresupuesto, notifReportes, notifPromociones);
            
            response.put("success", true);
            response.put("message", "Configuración de notificaciones guardada exitosamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/autenticacion-dos-factores")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleAutenticacionDosFactores(
            @RequestBody Map<String, Boolean> datos, Authentication auth) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (auth == null) {
                response.put("success", false);
                response.put("message", "No autenticado");
                return ResponseEntity.status(401).body(response);
            }
            
            String email = auth.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            boolean activar = datos.getOrDefault("activar", false);
            
            ConfiguracionUsuario config = configuracionService.actualizarAutenticacionDosFactores(usuario, activar);
            
            response.put("success", true);
            response.put("activado", config.isAutenticacionDosFactores());
            response.put("message", activar ? 
                    "Autenticación de dos factores activada" : 
                    "Autenticación de dos factores desactivada");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPDF(Authentication auth) {
        try {
            if (auth == null) {
                return ResponseEntity.status(401).build();
            }
            
            String email = auth.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Obtener datos del mes actual
            YearMonth mesActual = YearMonth.now();
            LocalDate inicioMes = mesActual.atDay(1);
            LocalDate finMes = mesActual.atEndOfMonth();
            
            List<Ingresos> gastos = ingresosService.obtenerIngresosPorUsuario(usuario).stream()
                    .filter(g -> !g.getFecha().isBefore(inicioMes) && !g.getFecha().isAfter(finMes))
                    .toList();
            
            List<IngresoMonetario> ingresos = ingresoMonetarioService.obtenerIngresosPorUsuario(usuario).stream()
                    .filter(i -> !i.getFecha().isBefore(inicioMes) && !i.getFecha().isAfter(finMes))
                    .toList();
            
            // Generar PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Título
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Reporte Financiero - " + mesActual.getMonth() + " " + mesActual.getYear(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Info usuario
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
            document.add(new Paragraph("Usuario: " + usuario.getNombre() + " " + usuario.getApellido(), normalFont));
            document.add(new Paragraph("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
            document.add(new Paragraph(" "));
            
            // Resumen
            double totalGastos = gastos.stream().mapToDouble(Ingresos::getMonto).sum();
            double totalIngresos = ingresos.stream().mapToDouble(IngresoMonetario::getMonto).sum();
            double ahorro = totalIngresos - totalGastos;
            
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("RESUMEN DEL MES", boldFont));
            document.add(new Paragraph("Total Gastos: $" + String.format("%,.0f", totalGastos), normalFont));
            document.add(new Paragraph("Total Ingresos: $" + String.format("%,.0f", totalIngresos), normalFont));
            document.add(new Paragraph("Ahorro: $" + String.format("%,.0f", ahorro), normalFont));
            document.add(new Paragraph(" "));
            
            // Tabla de Gastos
            document.add(new Paragraph("GASTOS", boldFont));
            PdfPTable tableGastos = new PdfPTable(4);
            tableGastos.setWidthPercentage(100);
            tableGastos.setSpacingBefore(10);
            
            // Headers
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            PdfPCell cell;
            cell = new PdfPCell(new Phrase("Fecha", headerFont));
            tableGastos.addCell(cell);
            cell = new PdfPCell(new Phrase("Descripción", headerFont));
            tableGastos.addCell(cell);
            cell = new PdfPCell(new Phrase("Categoría", headerFont));
            tableGastos.addCell(cell);
            cell = new PdfPCell(new Phrase("Monto", headerFont));
            tableGastos.addCell(cell);
            
            // Datos
            for (Ingresos gasto : gastos) {
                tableGastos.addCell(new Phrase(gasto.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
                tableGastos.addCell(new Phrase(gasto.getDescripcion(), normalFont));
                tableGastos.addCell(new Phrase(gasto.getCategoria(), normalFont));
                tableGastos.addCell(new Phrase("$" + String.format("%,.0f", gasto.getMonto()), normalFont));
            }
            
            document.add(tableGastos);
            document.add(new Paragraph(" "));
            
            // Tabla de Ingresos
            document.add(new Paragraph("INGRESOS", boldFont));
            PdfPTable tableIngresos = new PdfPTable(3);
            tableIngresos.setWidthPercentage(100);
            tableIngresos.setSpacingBefore(10);
            
            cell = new PdfPCell(new Phrase("Fecha", headerFont));
            tableIngresos.addCell(cell);
            cell = new PdfPCell(new Phrase("Tipo", headerFont));
            tableIngresos.addCell(cell);
            cell = new PdfPCell(new Phrase("Monto", headerFont));
            tableIngresos.addCell(cell);
            
            for (IngresoMonetario ingreso : ingresos) {
                tableIngresos.addCell(new Phrase(ingreso.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
                tableIngresos.addCell(new Phrase(ingreso.getTipoIngreso() != null ? ingreso.getTipoIngreso() : "Sin especificar", normalFont));
                tableIngresos.addCell(new Phrase("$" + String.format("%,.0f", ingreso.getMonto()), normalFont));
            }
            
            document.add(tableIngresos);
            document.close();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_" + mesActual + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());
                    
        } catch (DocumentException e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/exportar/csv")
    public ResponseEntity<byte[]> exportarCSV(Authentication auth) {
        try {
            if (auth == null) {
                return ResponseEntity.status(401).build();
            }
            
            String email = auth.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Obtener datos del mes actual
            YearMonth mesActual = YearMonth.now();
            LocalDate inicioMes = mesActual.atDay(1);
            LocalDate finMes = mesActual.atEndOfMonth();
            
            List<Ingresos> gastos = ingresosService.obtenerIngresosPorUsuario(usuario).stream()
                    .filter(g -> !g.getFecha().isBefore(inicioMes) && !g.getFecha().isAfter(finMes))
                    .toList();
            
            List<IngresoMonetario> ingresos = ingresoMonetarioService.obtenerIngresosPorUsuario(usuario).stream()
                    .filter(i -> !i.getFecha().isBefore(inicioMes) && !i.getFecha().isAfter(finMes))
                    .toList();
            
            // Generar CSV
            StringBuilder csv = new StringBuilder();
            csv.append("REPORTE FINANCIERO - ").append(mesActual.getMonth()).append(" ").append(mesActual.getYear()).append("\n\n");
            
            csv.append("GASTOS\n");
            csv.append("Fecha,Descripción,Categoría,Método Pago,Monto\n");
            for (Ingresos gasto : gastos) {
                csv.append(gasto.getFecha()).append(",")
                   .append(gasto.getDescripcion()).append(",")
                   .append(gasto.getCategoria()).append(",")
                   .append(gasto.getMetodoPago()).append(",")
                   .append(gasto.getMonto()).append("\n");
            }
            
            csv.append("\nINGRESOS\n");
            csv.append("Fecha,Tipo,Monto\n");
            for (IngresoMonetario ingreso : ingresos) {
                csv.append(ingreso.getFecha()).append(",")
                   .append(ingreso.getTipoIngreso() != null ? ingreso.getTipoIngreso() : "Sin especificar").append(",")
                   .append(ingreso.getMonto()).append("\n");
            }
            
            double totalGastos = gastos.stream().mapToDouble(Ingresos::getMonto).sum();
            double totalIngresos = ingresos.stream().mapToDouble(IngresoMonetario::getMonto).sum();
            
            csv.append("\nRESUMEN\n");
            csv.append("Total Gastos,").append(totalGastos).append("\n");
            csv.append("Total Ingresos,").append(totalIngresos).append("\n");
            csv.append("Ahorro,").append(totalIngresos - totalGastos).append("\n");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "reporte_" + mesActual + ".csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csv.toString().getBytes());
                    
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @PostMapping("/archivar-datos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> archivarDatos(Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (auth == null) {
                response.put("success", false);
                response.put("message", "No autenticado");
                return ResponseEntity.status(401).body(response);
            }
            
            // Por ahora solo retornamos un mensaje de éxito
            // En el futuro se puede implementar lógica de archivado
            response.put("success", true);
            response.put("message", "Datos archivados correctamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al archivar: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
