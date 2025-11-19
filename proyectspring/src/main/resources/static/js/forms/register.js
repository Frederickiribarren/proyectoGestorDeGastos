document.addEventListener('DOMContentLoaded', () => {
    const formEl = document.getElementById('form');
    const nameInput = document.getElementById('inputNombre');
    const emailInput = document.getElementById('inputEmail');
    const passwordInput = document.getElementById('inputPassword');
    const confirmInput = document.getElementById('inputConfirmPassword');
    const termsCheck = document.getElementById('inputTerminos');
    const registerBtn = document.getElementById('registerForm');
    
    const nameError = document.getElementById('nombreError');
    const emailError = document.getElementById('emailError');
    const passwordError = document.getElementById('passError');
    const confirmError = document.getElementById('confirmPassError');
    const warning = document.getElementById('warning');

    if (!formEl) return;

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const nameRegex = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{3,100}$/;

    function clearAllErrors() {
        [nameError, emailError, passwordError, confirmError].forEach(el => {
            if (el) {
                el.textContent = '';
                el.classList.remove('text-danger');
            }
        });
        if (warning) {
            warning.textContent = '';
            warning.classList.remove('text-danger', 'text-success');
        }
        // No aplicar clases de validación de Bootstrap
    }

    function showFieldError(field, errorElement, message) {
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.classList.add('text-danger');
        }
    }

    function showFieldValid(field, errorElement) {
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.classList.remove('text-danger');
        }
    }

    // Limpiar errores al escribir
    if (nameInput) {
        nameInput.addEventListener('input', () => {
            if (nameError) nameError.textContent = '';
            const serverError = document.getElementById('nombreErrorServer');
            if (serverError) serverError.style.display = 'none';
            if (warning) {
                warning.textContent = '';
                warning.classList.remove('text-danger', 'text-success');
            }
        });
        nameInput.addEventListener('invalid', (e) => e.preventDefault());
    }

    if (emailInput) {
        emailInput.addEventListener('input', () => {
            if (emailError) emailError.textContent = '';
            const serverError = document.getElementById('emailErrorServer');
            if (serverError) serverError.style.display = 'none';
            if (warning) {
                warning.textContent = '';
                warning.classList.remove('text-danger', 'text-success');
            }
        });
        emailInput.addEventListener('invalid', (e) => e.preventDefault());
    }

    if (passwordInput) {
        passwordInput.addEventListener('input', () => {
            if (passwordError) passwordError.textContent = '';
            const serverError = document.getElementById('passErrorServer');
            if (serverError) serverError.style.display = 'none';
            if (warning) {
                warning.textContent = '';
                warning.classList.remove('text-danger', 'text-success');
            }
        });
        passwordInput.addEventListener('invalid', (e) => e.preventDefault());
    }

    if (confirmInput) {
        confirmInput.addEventListener('input', () => {
            if (confirmError) confirmError.textContent = '';
            const serverError = document.getElementById('confirmPassErrorServer');
            if (serverError) serverError.style.display = 'none';
            if (warning) {
                warning.textContent = '';
                warning.classList.remove('text-danger', 'text-success');
            }
        });
        confirmInput.addEventListener('invalid', (e) => e.preventDefault());
    }

    // VALIDACIÓN AL ENVIAR
    formEl.addEventListener('submit', (e) => {
        e.preventDefault();
        clearAllErrors();

        let isValid = true;
        const nameVal = nameInput ? nameInput.value.trim() : '';
        const emailVal = emailInput ? emailInput.value.trim() : '';
        const passwordVal = passwordInput ? passwordInput.value.trim() : '';
        const confirmVal = confirmInput ? confirmInput.value.trim() : '';
        const termsChecked = termsCheck ? termsCheck.checked : false;

        // Validar nombre
        if (!nameVal) {
            showFieldError(nameInput, nameError, 'El nombre es obligatorio.');
            if (isValid && nameInput) nameInput.focus();
            isValid = false;
        } else if (nameVal.length < 3 || nameVal.length > 100) {
            showFieldError(nameInput, nameError, 'El nombre debe tener entre 3 y 100 caracteres.');
            if (isValid && nameInput) nameInput.focus();
            isValid = false;
        } else if (!nameRegex.test(nameVal)) {
            showFieldError(nameInput, nameError, 'Solo letras y espacios permitidos.');
            if (isValid && nameInput) nameInput.focus();
            isValid = false;
        } else {
            showFieldValid(nameInput, nameError);
        }

        // Validar email
        if (!emailVal) {
            showFieldError(emailInput, emailError, 'El email es obligatorio.');
            if (isValid && emailInput) emailInput.focus();
            isValid = false;
        } else if (!emailRegex.test(emailVal)) {
            showFieldError(emailInput, emailError, 'Formato de email inválido.');
            if (isValid && emailInput) emailInput.focus();
            isValid = false;
        } else {
            showFieldValid(emailInput, emailError);
        }

        // Validar contraseña
        if (!passwordVal) {
            showFieldError(passwordInput, passwordError, 'La contraseña es obligatoria.');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else if (passwordVal.length < 6 || passwordVal.length > 20) {
            showFieldError(passwordInput, passwordError, 'Entre 6 y 20 caracteres.');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else if (!/[A-Z]/.test(passwordVal)) {
            showFieldError(passwordInput, passwordError, 'Al menos una mayúscula.');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else if (!/[0-9]/.test(passwordVal)) {
            showFieldError(passwordInput, passwordError, 'Al menos un número.');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else if (!/[@$!%*?&#.\-_]/.test(passwordVal)) {
            showFieldError(passwordInput, passwordError, 'Al menos un carácter especial (@$!%*?&#.-_).');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else {
            showFieldValid(passwordInput, passwordError);
        }

        // Validar confirmación
        if (!confirmVal) {
            showFieldError(confirmInput, confirmError, 'Debes confirmar la contraseña.');
            if (isValid && confirmInput) confirmInput.focus();
            isValid = false;
        } else if (passwordVal !== confirmVal) {
            showFieldError(confirmInput, confirmError, 'Las contraseñas no coinciden.');
            if (isValid && confirmInput) confirmInput.focus();
            isValid = false;
        } else {
            showFieldValid(confirmInput, confirmError);
        }

        // Validar términos
        if (!termsChecked) {
            if (warning) {
                warning.textContent = 'Debes aceptar los términos y condiciones.';
                warning.classList.add('text-danger');
            }
            isValid = false;
        }

        // Si pasa validación frontend, enviar al backend
        if (isValid) {
            if (warning) {
                warning.textContent = 'Creando cuenta...';
                warning.classList.add('text-success');
            }
            
            if (registerBtn) {
                registerBtn.disabled = true;
                registerBtn.classList.add('disabled');
            }

            // ENVIAR AL BACKEND
            formEl.submit();
        } else {
            if (warning) {
                warning.textContent = 'Por favor, corrige los errores.';
                warning.classList.add('text-danger');
            }
        }
    });

    // Toggle para mostrar/ocultar contraseñas
    const togglePassword = document.getElementById('togglePassword');
    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            this.classList.toggle('bi-eye');
            this.classList.toggle('bi-eye-slash');
        });
    }

    if (toggleConfirmPassword && confirmInput) {
        toggleConfirmPassword.addEventListener('click', function() {
            const type = confirmInput.getAttribute('type') === 'password' ? 'text' : 'password';
            confirmInput.setAttribute('type', type);
            this.classList.toggle('bi-eye');
            this.classList.toggle('bi-eye-slash');
        });
    }
});
