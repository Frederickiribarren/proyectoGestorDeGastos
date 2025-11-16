    // Configuración de tarjetas de crédito
document.addEventListener('DOMContentLoaded', function() {
    const modalConfigTarjetas = document.getElementById('modalConfigTarjetas');
    
    if (!modalConfigTarjetas) return;
    
    // Cargar configuraciones existentes al abrir el modal
    modalConfigTarjetas.addEventListener('show.bs.modal', function() {
        cargarConfiguraciones();
    });
    
    // Función para cargar las configuraciones guardadas
    async function cargarConfiguraciones() {
        try {
            const response = await fetch('/tarjetas/configuraciones');
            
            if (response.ok) {
                const configuraciones = await response.json();
                
                // Aplicar las configuraciones a cada formulario
                configuraciones.forEach(config => {
                    const form = document.querySelector(`[data-tarjeta="${config.nombreTarjeta}"]`);
                    if (form) {
                        const selectCorte = form.querySelector('.dia-corte');
                        const selectPago = form.querySelector('.dia-pago');
                        
                        if (selectCorte && config.diaCorte) {
                            selectCorte.value = config.diaCorte;
                        }
                        if (selectPago && config.diaPago) {
                            selectPago.value = config.diaPago;
                        }
                    }
                });
            }
        } catch (error) {
            console.error('Error al cargar configuraciones:', error);
        }
    }
    
    // Manejar envío de formularios
    const formulariosTarjetas = document.querySelectorAll('.form-tarjeta');
    
    formulariosTarjetas.forEach(form => {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const nombreTarjeta = this.dataset.tarjeta;
            const diaCorte = this.querySelector('.dia-corte').value;
            const diaPago = this.querySelector('.dia-pago').value;
            const btnGuardar = this.querySelector('button[type="submit"]');
            
            // Validar
            if (!diaCorte || !diaPago) {
                mostrarNotificacion('warning', '⚠️ Por favor complete ambos campos');
                return;
            }
            
            // Deshabilitar botón
            const textoOriginal = btnGuardar.innerHTML;
            btnGuardar.disabled = true;
            btnGuardar.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Guardando...';
            
            try {
                // Obtener token CSRF
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
                const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
                
                const headers = {
                    'Content-Type': 'application/json'
                };
                
                if (csrfToken && csrfHeader) {
                    headers[csrfHeader] = csrfToken;
                }
                
                const response = await fetch('/tarjetas/configurar', {
                    method: 'POST',
                    headers: headers,
                    body: JSON.stringify({
                        nombreTarjeta: nombreTarjeta,
                        diaCorte: parseInt(diaCorte),
                        diaPago: parseInt(diaPago)
                    })
                });
                
                if (response.ok) {
                    mostrarNotificacion('success', `✅ Configuración guardada para ${nombreTarjeta}`);
                } else {
                    const error = await response.text();
                    mostrarNotificacion('error', '❌ Error al guardar: ' + error);
                }
            } catch (error) {
                console.error('Error:', error);
                mostrarNotificacion('error', '❌ Error de conexión al guardar');
            } finally {
                // Rehabilitar botón
                btnGuardar.disabled = false;
                btnGuardar.innerHTML = textoOriginal;
            }
        });
    });
    
    // Función para mostrar notificaciones
    function mostrarNotificacion(tipo, mensaje) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${tipo} fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
        alertDiv.style.zIndex = '9999';
        alertDiv.innerHTML = mensaje;
        
        document.body.appendChild(alertDiv);
        
        setTimeout(() => {
            alertDiv.remove();
        }, 2000);
    }
});
