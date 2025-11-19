package com.example.proyectspring.service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyectspring.dao.IIngresosDao;
import com.example.proyectspring.entity.ConfiguracionNotificaciones;
import com.example.proyectspring.entity.Ingresos;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.repository.ConfiguracionNotificacionesRepository;
import com.example.proyectspring.service.impl.EmailServiceImpl;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Service
public class NotificacionService {

    @Autowired
    private ConfiguracionNotificacionesRepository configuracionRepo;
    
    @Autowired
    private EmailServiceImpl emailService;
    
    @Autowired
    private IIngresosDao ingresosDao;

    @Transactional(readOnly = true)
    public ConfiguracionNotificaciones obtenerConfiguracion(Long usuarioId) {
        return configuracionRepo.findByUsuarioId(usuarioId).orElse(null);
    }

    @Transactional
    public ConfiguracionNotificaciones guardarConfiguracion(ConfiguracionNotificaciones config) {
        return configuracionRepo.save(config);
    }

    @Transactional
    public ConfiguracionNotificaciones crearConfiguracionPorDefecto(Usuario usuario) {
        ConfiguracionNotificaciones config = new ConfiguracionNotificaciones(usuario);
        return configuracionRepo.save(config);
    }

    public void enviarNotificacionTransaccion(Usuario usuario, String tipo, Double monto, String descripcion) {
        ConfiguracionNotificaciones config = obtenerConfiguracion(usuario.getId());
        
        if (config == null || !config.isNotificacionesEmail() || !config.isAlertasTransacciones()) {
            return;
        }

        String asunto = "Nueva Transacción - PocketBook";
        String htmlContent = crearEmailTransaccion(usuario.getNombre(), tipo, monto, descripcion);
        
        emailService.sendHtmlEmail(usuario.getEmail(), asunto, htmlContent);
    }

    public void enviarReporteMensual(Usuario usuario, YearMonth mesAnterior) {
        ConfiguracionNotificaciones config = obtenerConfiguracion(usuario.getId());
        
        if (config == null || !config.isNotificacionesEmail() || !config.isReportesMensuales()) {
            return;
        }

        // Obtener transacciones del mes anterior
        LocalDate inicio = mesAnterior.atDay(1);
        LocalDate fin = mesAnterior.atEndOfMonth();
        
        java.util.List<Ingresos> transacciones = ingresosDao.findByUsuario(usuario).stream()
            .filter(t -> !t.getFecha().isBefore(inicio) && !t.getFecha().isAfter(fin))
            .collect(Collectors.toList());

        if (transacciones.isEmpty()) {
            return; // No enviar si no hay transacciones
        }

        // Calcular totales
        double totalIngresos = transacciones.stream()
            .filter(t -> t.getMonto() > 0)
            .mapToDouble(Ingresos::getMonto)
            .sum();

        double totalGastos = Math.abs(transacciones.stream()
            .filter(t -> t.getMonto() < 0)
            .mapToDouble(Ingresos::getMonto)
            .sum());

        double balance = totalIngresos - totalGastos;

        // Categorías principales
        Map<String, Double> gastosPorCategoria = transacciones.stream()
            .filter(t -> t.getMonto() < 0)
            .collect(Collectors.groupingBy(
                Ingresos::getCategoria,
                Collectors.summingDouble(t -> Math.abs(t.getMonto()))
            ));

        String mesNombre = mesAnterior.getMonth().getDisplayName(TextStyle.FULL, Locale.of("es", "ES"));
        String asunto = "Reporte Mensual " + mesNombre + " " + mesAnterior.getYear() + " - PocketBook";
        
        // Generar PDF
        File pdfFile = generarPdfReporteMensual(
            usuario.getNombre(),
            mesAnterior,
            totalIngresos,
            totalGastos,
            balance,
            gastosPorCategoria,
            transacciones
        );
        
        String htmlContent = crearEmailReporteMensual(
            usuario.getNombre(),
            mesAnterior,
            totalIngresos,
            totalGastos,
            balance,
            gastosPorCategoria,
            transacciones.size()
        );
        
        // Enviar email con PDF adjunto
        emailService.sendEmailWithFile(usuario.getEmail(), asunto, htmlContent, pdfFile);
        
        // Eliminar archivo temporal
        if (pdfFile != null && pdfFile.exists()) {
            pdfFile.delete();
        }
    }

