// Auto-dismiss alerts después de 2 segundos
document.addEventListener('DOMContentLoaded', () => {
    const alerts = document.querySelectorAll('.alert.fade.show');
    
    alerts.forEach(alert => {
        // Auto-dismiss después de 2 segundos
        setTimeout(() => {
            // Fade out con transición
            alert.classList.remove('show');
            
            // Remover del DOM después de la animación
            setTimeout(() => {
                alert.remove();
            }, 150); // Esperar a que termine la animación fade
        }, 2000);
    });
});
