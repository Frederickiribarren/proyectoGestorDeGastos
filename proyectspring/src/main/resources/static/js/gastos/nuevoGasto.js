// Validación y envío del formulario de nuevo gasto
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formNuevoGasto');
    const btnGuardar = document.getElementById('btnGuardarGasto');
    const modal = document.getElementById('modalNuevoGasto');
    const fechaInput = document.getElementById('fecha');
    
    // Dropdown personalizado para método de pago
    const dropdownItems = document.querySelectorAll('.dropdown-menu .dropdown-item');
    const selectedMetodo = document.getElementById('selectedMetodo');
    const metodoPagoInput = document.getElementById('metodoPago');
    
    dropdownItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const value = this.getAttribute('data-value');
            
            // Obtener el header (grupo) más cercano hacia arriba
            let categoria = '';
            let prevElement = this.closest('li').previousElementSibling;
            
            // Buscar hacia atrás hasta encontrar un header
            while (prevElement) {
                if (prevElement.querySelector('.dropdown-header')) {
                    categoria = prevElement.querySelector('.dropdown-header').textContent.trim();
                    break;
                }
                prevElement = prevElement.previousElementSibling;
            }
            
            // Construir el texto a mostrar
            let displayText;
            if (categoria && value !== 'efectivo' && value !== 'otro') {
                // Remover el emoji del inicio del texto de categoría
                categoria = categoria.replace(/^[\u{1F300}-\u{1F9FF}]\s*/u, '');
                displayText = categoria + ' / ' + this.textContent.trim();
            } else {
                displayText = this.textContent.trim();
            }
            
            selectedMetodo.textContent = displayText;
            selectedMetodo.classList.remove('text-muted');
            selectedMetodo.classList.add('text-dark');
            metodoPagoInput.value = value;
            metodoPagoInput.classList.remove('is-invalid');
        });
    });
    
    // Array para almacenar los gastos (simulando base de datos)
    let gastos = [];
    let contadorId = 1;
    
    // Mapeo de categorías con iconos y estilos
    const categorias = {
        'alimentacion': { icono: '🍔', nombre: 'Alimentación', clase: 'badge-alimentacion' },
        'transporte': { icono: '🚗', nombre: 'Transporte', clase: 'badge-transporte' },
        'vivienda': { icono: '🏠', nombre: 'Vivienda', clase: 'badge-vivienda' },
        'entretenimiento': { icono: '🎬', nombre: 'Entretenimiento', clase: 'badge-entretenimiento' },
        'salud': { icono: '💊', nombre: 'Salud', clase: 'badge-salud' },
        'educacion': { icono: '📚', nombre: 'Educación', clase: 'badge-educacion' },
        'servicios': { icono: '💡', nombre: 'Servicios', clase: 'badge-servicios' },
        'ropa': { icono: '👔', nombre: 'Ropa y Calzado', clase: 'badge-ropa' },
        'otros': { icono: '📦', nombre: 'Otros', clase: 'badge-otros' }
    };
    
    const metodosPago = {
        'efectivo': { icono: '💵', nombre: 'Efectivo', clase: 'badge-efectivo' },
        'debito': { icono: '💳', nombre: 'Débito', clase: 'badge-debito' },
        'credito': { icono: '💳', nombre: 'Crédito', clase: 'badge-credito' },
        'transferencia': { icono: '🏦', nombre: 'Transferencia', clase: 'badge-transferencia' },
        'otro': { icono: '📱', nombre: 'Otro', clase: 'badge-otro' }
    };
    
    // Establecer la fecha actual por defecto
    if (fechaInput) {
        const hoy = new Date().toISOString().split('T')[0];
        fechaInput.value = hoy;
        fechaInput.max = hoy; // No permitir fechas futuras
    }
    
    // Función para limpiar errores
    function limpiarErrores() {
        const inputs = form.querySelectorAll('.form-control, .form-select');
        inputs.forEach(input => {
            input.classList.remove('is-invalid', 'is-valid');
        });
    }
    
    // Función para mostrar error en un campo
    function mostrarError(campo, mensaje) {
        campo.classList.add('is-invalid');
        campo.classList.remove('is-valid');
        const feedback = campo.parentElement.querySelector('.invalid-feedback') || 
                        campo.nextElementSibling;
        if (feedback && feedback.classList.contains('invalid-feedback')) {
            feedback.textContent = mensaje;
        }
    }
    
    // Función para mostrar campo válido
    function mostrarValido(campo) {
        campo.classList.add('is-valid');
        campo.classList.remove('is-invalid');
    }
    
    // Validar descripción
    function validarDescripcion(descripcion) {
        const valor = descripcion.value.trim();
        if (valor === '') {
            mostrarError(descripcion, 'Por favor, ingrese una descripción del gasto.');
            return false;
        }
        if (valor.length < 3) {
            mostrarError(descripcion, 'La descripción debe tener al menos 3 caracteres.');
            return false;
        }
        if (valor.length > 100) {
            mostrarError(descripcion, 'La descripción no puede exceder 100 caracteres.');
            return false;
        }
        mostrarValido(descripcion);
        return true;
    }
    
    // Validar monto
    function validarMonto(monto) {
        const valor = parseFloat(monto.value);
        if (isNaN(valor) || valor <= 0) {
            mostrarError(monto, 'Ingrese un monto válido mayor a 0.');
            return false;
        }
        if (valor > 999999999) {
            mostrarError(monto, 'El monto es demasiado grande.');
            return false;
        }
        // Validar que sea número entero (sin decimales)
        if (!Number.isInteger(valor)) {
            mostrarError(monto, 'Ingrese un monto sin decimales (pesos chilenos).');
            return false;
        }
        mostrarValido(monto);
        return true;
    }
    
    // Validar fecha
    function validarFecha(fecha) {
        const valor = fecha.value;
        if (!valor) {
            mostrarError(fecha, 'Por favor, seleccione una fecha.');
            return false;
        }
        
        const fechaSeleccionada = new Date(valor);
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);
        
        if (fechaSeleccionada > hoy) {
            mostrarError(fecha, 'No puede seleccionar una fecha futura.');
            return false;
        }
        
        mostrarValido(fecha);
        return true;
    }
    
    // Validar select (categoría y método de pago)
    function validarSelect(select, mensajeError) {
        if (!select.value || select.value === '') {
            mostrarError(select, mensajeError);
            return false;
        }
        mostrarValido(select);
        return true;
    }
    
    // Limpiar errores al escribir
    const campos = form.querySelectorAll('.form-control, .form-select');
    campos.forEach(campo => {
        campo.addEventListener('input', function() {
            this.classList.remove('is-invalid', 'is-valid');
        });
        
        campo.addEventListener('change', function() {
            this.classList.remove('is-invalid', 'is-valid');
        });
    });
    
    // Validar formulario completo
    function validarFormulario() {
        let esValido = true;
        
        const descripcion = document.getElementById('descripcion');
        const monto = document.getElementById('monto');
        const fecha = document.getElementById('fecha');
        const categoria = document.getElementById('categoria');
        const metodoPago = document.getElementById('metodoPago');
        
        esValido = validarDescripcion(descripcion) && esValido;
        esValido = validarMonto(monto) && esValido;
        esValido = validarFecha(fecha) && esValido;
        esValido = validarSelect(categoria, 'Por favor, seleccione una categoría.') && esValido;
        esValido = validarSelect(metodoPago, 'Por favor, seleccione un método de pago.') && esValido;
        
        return esValido;
    }
    
    // Evento del botón guardar
    if (btnGuardar) {
        btnGuardar.addEventListener('click', function(e) {
            e.preventDefault();
            
            if (validarFormulario()) {
                // Crear objeto gasto
                const formData = new FormData(form);
                const nuevoGasto = {
                    id: contadorId++,
                    descripcion: formData.get('descripcion'),
                    monto: parseFloat(formData.get('monto')),
                    fecha: formData.get('fecha'),
                    categoria: formData.get('categoria'),
                    metodoPago: formData.get('metodoPago'),
                    notas: formData.get('notas') || ''
                };
                
                // Agregar a la lista
                gastos.push(nuevoGasto);
                
                // Actualizar tabla
                actualizarTabla();
                
                // Cerrar modal y limpiar formulario
                const modalInstance = bootstrap.Modal.getInstance(modal);
                modalInstance.hide();
                form.reset();
                limpiarErrores();
                
                // Establecer fecha actual nuevamente
                const hoy = new Date().toISOString().split('T')[0];
                fechaInput.value = hoy;
                
                // Mostrar notificación
                mostrarNotificacion('success', '✅ Gasto registrado exitosamente');
            } else {
                // Hacer scroll al primer error
                const primerError = form.querySelector('.is-invalid');
                if (primerError) {
                    primerError.focus();
                }
            }
        });
    }
    
    // Función para actualizar la tabla
    function actualizarTabla() {
        const tbody = document.getElementById('tbodyGastos');
        const emptyState = document.getElementById('emptyState');
        
        if (gastos.length === 0) {
            emptyState.style.display = '';
            actualizarEstadisticas();
            return;
        }
        
        emptyState.style.display = 'none';
        
        // Limpiar tbody
        tbody.innerHTML = '';
        
        // Ordenar gastos por fecha (más reciente primero)
        const gastosOrdenados = [...gastos].sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
        
        // Agregar filas
        gastosOrdenados.forEach((gasto, index) => {
            const fila = crearFilaGasto(gasto, index + 1);
            tbody.appendChild(fila);
        });
        
        // Actualizar estadísticas
        actualizarEstadisticas();
    }
    
    // Función para crear una fila de gasto
    function crearFilaGasto(gasto, numero) {
        const tr = document.createElement('tr');
        tr.className = 'new-row';
        tr.dataset.gastoId = gasto.id;
        
        const cat = categorias[gasto.categoria];
        const pago = metodosPago[gasto.metodoPago];
        
        tr.innerHTML = `
            <td class="ps-4 fw-semibold">${numero}</td>
            <td>${formatearFecha(gasto.fecha)}</td>
            <td>
                <div class="fw-semibold">${gasto.descripcion}</div>
                ${gasto.notas ? `<small class="text-muted">${gasto.notas}</small>` : ''}
            </td>
            <td>
                <span class="badge badge-categoria ${cat.clase}">
                    ${cat.icono} ${cat.nombre}
                </span>
            </td>
            <td>
                <span class="badge badge-pago ${pago.clase}">
                    ${pago.icono} ${pago.nombre}
                </span>
            </td>
            <td class="text-end fw-bold text-success">$${formatearPesosChilenos(gasto.monto)}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-outline-primary btn-action me-1" onclick="verDetalleGasto(${gasto.id})" title="Ver detalles">
                    <i class="bi bi-eye"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger btn-action" onclick="eliminarGasto(${gasto.id})" title="Eliminar">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
        
        return tr;
    }
    
    // Función para formatear pesos chilenos
    function formatearPesosChilenos(monto) {
        return Math.round(monto).toLocaleString('es-CL');
    }
    
    // Función para formatear fecha
    function formatearFecha(fecha) {
        const opciones = { year: 'numeric', month: 'short', day: 'numeric' };
        return new Date(fecha + 'T00:00:00').toLocaleDateString('es-ES', opciones);
    }
    
    // Función para actualizar estadísticas
    function actualizarEstadisticas() {
        // Total de gastos
        const total = gastos.reduce((sum, g) => sum + g.monto, 0);
        document.getElementById('montoTotal').textContent = `$${formatearPesosChilenos(total)}`;
        document.getElementById('totalGastos').textContent = `${gastos.length} gasto${gastos.length !== 1 ? 's' : ''}`;
        
        // Gastos del mes actual
        const mesActual = new Date().getMonth();
        const añoActual = new Date().getFullYear();
        const gastosMes = gastos.filter(g => {
            const fecha = new Date(g.fecha + 'T00:00:00');
            return fecha.getMonth() === mesActual && fecha.getFullYear() === añoActual;
        });
        const totalMes = gastosMes.reduce((sum, g) => sum + g.monto, 0);
        document.getElementById('totalMes').textContent = `$${formatearPesosChilenos(totalMes)}`;
        
        // Promedio diario
        const diasDelMes = new Date(añoActual, mesActual + 1, 0).getDate();
        const promedio = totalMes / diasDelMes;
        document.getElementById('promedioDiario').textContent = `$${formatearPesosChilenos(promedio)}`;
        
        // Categoría principal
        const categoriasCont = {};
        gastos.forEach(g => {
            categoriasCont[g.categoria] = (categoriasCont[g.categoria] || 0) + 1;
        });
        
        let catPrincipal = '-';
        let maxCont = 0;
        for (const [cat, cont] of Object.entries(categoriasCont)) {
            if (cont > maxCont) {
                maxCont = cont;
                catPrincipal = categorias[cat].nombre;
            }
        }
        document.getElementById('categoriaPrincipal').textContent = catPrincipal;
    }
    
    // Función para mostrar notificación
    function mostrarNotificacion(tipo, mensaje) {
        // Crear elemento de notificación
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${tipo} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
        alertDiv.style.zIndex = '9999';
        alertDiv.innerHTML = `
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(alertDiv);
        
        // Remover después de 3 segundos
        setTimeout(() => {
            alertDiv.remove();
        }, 3000);
    }
    
    // Funciones globales para acciones de tabla
    window.verDetalleGasto = function(id) {
        const gasto = gastos.find(g => g.id === id);
        if (gasto) {
            const cat = categorias[gasto.categoria];
            const pago = metodosPago[gasto.metodoPago];
            
            alert(`📋 DETALLE DEL GASTO\n\n` +
                  `Descripción: ${gasto.descripcion}\n` +
                  `Monto: $${formatearPesosChilenos(gasto.monto)} CLP\n` +
                  `Fecha: ${formatearFecha(gasto.fecha)}\n` +
                  `Categoría: ${cat.icono} ${cat.nombre}\n` +
                  `Método de Pago: ${pago.icono} ${pago.nombre}\n` +
                  (gasto.notas ? `Notas: ${gasto.notas}` : ''));
        }
    };
    
    window.eliminarGasto = function(id) {
        if (confirm('¿Está seguro de eliminar este gasto?')) {
            const index = gastos.findIndex(g => g.id === id);
            if (index !== -1) {
                gastos.splice(index, 1);
                actualizarTabla();
                mostrarNotificacion('warning', '🗑️ Gasto eliminado');
            }
        }
    };
    
    // Filtros y búsqueda
    const buscarInput = document.getElementById('buscarGasto');
    const filtroCategoria = document.getElementById('filtroCategoria');
    const filtroMes = document.getElementById('filtroMes');
    
    if (buscarInput) {
        buscarInput.addEventListener('input', aplicarFiltros);
    }
    if (filtroCategoria) {
        filtroCategoria.addEventListener('change', aplicarFiltros);
    }
    if (filtroMes) {
        filtroMes.addEventListener('change', aplicarFiltros);
    }
    
    function aplicarFiltros() {
        const textoBusqueda = buscarInput?.value.toLowerCase() || '';
        const categoriaFiltro = filtroCategoria?.value || '';
        const mesFiltro = filtroMes?.value || '';
        
        const filas = document.querySelectorAll('#tbodyGastos tr:not(#emptyState)');
        
        filas.forEach(fila => {
            const gastoId = parseInt(fila.dataset.gastoId);
            const gasto = gastos.find(g => g.id === gastoId);
            
            if (!gasto) return;
            
            let mostrar = true;
            
            // Filtro de búsqueda
            if (textoBusqueda && !gasto.descripcion.toLowerCase().includes(textoBusqueda)) {
                mostrar = false;
            }
            
            // Filtro de categoría
            if (categoriaFiltro && gasto.categoria !== categoriaFiltro) {
                mostrar = false;
            }
            
            // Filtro de mes
            if (mesFiltro && !gasto.fecha.startsWith(mesFiltro)) {
                mostrar = false;
            }
            
            fila.style.display = mostrar ? '' : 'none';
        });
    }
    
    // Limpiar formulario al cerrar el modal
    if (modal) {
        modal.addEventListener('hidden.bs.modal', function() {
            form.reset();
            limpiarErrores();
            // Restablecer fecha actual
            const hoy = new Date().toISOString().split('T')[0];
            fechaInput.value = hoy;
        });
    }
});
