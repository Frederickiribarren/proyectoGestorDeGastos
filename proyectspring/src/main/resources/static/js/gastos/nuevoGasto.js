// Validación y envío del formulario de nuevo gasto
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formNuevoGasto');
    const btnGuardar = document.getElementById('btnGuardarGasto');
    const modal = document.getElementById('modalNuevoGasto');
    const fechaInput = document.getElementById('fecha');
    
    // Modal de eliminación
    const modalEliminar = document.getElementById('modalEliminarGasto');
    const btnConfirmarEliminar = document.getElementById('btnConfirmarEliminar');
    let gastoIdAEliminar = null;
    
    // Event listener para cuando se abre el modal de eliminación
    if (modalEliminar) {
        modalEliminar.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            gastoIdAEliminar = button.getAttribute('data-gasto-id');
            const descripcion = button.getAttribute('data-gasto-descripcion');
            const monto = button.getAttribute('data-gasto-monto');
            
            document.getElementById('gastoDescripcion').textContent = descripcion;
            document.getElementById('gastoMonto').textContent = '$' + parseFloat(monto).toLocaleString('es-CL') + ' CLP';
        });
    }
    
    // Event listener para confirmar eliminación
    if (btnConfirmarEliminar) {
        btnConfirmarEliminar.addEventListener('click', async function() {
            if (!gastoIdAEliminar) return;
            
            // Deshabilitar botón mientras se elimina
            btnConfirmarEliminar.disabled = true;
            btnConfirmarEliminar.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Eliminando...';
            
            try {
                // Obtener token CSRF
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
                const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
                
                const headers = {};
                if (csrfToken && csrfHeader) {
                    headers[csrfHeader] = csrfToken;
                }
                
                const response = await fetch(`/gastos/eliminar/${gastoIdAEliminar}`, {
                    method: 'DELETE',
                    headers: headers
                });
                
                if (response.ok) {
                    // Cerrar modal
                    const modalInstance = bootstrap.Modal.getInstance(modalEliminar);
                    modalInstance.hide();
                    
                    // Recargar la página para mostrar los datos actualizados
                    window.location.reload();
                } else {
                    const error = await response.text();
                    alert('Error al eliminar: ' + error);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Error de conexión al eliminar el gasto');
            } finally {
                // Rehabilitar botón
                btnConfirmarEliminar.disabled = false;
                btnConfirmarEliminar.innerHTML = '<i class="bi bi-trash me-1"></i>Eliminar Gasto';
            }
        });
    }
    
    // Dropdown personalizado para método de pago
    const dropdownItems = document.querySelectorAll('#modalNuevoGasto .dropdown-menu .dropdown-item');
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
            
            // Mostrar/ocultar selector de cuotas según el método de pago
            const divCuotas = document.getElementById('divCuotas');
            if (value && value.startsWith('credito-')) {
                divCuotas.style.display = 'block';
            } else {
                divCuotas.style.display = 'none';
                document.getElementById('numeroCuotas').value = '1';
            }
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
        btnGuardar.addEventListener('click', async function(e) {
            e.preventDefault();
            
            if (validarFormulario()) {
                // Deshabilitar botón mientras se guarda
                btnGuardar.disabled = true;
                btnGuardar.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Guardando...';
                
                try {
                    // Crear objeto gasto
                    const formData = new FormData(form);
                    const nuevoGasto = {
                        descripcion: formData.get('descripcion'),
                        monto: parseFloat(formData.get('monto')),
                        fecha: formData.get('fecha'),
                        categoria: formData.get('categoria'),
                        metodoPago: formData.get('metodoPago'),
                        notaAdicional: formData.get('notas') || '',
                        numeroCuotas: parseInt(formData.get('numeroCuotas')) || 1
                    };
                    
                    // Obtener token CSRF
                    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
                    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
                    
                    // Enviar al servidor
                    const headers = {
                        'Content-Type': 'application/json',
                    };
                    
                    if (csrfToken && csrfHeader) {
                        headers[csrfHeader] = csrfToken;
                    }
                    
                    const response = await fetch('/gastos/guardar', {
                        method: 'POST',
                        headers: headers,
                        body: JSON.stringify(nuevoGasto)
                    });
                    
                    if (response.ok) {
                        const mensaje = await response.text();
                        
                        // Recargar la página para mostrar los datos actualizados
                        window.location.reload();
                    } else {
                        const error = await response.text();
                        mostrarNotificacion('error', '❌ Error al guardar: ' + error);
                    }
                } catch (error) {
                    console.error('Error:', error);
                    mostrarNotificacion('error', '❌ Error de conexión al guardar el gasto');
                } finally {
                    // Rehabilitar botón
                    btnGuardar.disabled = false;
                    btnGuardar.innerHTML = '<i class="bi bi-save me-1"></i>Guardar Gasto';
                }
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
        
        // Remover después de 2 segundos
        setTimeout(() => {
            alertDiv.remove();
        }, 2000);
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
    
    // Filtros y búsqueda
    const buscarInput = document.getElementById('buscarGasto');
    const filtroCategoria = document.getElementById('filtroCategoria');
    const filtroMes = document.getElementById('filtroMes');
    const btnLimpiarFiltros = document.getElementById('btnLimpiarFiltros');
    
    // Función para poblar el filtro de meses con los meses que tienen gastos
    function poblarFiltroMeses() {
        const filas = document.querySelectorAll('#tbodyGastos tr:not(#emptyState)');
        const mesesSet = new Set();
        
        filas.forEach(fila => {
            const fechaData = fila.dataset.fecha; // formato yyyy-MM-dd
            if (fechaData) {
                const [año, mes] = fechaData.split('-');
                const mesKey = `${año}-${mes}`;
                mesesSet.add(mesKey);
            }
        });
        
        // Convertir a array y ordenar descendente (más reciente primero)
        const mesesArray = Array.from(mesesSet).sort((a, b) => b.localeCompare(a));
        
        // Limpiar opciones existentes (excepto "Seleccionar mes")
        const opcionSeleccionar = filtroMes.querySelector('option[value=""]');
        filtroMes.innerHTML = '';
        filtroMes.appendChild(opcionSeleccionar);
        
        // Agregar opciones de meses que tienen gastos
        mesesArray.forEach(mesKey => {
            const [año, mes] = mesKey.split('-');
            const fecha = new Date(parseInt(año), parseInt(mes) - 1, 1);
            const nombreMes = fecha.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' });
            // Capitalizar primera letra
            const nombreMesCapitalizado = nombreMes.charAt(0).toUpperCase() + nombreMes.slice(1);
            
            const option = document.createElement('option');
            option.value = mesKey;
            option.textContent = nombreMesCapitalizado;
            filtroMes.appendChild(option);
        });
        
        // Seleccionar y filtrar por el mes actual automáticamente
        const fechaActual = new Date();
        const mesActual = `${fechaActual.getFullYear()}-${String(fechaActual.getMonth() + 1).padStart(2, '0')}`;
        if (mesesArray.includes(mesActual)) {
            filtroMes.value = mesActual;
            // Remover el atributo disabled para que se pueda cambiar
            opcionSeleccionar.removeAttribute('disabled');
            opcionSeleccionar.textContent = 'Todos los meses';
            aplicarFiltros(); // Aplicar filtro del mes actual automáticamente
        }
    }
    
    // Función para calcular y actualizar las estadísticas
    function actualizarEstadisticas() {
        const filas = document.querySelectorAll('#tbodyGastos tr:not(#emptyState)');
        
        if (filas.length === 0) {
            document.getElementById('totalMes').textContent = '$0 CLP';
            document.getElementById('promedioDiario').textContent = '$0 CLP';
            document.getElementById('categoriaPrincipal').textContent = '-';
            return;
        }
        
        // Calcular gastos del mes actual
        const fechaActual = new Date();
        const mesActual = fechaActual.getMonth();
        const añoActual = fechaActual.getFullYear();
        
        let totalMes = 0;
        const categoriasCont = {};
        
        filas.forEach(fila => {
            const fechaData = fila.dataset.fecha; // formato yyyy-MM-dd
            const categoriaData = fila.dataset.categoria;
            const montoCell = fila.querySelector('td:nth-child(6)');
            
            if (fechaData && montoCell) {
                const [año, mes, dia] = fechaData.split('-');
                const fechaGasto = new Date(parseInt(año), parseInt(mes) - 1, parseInt(dia));
                
                // Obtener monto del texto
                const montoTexto = montoCell.textContent.replace(/[^0-9]/g, '');
                const monto = parseInt(montoTexto) || 0;
                
                // Sumar si es del mes actual
                if (fechaGasto.getMonth() === mesActual && fechaGasto.getFullYear() === añoActual) {
                    totalMes += monto;
                }
                
                // Contar categorías
                if (categoriaData) {
                    categoriasCont[categoriaData] = (categoriasCont[categoriaData] || 0) + 1;
                }
            }
        });
        
        // Actualizar total del mes
        document.getElementById('totalMes').textContent = '$' + totalMes.toLocaleString('es-CL') + ' CLP';
        
        // Calcular promedio diario
        const diasDelMes = new Date(añoActual, mesActual + 1, 0).getDate();
        const promedio = Math.round(totalMes / diasDelMes);
        document.getElementById('promedioDiario').textContent = '$' + promedio.toLocaleString('es-CL') + ' CLP';
        
        // Encontrar categoría principal
        let catPrincipal = '-';
        let maxCont = 0;
        
        for (const [cat, cont] of Object.entries(categoriasCont)) {
            if (cont > maxCont) {
                maxCont = cont;
                const categoriaInfo = categorias[cat];
                catPrincipal = categoriaInfo ? `${categoriaInfo.icono} ${categoriaInfo.nombre}` : cat;
            }
        }
        
        document.getElementById('categoriaPrincipal').textContent = catPrincipal;
    }
    
    // Poblar filtro de meses y actualizar estadísticas al cargar la página
    if (filtroMes) {
        poblarFiltroMeses();
        actualizarEstadisticas();
    }
    
    if (buscarInput) {
        buscarInput.addEventListener('input', aplicarFiltros);
    }
    if (filtroCategoria) {
        filtroCategoria.addEventListener('change', aplicarFiltros);
    }
    if (filtroMes) {
        filtroMes.addEventListener('change', aplicarFiltros);
    }
    if (btnLimpiarFiltros) {
        btnLimpiarFiltros.addEventListener('click', function() {
            buscarInput.value = '';
            filtroCategoria.value = '';
            filtroMes.value = '';
            aplicarFiltros();
        });
    }
    
    function aplicarFiltros() {
        const textoBusqueda = buscarInput?.value.toLowerCase() || '';
        const categoriaFiltro = filtroCategoria?.value || '';
        const mesFiltro = filtroMes?.value || '';
        
        const filas = document.querySelectorAll('#tbodyGastos tr:not(#emptyState)');
        let filasVisibles = 0;
        let totalVisible = 0;
        
        filas.forEach(fila => {
            let mostrar = true;
            
            // Usar data attributes si están disponibles, sino usar el contenido del DOM
            const descripcion = (fila.dataset.descripcion || fila.querySelector('td:nth-child(3)')?.textContent || '').toLowerCase();
            const categoria = (fila.dataset.categoria || fila.querySelector('td:nth-child(4) .badge-categoria')?.textContent.trim() || '').toLowerCase();
            const fechaData = fila.dataset.fecha; // formato yyyy-MM-dd
            const fechaTexto = fila.querySelector('td:nth-child(2)')?.textContent.trim(); // formato dd/MM/yyyy
            const montoCell = fila.querySelector('td:nth-child(6)');
            
            // Filtro de búsqueda por descripción
            if (textoBusqueda && !descripcion.includes(textoBusqueda)) {
                mostrar = false;
            }
            
            // Filtro de categoría
            if (categoriaFiltro && categoria !== categoriaFiltro.toLowerCase()) {
                mostrar = false;
            }
            
            // Filtro de mes
            if (mesFiltro) {
                let cumpleMes = false;
                
                // Primero intentar con data-fecha (formato yyyy-MM-dd)
                if (fechaData) {
                    const [año, mes] = fechaData.split('-');
                    const fechaISO = `${año}-${mes}`;
                    cumpleMes = (fechaISO === mesFiltro);
                } 
                // Sino usar el texto visible (formato dd/MM/yyyy)
                else if (fechaTexto) {
                    const [dia, mes, año] = fechaTexto.split('/');
                    const fechaISO = `${año}-${mes.padStart(2, '0')}`;
                    cumpleMes = (fechaISO === mesFiltro);
                }
                
                if (!cumpleMes) {
                    mostrar = false;
                }
            }
            
            fila.style.display = mostrar ? '' : 'none';
            
            // Contar filas visibles y sumar total
            if (mostrar) {
                filasVisibles++;
                if (montoCell) {
                    const montoTexto = montoCell.textContent.replace(/[^0-9]/g, '');
                    totalVisible += parseInt(montoTexto) || 0;
                }
            }
        });
        
        // Actualizar total en el footer
        const montoTotalElement = document.getElementById('montoTotal');
        if (montoTotalElement) {
            if (filasVisibles === 0) {
                montoTotalElement.innerHTML = '<span>$0 CLP</span>';
            } else {
                montoTotalElement.innerHTML = `<span>$${totalVisible.toLocaleString('es-CL')} CLP</span>`;
            }
        }
        
        // Mostrar/ocultar mensaje de sin resultados
        const emptyState = document.getElementById('emptyState');
        if (filasVisibles === 0 && filas.length > 0) {
            if (!document.getElementById('noResultsRow')) {
                const noResultsRow = document.createElement('tr');
                noResultsRow.id = 'noResultsRow';
                noResultsRow.innerHTML = `
                    <td colspan="7" class="text-center py-5 text-muted">
                        <i class="bi bi-search fs-1 d-block mb-3"></i>
                        <p class="mb-0">No se encontraron gastos con los filtros aplicados</p>
                        <small>Intente modificar los criterios de búsqueda</small>
                    </td>
                `;
                document.getElementById('tbodyGastos').appendChild(noResultsRow);
            }
        } else {
            const noResultsRow = document.getElementById('noResultsRow');
            if (noResultsRow) {
                noResultsRow.remove();
            }
        }
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
