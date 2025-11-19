package com.example.proyectspring.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyectspring.entity.PasswordResetToken;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.repository.PasswordResetTokenRepository;
import com.example.proyectspring.service.impl.EmailServiceImpl;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private EmailServiceImpl emailService;

    @Transactional
    public void crearTokenRecuperacion(String email, String baseUrl) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        
        if (usuario == null) {
            // Por seguridad, no revelamos si el email existe o no
            return;
        }
        
        // Eliminar tokens anteriores del usuario
        tokenRepository.deleteByUsuarioId(usuario.getId());
        
        // Generar nuevo token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, usuario);
        tokenRepository.save(resetToken);
        
        // Enviar email con link de recuperación
        String resetUrl = baseUrl + "/recuperar-password/cambiar?token=" + token;
        enviarEmailRecuperacion(usuario.getEmail(), resetUrl, usuario.getNombre());
    }

    private void enviarEmailRecuperacion(String email, String resetUrl, String nombre) {
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; background: #667eea; color: #ffffff; padding: 15px 40px; text-decoration: none; border-radius: 5px; margin: 20px 0; font-weight: bold; font-size: 16px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .button:hover { background: #5568d3; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Recuperación de Contraseña</h1>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        <p>Recibimos una solicitud para restablecer la contraseña de tu cuenta en PocketBook.</p>
                        <p>Haz clic en el siguiente botón para crear una nueva contraseña:</p>
                        <div style="text-align: center;">
                            <a href="%s" class="button">Restablecer Contraseña</a>
                        </div>
                        <p><strong>Este enlace es válido por 24 horas.</strong></p>
                        <p>Si no solicitaste este cambio, puedes ignorar este correo. Tu contraseña actual seguirá siendo la misma.</p>
                        <hr>
                        <p style="font-size: 12px; color: #666;">Si el botón no funciona, copia y pega este enlace en tu navegador:<br>
                        <a href="%s">%s</a></p>
                    </div>
                    <div class="footer">
                        <p>© 2025 PocketBook - Todos los derechos reservados</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombre, resetUrl, resetUrl, resetUrl);
        
        emailService.sendHtmlEmail(email, "Recuperación de Contraseña - PocketBook", htmlContent);
    }

    public PasswordResetToken validarToken(String token) {
        return tokenRepository.findByToken(token)
            .filter(t -> !t.isUsado() && !t.isExpirado())
            .orElse(null);
    }

    @Transactional
    public boolean cambiarPassword(String token, String nuevaPassword) {
        PasswordResetToken resetToken = validarToken(token);
        
        if (resetToken == null) {
            return false;
        }
        
        // Cambiar contraseña
        usuarioService.actualizarPassword(resetToken.getUsuario().getId(), nuevaPassword);
        
        // Marcar token como usado
        resetToken.setUsado(true);
        tokenRepository.save(resetToken);
        
        return true;
    }
}
