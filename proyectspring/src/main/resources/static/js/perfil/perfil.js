document.addEventListener("DOMContentLoaded", () => {
    const perfilForm = document.getElementById("perfilForm");
    const btnEditar = document.getElementById("btnEditar");
    const btnCancelar = document.getElementById("btnCancelar");
    const botonesEdicion = document.getElementById("botonesEdicion");

    // Campos del formulario (excepto email que siempre está readonly)
    const camposEditables = [
        document.getElementById("nombre"),
        document.getElementById("apellido"),
        document.getElementById("telefono"),
        document.getElementById("fechaNacimiento"),
        document.getElementById("pais"),
        document.getElementById("biografia")
    ];

    // Guardar valores originales
    let valoresOriginales = {};

    function guardarValoresOriginales() {
        camposEditables.forEach(campo => {
            if(campo) {
                valoresOriginales[campo.id] = campo.value;
            }
        });
    }

    function restaurarValoresOriginales() {
        camposEditables.forEach(campo => {
            if(campo && valoresOriginales[campo.id] !== undefined) {
                campo.value = valoresOriginales[campo.id];
            }
        });
    }

    function habilitarEdicion() {
        // Guardar valores actuales
        guardarValoresOriginales();

        // Habilitar campos
        camposEditables.forEach(campo => {
            if(campo) {
                campo.removeAttribute("readonly");
                campo.classList.add("border-primary");
            }
        });

        // Mostrar/ocultar botones
        btnEditar.style.display = "none";
        botonesEdicion.style.display = "block";
    }

    function deshabilitarEdicion() {
        // Deshabilitar campos
        camposEditables.forEach(campo => {
            if(campo) {
                campo.setAttribute("readonly", true);
                campo.classList.remove("border-primary");
            }
        });

        // Mostrar/ocultar botones
        btnEditar.style.display = "block";
        botonesEdicion.style.display = "none";
    }

    // Event Listeners
    if(btnEditar) {
        btnEditar.addEventListener("click", habilitarEdicion);
    }

    if(btnCancelar) {
        btnCancelar.addEventListener("click", () => {
            restaurarValoresOriginales();
            deshabilitarEdicion();
        });
    }

    // Guardar valores iniciales al cargar la página
    guardarValoresOriginales();

    // ============================================
    // TOGGLE EDICIÓN: CAMBIAR CONTRASEÑA
    // ============================================
    const btnEditarPassword = document.getElementById("btnEditarPassword");
    const btnCancelarPassword = document.getElementById("btnCancelarPassword");
    const botonesPassword = document.getElementById("botonesPassword");
    const camposPassword = [
        document.getElementById("passwordActual"),
        document.getElementById("nuevaPassword"),
        document.getElementById("confirmarPassword")
    ];

    let valoresOriginalesPassword = {};

    function guardarValoresPassword() {
        camposPassword.forEach(campo => {
            if (campo) {
                valoresOriginalesPassword[campo.id] = campo.value;
            }
        });
    }

    function habilitarEdicionPassword() {
        guardarValoresPassword();
        camposPassword.forEach(campo => {
            if (campo) {
                campo.removeAttribute("readonly");
                campo.classList.add("border-warning");
            }
        });
        btnEditarPassword.style.display = "none";
        botonesPassword.style.display = "block";
    }

    function deshabilitarEdicionPassword() {
        camposPassword.forEach(campo => {
            if (campo) {
                campo.setAttribute("readonly", true);
                campo.classList.remove("border-warning");
            }
        });
        btnEditarPassword.style.display = "block";
        botonesPassword.style.display = "none";
    }

    function restaurarValoresPassword() {
        camposPassword.forEach(campo => {
            if (campo && valoresOriginalesPassword[campo.id] !== undefined) {
                campo.value = valoresOriginalesPassword[campo.id];
            }
        });
    }

    if (btnEditarPassword) {
        btnEditarPassword.addEventListener("click", habilitarEdicionPassword);
    }

    if (btnCancelarPassword) {
        btnCancelarPassword.addEventListener("click", () => {
            restaurarValoresPassword();
            deshabilitarEdicionPassword();
        });
    }

    // ============================================
    // TOGGLE EDICIÓN: PALABRA DE SEGURIDAD
    // ============================================
    const btnEditarPalabraSeguridad = document.getElementById("btnEditarPalabraSeguridad");
    const btnCancelarPalabraSeguridad = document.getElementById("btnCancelarPalabraSeguridad");
    const botonesPalabraSeguridad = document.getElementById("botonesPalabraSeguridad");
    const campoPalabraSeguridad = document.getElementById("palabraSeguridad");

    let valorOriginalPalabraSeguridad = "";

    function guardarValorPalabraSeguridad() {
        if (campoPalabraSeguridad) {
            valorOriginalPalabraSeguridad = campoPalabraSeguridad.value;
        }
    }

    function habilitarEdicionPalabraSeguridad() {
        guardarValorPalabraSeguridad();
        if (campoPalabraSeguridad) {
            campoPalabraSeguridad.removeAttribute("readonly");
            campoPalabraSeguridad.classList.add("border-success");
        }
        btnEditarPalabraSeguridad.style.display = "none";
        botonesPalabraSeguridad.style.display = "block";
    }

    function deshabilitarEdicionPalabraSeguridad() {
        if (campoPalabraSeguridad) {
            campoPalabraSeguridad.setAttribute("readonly", true);
            campoPalabraSeguridad.classList.remove("border-success");
        }
        btnEditarPalabraSeguridad.style.display = "block";
        botonesPalabraSeguridad.style.display = "none";
    }

    function restaurarValorPalabraSeguridad() {
        if (campoPalabraSeguridad) {
            campoPalabraSeguridad.value = valorOriginalPalabraSeguridad;
        }
    }

    if (btnEditarPalabraSeguridad) {
        btnEditarPalabraSeguridad.addEventListener("click", habilitarEdicionPalabraSeguridad);
    }

    if (btnCancelarPalabraSeguridad) {
        btnCancelarPalabraSeguridad.addEventListener("click", () => {
            restaurarValorPalabraSeguridad();
            deshabilitarEdicionPalabraSeguridad();
        });
    }

    // ============================================
    // VALIDACIÓN MODAL ELIMINAR CUENTA
    // ============================================
    const deleteAccountModal = document.getElementById("deleteAccountModal");
    if (deleteAccountModal) {
        const confirmDeleteInput = document.getElementById("confirmDelete");
        const confirmCheckbox = document.getElementById("confirmarEliminacion");
        const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");

        function validarFormularioEliminar() {
            const textoEsValido = confirmDeleteInput.value === "ELIMINAR";
            const checkboxMarcado = confirmCheckbox.checked;
            
            if (textoEsValido && checkboxMarcado) {
                confirmDeleteBtn.disabled = false;
                confirmDeleteBtn.classList.remove("btn-secondary");
                confirmDeleteBtn.classList.add("btn-danger");
            } else {
                confirmDeleteBtn.disabled = true;
                confirmDeleteBtn.classList.remove("btn-danger");
                confirmDeleteBtn.classList.add("btn-secondary");
            }
        }

        confirmDeleteInput.addEventListener("input", validarFormularioEliminar);
        confirmCheckbox.addEventListener("change", validarFormularioEliminar);

        // Resetear el formulario cuando se cierra el modal
        deleteAccountModal.addEventListener("hidden.bs.modal", function () {
            confirmDeleteInput.value = "";
            confirmCheckbox.checked = false;
            validarFormularioEliminar();
        });
    }
});
