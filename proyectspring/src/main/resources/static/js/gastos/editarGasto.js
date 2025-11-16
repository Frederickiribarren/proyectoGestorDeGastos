// Manejo del modal de editar gasto
document.addEventListener('DOMContentLoaded', function() {
    const modalEditar = document.getElementById('modalEditarGasto');
    const formEditar = document.getElementById('formEditarGasto');
    const btnActualizar = document.getElementById('btnActualizarGasto');
    
    if (modalEditar) {
        modalEditar.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-gasto-id');
            const descripcion = button.getAttribute('data-gasto-descripcion');
            const monto = button.getAttribute('data-gasto-monto');
            const fecha = button.getAttribute('data-gasto-fecha');
            const categoria = button.getAttribute('data-gasto-categoria');
            const metodo = button.getAttribute('data-gasto-metodo');
            
            document.getElementById('editGastoId').value = id;
            document.getElementById('editDescripcion').value = descripcion;
            document.getElementById('editMonto').value = monto;
            document.getElementById('editFecha').value = fecha;
            document.getElementById('editCategoria').value = categoria;
            document.getElementById('editMetodoPago').value = metodo;
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
            const data = {
                id: parseInt(formData.get('id')),
                descripcion: formData.get('descripcion'),
                monto: parseFloat(formData.get('monto')),
                fecha: formData.get('fecha'),
                categoria: formData.get('categoria'),
                metodoPago: formData.get('metodoPago')
            };
            
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
            
            try {
                const response = await fetch('/gastos/actualizar', {
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
                    mostrarAlerta('Gasto actualizado correctamente', 'success');
                    setTimeout(() => window.location.reload(), 1500);
                } else {
                    const error = await response.text();
                    mostrarAlerta('Error: ' + error, 'danger');
                }
            } catch (error) {
                console.error('Error:', error);
                mostrarAlerta('Error de conexión al actualizar el gasto', 'danger');
            } finally {
                btnActualizar.disabled = false;
                btnActualizar.innerHTML = '<i class="bi bi-save me-1"></i>Actualizar Gasto';
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
