document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formNuevoIngreso');
    const btnGuardar = document.getElementById('btnGuardarIngreso');
    const modal = document.getElementById('modalNuevoIngreso');
    const fechaInput = document.getElementById('fecha');
    const tipoIngresoSelect = document.getElementById('tipoIngreso');
    const divOrigen = document.getElementById('divOrigen');
    
    // Establecer fecha actual por defecto
    if (fechaInput) {
        fechaInput.valueAsDate = new Date();
    }
    
    // Mostrar/ocultar campo origen según tipo de ingreso
    tipoIngresoSelect.addEventListener('change', function() {
        const tipo = this.value;
        if (tipo === 'transferencia' || tipo === 'trabajo_extra') {
            divOrigen.style.display = 'block';
        } else {
            divOrigen.style.display = 'none';
            document.getElementById('origen').value = '';
        }
    });
    
    // Modal de eliminación
    const modalEliminar = document.getElementById('modalEliminarIngreso');
    const btnConfirmarEliminar = document.getElementById('btnConfirmarEliminar');
    let ingresoIdAEliminar = null;
    
    if (modalEliminar) {
        modalEliminar.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            ingresoIdAEliminar = button.getAttribute('data-ingreso-id');
            const descripcion = button.getAttribute('data-ingreso-descripcion');
            document.getElementById('ingresoDescripcion').textContent = descripcion;
        });
    }
    
    // Confirmar eliminación
    if (btnConfirmarEliminar) {
        btnConfirmarEliminar.addEventListener('click', async function() {
            if (!ingresoIdAEliminar) return;
            
            btnConfirmarEliminar.disabled = true;
            btnConfirmarEliminar.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Eliminando...';
            
            // Obtener token CSRF
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
            
            try {
                const response = await fetch(`/ingresos/eliminar/${ingresoIdAEliminar}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    }
                });
                
                if (response.ok) {
                    const modalInstance = bootstrap.Modal.getInstance(modalEliminar);
                    modalInstance.hide();
                    mostrarAlerta('Ingreso eliminado correctamente', 'success');
                    setTimeout(() => window.location.reload(), 1500);
                } else {
                    mostrarAlerta('Error al eliminar el ingreso', 'danger');
                }
            } catch (error) {
                console.error('Error:', error);
                mostrarAlerta('Error de conexión al eliminar el ingreso', 'danger');
            } finally {
                btnConfirmarEliminar.disabled = false;
                btnConfirmarEliminar.innerHTML = '<i class="bi bi-trash me-1"></i>Eliminar';
            }
        });
    }
    
    // Guardar ingreso
    if (btnGuardar) {
        btnGuardar.addEventListener('click', async function(e) {
            e.preventDefault();
            
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }
            
            btnGuardar.disabled = true;
            btnGuardar.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Guardando...';
            
            const formData = new FormData(form);
            const data = {
                descripcion: formData.get('descripcion'),
                monto: parseFloat(formData.get('monto')),
                fecha: formData.get('fecha'),
                tipoIngreso: formData.get('tipoIngreso'),
                origen: formData.get('origen') || null,
                categoria: formData.get('categoria') || null
            };
            
            // Obtener token CSRF
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
            
            try {
                const response = await fetch('/ingresos/guardar', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    },
                    body: JSON.stringify(data)
                });
                
                if (response.ok) {
                    const modalInstance = bootstrap.Modal.getInstance(modal);
                    modalInstance.hide();
                    form.reset();
                    if (fechaInput) fechaInput.valueAsDate = new Date();
                    mostrarAlerta('Ingreso registrado correctamente', 'success');
                    setTimeout(() => window.location.reload(), 1500);
                } else {
                    const error = await response.text();
                    mostrarAlerta('Error: ' + error, 'danger');
                }
            } catch (error) {
                console.error('Error:', error);
                mostrarAlerta('Error de conexión al guardar el ingreso', 'danger');
            } finally {
                btnGuardar.disabled = false;
                btnGuardar.innerHTML = '<i class="bi bi-save me-1"></i>Guardar Ingreso';
            }
        });
    }
    
    // Función para mostrar alertas
    function mostrarAlerta(mensaje, tipo) {
        const alertaDiv = document.createElement('div');
        alertaDiv.className = `alert alert-${tipo} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
        alertaDiv.style.zIndex = '9999';
        alertaDiv.innerHTML = `
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        document.body.appendChild(alertaDiv);
        
        setTimeout(() => {
            alertaDiv.remove();
        }, 3000);
    }
    
    // Poblar filtro de meses
    poblarFiltroMeses();
    
    // Calcular estadísticas
    calcularEstadisticas();
    
    // Event listeners para filtros
    document.getElementById('filtroTipo').addEventListener('change', aplicarFiltros);
    document.getElementById('filtroMes').addEventListener('change', aplicarFiltros);
    document.getElementById('btnLimpiarFiltros').addEventListener('click', limpiarFiltros);
});

function poblarFiltroMeses() {
    const filtroMes = document.getElementById('filtroMes');
    const meses = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 
                   'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
    
    const fechaActual = new Date();
    const mesActual = fechaActual.getMonth();
    const anioActual = fechaActual.getFullYear();
    
    for (let i = 0; i < 12; i++) {
        const mes = (mesActual - i + 12) % 12;
        const anio = mesActual - i < 0 ? anioActual - 1 : anioActual;
        const option = document.createElement('option');
        option.value = `${anio}-${String(mes + 1).padStart(2, '0')}`;
        option.textContent = `${meses[mes]} ${anio}`;
        filtroMes.appendChild(option);
    }
    
    // Seleccionar mes actual
    filtroMes.value = `${anioActual}-${String(mesActual + 1).padStart(2, '0')}`;
    aplicarFiltros();
}

function aplicarFiltros() {
    const filtroTipo = document.getElementById('filtroTipo').value.toLowerCase();
    const filtroMes = document.getElementById('filtroMes').value;
    const filas = document.querySelectorAll('#tbodyIngresos tr:not(#emptyState)');
    
    let visibles = 0;
    
    filas.forEach(fila => {
        const tipo = fila.querySelector('td:nth-child(4)').textContent.toLowerCase().trim();
        const fecha = fila.querySelector('td:nth-child(2)').textContent.trim();
        const [dia, mes, anio] = fecha.split('/');
        const fechaFila = `${anio}-${mes}`;
        
        const cumpleTipo = !filtroTipo || tipo === filtroTipo;
        const cumpleMes = !filtroMes || fechaFila === filtroMes;
        
        if (cumpleTipo && cumpleMes) {
            fila.style.display = '';
            visibles++;
        } else {
            fila.style.display = 'none';
        }
    });
    
    const emptyState = document.getElementById('emptyState');
    if (emptyState) {
        emptyState.style.display = visibles === 0 ? '' : 'none';
    }
    
    calcularEstadisticas();
}

function limpiarFiltros() {
    document.getElementById('filtroTipo').value = '';
    document.getElementById('filtroMes').value = '';
    aplicarFiltros();
}

function calcularEstadisticas() {
    const filas = Array.from(document.querySelectorAll('#tbodyIngresos tr:not(#emptyState)'))
        .filter(fila => fila.style.display !== 'none');
    
    let totalMes = 0;
    const tiposContador = {};
    
    filas.forEach(fila => {
        const montoText = fila.querySelector('td:nth-child(6)').textContent;
        const monto = parseFloat(montoText.replace(/[^\d.-]/g, ''));
        totalMes += monto;
        
        const tipo = fila.querySelector('td:nth-child(4)').textContent.trim();
        tiposContador[tipo] = (tiposContador[tipo] || 0) + 1;
    });
    
    document.getElementById('totalMes').textContent = '$' + totalMes.toLocaleString('es-CL') + ' CLP';
    
    const tipoPrincipal = Object.keys(tiposContador).reduce((a, b) => 
        tiposContador[a] > tiposContador[b] ? a : b, '-');
    document.getElementById('tipoPrincipal').textContent = tipoPrincipal;
    
    // Calcular promedio mensual (últimos 3 meses)
    const promedioMensual = totalMes; // Por ahora solo el mes actual
    document.getElementById('promedioMensual').textContent = '$' + promedioMensual.toLocaleString('es-CL') + ' CLP';
}
