document.addEventListener('DOMContentLoaded', () => {
    // Elementos del DOM
    const formEl = document.getElementById('form');
    const email = document.getElementById('inputEmail');
    const pass = document.getElementById('inputPassword');
    const submitBtn = document.getElementById('loginForm');
    const emailError = document.getElementById('emailError');
    const passError = document.getElementById('passError');
    const warning = document.getElementById('warning');

    if (!formEl) return;

    // Regex para validar email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    // Función para limpiar todos los mensajes de error
    function clearAllErrors() {
        if (emailError) {
            emailError.textContent = '';
            emailError.classList.remove('text-danger');
        }
        if (passError) {
            passError.textContent = '';
            passError.classList.remove('text-danger');
        }
        if (warning) {
            warning.textContent = '';
            warning.classList.remove('text-danger', 'text-success');
        }
        if (email) email.classList.remove('is-invalid', 'is-valid');
        if (pass) pass.classList.remove('is-invalid', 'is-valid');
    }

    // Función para mostrar error en campo específico
    function showFieldError(field, errorElement, message) {
        if (field) {
            field.classList.remove('is-valid');
            field.classList.add('is-invalid');
        }
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.classList.add('text-danger');
        }
    }

    // Función para mostrar campo válido (sin borde verde)
    function showFieldValid(field, errorElement) {
        if (field) {
            field.classList.remove('is-invalid', 'is-valid');
        }
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.classList.remove('text-danger');
        }
    }

    // Limpiar error cuando el usuario escribe
    if (email) {
        email.addEventListener('input', () => {
            if (emailError) {
                emailError.textContent = '';
                emailError.classList.remove('text-danger');
            }
            email.classList.remove('is-invalid', 'is-valid');
        });
    }

    if (pass) {
        pass.addEventListener('input', () => {
            if (passError) {
                passError.textContent = '';
                passError.classList.remove('text-danger');
            }
            pass.classList.remove('is-invalid', 'is-valid');
        });
    }

    // Prevenir validación HTML5 nativa
    if (email) {
        email.addEventListener('invalid', (e) => {
            e.preventDefault();
        });
    }
    if (pass) {
        pass.addEventListener('invalid', (e) => {
            e.preventDefault();
        });
    }

    // Manejar submit del formulario
    formEl.addEventListener('submit', (e) => {
        e.preventDefault();
        clearAllErrors();

        let isValid = true;
        const emailVal = email ? email.value.trim() : '';
        const passVal = pass ? pass.value.trim() : '';

        // Validar email
        if (!emailVal) {
            showFieldError(email, emailError, 'El correo no debe estar vacío.');
            if (isValid && email) email.focus();
            isValid = false;
        } else if (!emailRegex.test(emailVal)) {
            showFieldError(email, emailError, 'Formato de correo inválido.');
            if (isValid && email) email.focus();
            isValid = false;
        } else {
            showFieldValid(email, emailError);
        }

        // Validar contraseña
        if (!passVal) {
            showFieldError(pass, passError, 'La contraseña no debe estar vacía.');
            if (isValid && pass) pass.focus();
            isValid = false;
        } else if (passVal.length < 6) {
            showFieldError(pass, passError, 'Mínimo 6 caracteres.');
            if (isValid && pass) pass.focus();
            isValid = false;
        } else {
            showFieldValid(pass, passError);
        }

        // Si todo es válido, enviar formulario
        if (isValid) {
            if (warning) {
                warning.textContent = 'Iniciando sesión...';
                warning.classList.add('text-success');
            }
            
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.classList.add('disabled');
            }

            // Enviar formulario
            formEl.submit();
        } else {
            if (warning) {
                warning.textContent = 'Por favor, corrija los errores antes de continuar.';
                warning.classList.add('text-danger');
            }
        }
    });

    // Toggle para mostrar/ocultar contraseña
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('inputPassword');

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            
            // Cambiar ícono
            this.classList.toggle('bi-eye');
            this.classList.toggle('bi-eye-slash');
        });
    }
});
