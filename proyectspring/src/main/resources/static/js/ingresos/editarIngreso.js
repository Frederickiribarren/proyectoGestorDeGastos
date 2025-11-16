// Manejo del modal de editar ingreso
document.addEventListener('DOMContentLoaded', function() {
    const modalEditar = document.getElementById('modalEditarIngreso');
    const formEditar = document.getElementById('formEditarIngreso');
    const btnActualizar = document.getElementById('btnActualizarIngreso');
    const editTipoSelect = document.getElementById('editIngresoTipo');
    const editDivOrigen = document.getElementById('editDivOrigen');
    
    if (modalEditar) {
        modalEditar.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-ingreso-id');
            const descripcion = button.getAttribute('data-ingreso-descripcion');
            const monto = button.getAttribute('data-ingreso-monto');
            const fecha = button.getAttribute('data-ingreso-fecha');
            const tipo = button.getAttribute('data-ingreso-tipo');
            const origen = button.getAttribute('data-ingreso-origen');
            const categoria = button.getAttribute('data-ingreso-categoria');
            
            document.getElementById('editIngresoId').value = id;
            document.getElementById('editIngresoDescripcion').value = descripcion;
            document.getElementById('editIngresoMonto').value = monto;
            document.getElementById('editIngresoFecha').value = fecha;
            document.getElementById('editIngresoTipo').value = tipo || '';
            document.getElementById('editIngresoOrigen').value = origen && origen !== 'null' ? origen : '';
            document.getElementById('editIngresoCategoria').value = categoria && categoria !== 'null' ? categoria : '';
            
            // Mostrar/ocultar campo origen
            if (tipo === 'transferencia' || tipo === 'trabajo_extra') {
                editDivOrigen.style.display = 'block';
            } else {
                editDivOrigen.style.display = 'none';
            }
        });
    }
    
    // Mostrar/ocultar campo origen al cambiar tipo
    if (editTipoSelect) {
        editTipoSelect.addEventListener('change', function() {
            const tipo = this.value;
            if (tipo === 'transferencia' || tipo === 'trabajo_extra') {
                editDivOrigen.style.display = 'block';
            } else {
                editDivOrigen.style.display = 'none';
                document.getElementById('editIngresoOrigen').value = '';
            }
        });
    }
    
    if (btnActualizar) {
        btnActualizar.addEventListener('click', async function(e) {
            e.preventDefault();
            
            if (!formEditar.checkValidity()) {
                formEditar.reportValidity();
                return;
            }
            
            btnActualizar.disabled = true;
            btnActualizar.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Actualizando...';
            
            const formData = new FormData(formEditar);
            const id = formData.get('id');
            const data = {
                descripcion: formData.get('descripcion'),
                monto: parseFloat(formData.get('monto')),
                fecha: formData.get('fecha'),
                tipoIngreso: formData.get('tipoIngreso'),
                origen: formData.get('origen') || null,
                categoria: formData.get('categoria') || null
            };
            
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
            
            try {
                const response = await fetch(`/ingresos/actualizar/${id}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    },
                    body: JSON.stringify(data)
                });
                
                if (response.ok) {
                    const modalInstance = bootstrap.Modal.getInstance(modalEditar);
                    modalInstance.hide();
                    mostrarAlerta('Ingreso actualizado correctamente', 'success');
                    setTimeout(() => window.location.reload(), 1500);
                } else {
                    const error = await response.text();
                    mostrarAlerta('Error: ' + error, 'danger');
                }
            } catch (error) {
                console.error('Error:', error);
                mostrarAlerta('Error de conexión al actualizar el ingreso', 'danger');
            } finally {
                btnActualizar.disabled = false;
                btnActualizar.innerHTML = '<i class="bi bi-save me-1"></i>Actualizar Ingreso';
            }
        });
    }
    
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
});