    private String crearEmailTransaccion(String nombre, String tipo, Double monto, String descripcion) {
        String icono = tipo.equals("GASTO") ? "💸" : "💰";
        String color = tipo.equals("GASTO") ? "#e74c3c" : "#27ae60";
        String montoFormateado = String.format("$%,.0f", Math.abs(monto));

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .transaccion { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid %s; }
                    .monto { font-size: 32px; font-weight: bold; color: %s; margin: 10px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s Nueva Transacción</h1>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        <p>Se ha registrado una nueva transacción en tu cuenta:</p>
                        <div class="transaccion">
                            <div><strong>Tipo:</strong> %s</div>
                            <div class="monto">%s</div>
                            <div><strong>Descripción:</strong> %s</div>
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2025 PocketBook - Todos los derechos reservados</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(color, color, icono, nombre, tipo, montoFormateado, descripcion);
    }

    private String crearEmailReporteMensual(String nombre, YearMonth mes, double totalIngresos, 
                                            double totalGastos, double balance, 
                                            Map<String, Double> gastosPorCategoria, int numTransacciones) {
        
        String mesNombre = mes.getMonth().getDisplayName(TextStyle.FULL, Locale.of("es", "ES"));
        String mesCapitalizado = mesNombre.substring(0, 1).toUpperCase() + mesNombre.substring(1);
        
        // Top 3 categorías
        String topCategorias = gastosPorCategoria.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(3)
            .map(e -> String.format(
                "<tr><td style='padding: 8px; border-bottom: 1px solid #ddd;'>%s</td>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right; color: #e74c3c;'>$%,.0f</td></tr>",
                e.getKey(), e.getValue()
            ))
            .collect(Collectors.joining(""));

        String colorBalance = balance >= 0 ? "#27ae60" : "#e74c3c";
        String textoBalance = balance >= 0 ? "Superávit" : "Déficit";

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; background: #f4f4f4; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: white; padding: 30px; }
                    .resumen { display: flex; justify-content: space-around; margin: 20px 0; }
                    .stat { text-align: center; padding: 15px; }
                    .stat-valor { font-size: 24px; font-weight: bold; margin: 5px 0; }
                    .stat-label { color: #666; font-size: 14px; }
                    .ingresos { color: #27ae60; }
                    .gastos { color: #e74c3c; }
                    .balance { color: %s; }
                    .tabla { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                    .footer { background: #667eea; color: white; padding: 20px; text-align: center; border-radius: 0 0 10px 10px; }
                    .highlight { background: #f0f0f0; padding: 15px; border-radius: 5px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📊 Reporte Mensual</h1>
                        <p style="font-size: 18px; margin: 0;">%s %d</p>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        <p>Este es tu resumen financiero del mes anterior:</p>
                        
                        <div class="highlight" style="background: #e3f2fd; border-left: 4px solid #667eea;">
                            <strong>📎 Archivo adjunto:</strong> Encontrarás un reporte detallado en PDF con toda la información de tus transacciones.
                        </div>
                        
                        <div class="resumen">
                            <div class="stat">
                                <div class="stat-label">Ingresos</div>
                                <div class="stat-valor ingresos">$%,.0f</div>
                            </div>
                            <div class="stat">
                                <div class="stat-label">Gastos</div>
                                <div class="stat-valor gastos">$%,.0f</div>
                            </div>
                            <div class="stat">
                                <div class="stat-label">%s</div>
                                <div class="stat-valor balance">$%,.0f</div>
                            </div>
                        </div>

                        <div class="highlight">
                            <strong>📈 Total de transacciones:</strong> %d
                        </div>

                        <h3>🏆 Top Categorías de Gastos</h3>
                        <table class="tabla">
                            %s
                        </table>

                        <p style="margin-top: 30px; color: #666; font-size: 14px;">
                            💡 <strong>Consejo:</strong> Revisa tus gastos principales y busca oportunidades de ahorro.
                        </p>
                    </div>
                    <div class="footer">
                        <p style="margin: 0;">© 2025 PocketBook - Tu gestor financiero personal</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                colorBalance,
                mesCapitalizado, mes.getYear(),
                nombre,
                totalIngresos,
                totalGastos,
                textoBalance, Math.abs(balance),
                numTransacciones,
                topCategorias.isEmpty() ? "<tr><td colspan='2' style='text-align: center; padding: 20px; color: #999;'>No hay gastos registrados</td></tr>" : topCategorias
            );
    }

    private File generarPdfReporteMensual(String nombre, YearMonth mes, double totalIngresos, 
                                          double totalGastos, double balance, 
                                          Map<String, Double> gastosPorCategoria,
                                          java.util.List<Ingresos> transacciones) {
        try {
            String mesNombre = mes.getMonth().getDisplayName(TextStyle.FULL, Locale.of("es", "ES"));
            String mesCapitalizado = mesNombre.substring(0, 1).toUpperCase() + mesNombre.substring(1);
            
            // Crear archivo temporal
            File pdfFile = File.createTempFile("reporte_" + mes.toString() + "_", ".pdf");
            
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Fuentes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);

            // Título
            Paragraph title = new Paragraph("Reporte Mensual", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            Paragraph subtitle = new Paragraph(mesCapitalizado + " " + mes.getYear(), subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Usuario
            Paragraph usuario = new Paragraph("Usuario: " + nombre, normalFont);
            usuario.setSpacingAfter(20);
            document.add(usuario);

            // Resumen Financiero - Tabla
            PdfPTable resumenTable = new PdfPTable(2);
            resumenTable.setWidthPercentage(100);
            resumenTable.setSpacingBefore(10);
            resumenTable.setSpacingAfter(20);

            // Header
            PdfPCell headerCell = new PdfPCell(new Phrase("RESUMEN FINANCIERO", headerFont));
            headerCell.setBackgroundColor(new BaseColor(102, 126, 234)); // #667eea
            headerCell.setColspan(2);
            headerCell.setPadding(10);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            resumenTable.addCell(headerCell);

            // Ingresos
            PdfPCell ingresosLabel = new PdfPCell(new Phrase("Ingresos Totales", boldFont));
            ingresosLabel.setPadding(8);
            ingresosLabel.setBorder(Rectangle.NO_BORDER);
            resumenTable.addCell(ingresosLabel);
            
            PdfPCell ingresosValue = new PdfPCell(new Phrase(String.format("$%,.0f", totalIngresos), 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new BaseColor(39, 174, 96))));
            ingresosValue.setPadding(8);
            ingresosValue.setBorder(Rectangle.NO_BORDER);
            ingresosValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            resumenTable.addCell(ingresosValue);

            // Gastos
            PdfPCell gastosLabel = new PdfPCell(new Phrase("Gastos Totales", boldFont));
            gastosLabel.setPadding(8);
            gastosLabel.setBorder(Rectangle.NO_BORDER);
            gastosLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
            resumenTable.addCell(gastosLabel);
            
            PdfPCell gastosValue = new PdfPCell(new Phrase(String.format("$%,.0f", totalGastos), 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new BaseColor(231, 76, 60))));
            gastosValue.setPadding(8);
            gastosValue.setBorder(Rectangle.NO_BORDER);
            gastosValue.setBackgroundColor(BaseColor.LIGHT_GRAY);
            gastosValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            resumenTable.addCell(gastosValue);

            // Balance
            BaseColor balanceColor = balance >= 0 ? new BaseColor(39, 174, 96) : new BaseColor(231, 76, 60);
            String balanceTexto = balance >= 0 ? "Superávit" : "Déficit";
            
            PdfPCell balanceLabel = new PdfPCell(new Phrase(balanceTexto, boldFont));
            balanceLabel.setPadding(8);
            balanceLabel.setBorder(Rectangle.NO_BORDER);
            resumenTable.addCell(balanceLabel);
            
            PdfPCell balanceValue = new PdfPCell(new Phrase(String.format("$%,.0f", Math.abs(balance)), 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, balanceColor)));
            balanceValue.setPadding(8);
            balanceValue.setBorder(Rectangle.NO_BORDER);
            balanceValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            resumenTable.addCell(balanceValue);

            document.add(resumenTable);

            // Transacciones totales
            Paragraph transaccionesInfo = new Paragraph(
                String.format("📈 Total de transacciones: %d", transacciones.size()), 
                normalFont
            );
            transaccionesInfo.setSpacingAfter(20);
            document.add(transaccionesInfo);

            // Top Categorías
            Paragraph categoriasTitle = new Paragraph("🏆 Top Categorías de Gastos", subtitleFont);
            categoriasTitle.setSpacingAfter(10);
            document.add(categoriasTitle);

            PdfPTable categoriasTable = new PdfPTable(2);
            categoriasTable.setWidthPercentage(100);
            categoriasTable.setSpacingAfter(20);

            // Header categorías
            PdfPCell catHeader1 = new PdfPCell(new Phrase("Categoría", headerFont));
            catHeader1.setBackgroundColor(new BaseColor(102, 126, 234));
            catHeader1.setPadding(8);
            categoriasTable.addCell(catHeader1);

            PdfPCell catHeader2 = new PdfPCell(new Phrase("Monto", headerFont));
            catHeader2.setBackgroundColor(new BaseColor(102, 126, 234));
            catHeader2.setPadding(8);
            catHeader2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            categoriasTable.addCell(catHeader2);

            // Datos top 5 categorías
            gastosPorCategoria.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    PdfPCell catCell = new PdfPCell(new Phrase(entry.getKey(), normalFont));
                    catCell.setPadding(8);
                    categoriasTable.addCell(catCell);

                    PdfPCell montoCell = new PdfPCell(new Phrase(
                        String.format("$%,.0f", entry.getValue()), 
                        FontFactory.getFont(FontFactory.HELVETICA, 11, new BaseColor(231, 76, 60))
                    ));
                    montoCell.setPadding(8);
                    montoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    categoriasTable.addCell(montoCell);
                });

            if (gastosPorCategoria.isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No hay gastos registrados", normalFont));
                emptyCell.setColspan(2);
                emptyCell.setPadding(15);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                categoriasTable.addCell(emptyCell);
            }

            document.add(categoriasTable);

            // Consejo
            Paragraph consejo = new Paragraph(
                "💡 Consejo: Revisa tus gastos principales y busca oportunidades de ahorro.", 
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.DARK_GRAY)
            );
            consejo.setSpacingBefore(20);
            document.add(consejo);

            // Footer
            Paragraph footer = new Paragraph(
                "© 2025 PocketBook - Tu gestor financiero personal", 
                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();
            
            return pdfFile;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
