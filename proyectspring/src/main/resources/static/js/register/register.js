document.addEventListener('DOMContentLoaded', () => {
    const formEl = document.getElementById('registerForm');
    const nameInput = document.getElementById('floatingName');
    const emailInput = document.getElementById('floatingInput');
    const passwordInput = document.getElementById('floatingPassword');
    const confirmInput = document.getElementById('floatingPasswordConfirm');
    const termsCheck = document.getElementById('termsCheck');
    const registerBtn = document.getElementById('registerBtn');
    
    const nameError = document.getElementById('nameError');
    const emailError = document.getElementById('emailError');
    const passwordError = document.getElementById('passwordError');
    const confirmError = document.getElementById('confirmError');
    const termsError = document.getElementById('termsError');
    const warning = document.getElementById('warning');

    if (!formEl) return;

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const nameRegex = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{3,50}$/;

    function clearAllErrors() {
        [nameError, emailError, passwordError, confirmError, termsError].forEach(el => {
            if (el) {
                el.textContent = '';
                el.classList.remove('text-danger');
            }
        });
        if (warning) {
            warning.textContent = '';
            warning.classList.remove('text-danger', 'text-success');
        }
        [nameInput, emailInput, passwordInput, confirmInput].forEach(input => {
            if (input) input.classList.remove('is-invalid', 'is-valid');
        });
    }

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

    function showFieldValid(field, errorElement) {
        if (field) {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        }
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.classList.remove('text-danger');
        }
    }

    if (nameInput) {
        nameInput.addEventListener('input', () => {
            if (nameError) {
                nameError.textContent = '';
                nameError.classList.remove('text-danger');
            }
            nameInput.classList.remove('is-invalid');
        });
    }

    if (emailInput) {
        emailInput.addEventListener('input', () => {
            if (emailError) {
                emailError.textContent = '';
                emailError.classList.remove('text-danger');
            }
            emailInput.classList.remove('is-invalid');
        });
    }

    if (passwordInput) {
        passwordInput.addEventListener('input', () => {
            if (passwordError) {
                passwordError.textContent = '';
                passwordError.classList.remove('text-danger');
            }
            passwordInput.classList.remove('is-invalid');
        });
    }

    if (confirmInput) {
        confirmInput.addEventListener('input', () => {
            if (confirmError) {
                confirmError.textContent = '';
                confirmError.classList.remove('text-danger');
            }
            confirmInput.classList.remove('is-invalid');
        });
    }

    [nameInput, emailInput, passwordInput, confirmInput].forEach(input => {
        if (input) {
            input.addEventListener('invalid', (e) => {
                e.preventDefault();
            });
        }
    });

    formEl.addEventListener('submit', (e) => {
        e.preventDefault();
        clearAllErrors();

        let isValid = true;
        const nameVal = nameInput ? nameInput.value.trim() : '';
        const emailVal = emailInput ? emailInput.value.trim() : '';
        const passwordVal = passwordInput ? passwordInput.value.trim() : '';
        const confirmVal = confirmInput ? confirmInput.value.trim() : '';
        const termsChecked = termsCheck ? termsCheck.checked : false;

        if (!nameVal) {
            showFieldError(nameInput, nameError, 'El nombre no debe estar vacío.');
            if (isValid && nameInput) nameInput.focus();
            isValid = false;
        } else if (nameVal.length < 3) {
            showFieldError(nameInput, nameError, 'El nombre debe tener al menos 3 caracteres.');
            if (isValid && nameInput) nameInput.focus();
            isValid = false;
        } else if (!nameRegex.test(nameVal)) {
            showFieldError(nameInput, nameError, 'Solo letras y espacios.');
            if (isValid && nameInput) nameInput.focus();
            isValid = false;
        } else {
            showFieldValid(nameInput, nameError);
        }

        if (!emailVal) {
            showFieldError(emailInput, emailError, 'El correo no debe estar vacío.');
            if (isValid && emailInput) emailInput.focus();
            isValid = false;
        } else if (!emailRegex.test(emailVal)) {
            showFieldError(emailInput, emailError, 'Formato de correo inválido.');
            if (isValid && emailInput) emailInput.focus();
            isValid = false;
        } else {
            showFieldValid(emailInput, emailError);
        }

        if (!passwordVal) {
            showFieldError(passwordInput, passwordError, 'La contraseña no debe estar vacía.');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else if (passwordVal.length < 6) {
            showFieldError(passwordInput, passwordError, 'Mínimo 6 caracteres.');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else if (passwordVal.length > 20) {
            showFieldError(passwordInput, passwordError, 'Máximo 20 caracteres.');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else if (!/[A-Z]/.test(passwordVal)) {
            showFieldError(passwordInput, passwordError, 'Debe tener al menos una mayúscula.');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else if (!/[0-9]/.test(passwordVal)) {
            showFieldError(passwordInput, passwordError, 'Debe tener al menos un número.');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(passwordVal)) {
            showFieldError(passwordInput, passwordError, 'Debe tener al menos un carácter especial.');
            if (isValid && passwordInput) passwordInput.focus();
            isValid = false;
        } else {
            showFieldValid(passwordInput, passwordError);
        }

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

        if (!termsChecked) {
            if (warning) {
                warning.textContent = 'Debes aceptar los términos y condiciones para continuar.';
                warning.classList.add('text-danger');
            }
            isValid = false;
            return;
        }

        if (isValid) {
            if (warning) {
                warning.textContent = 'Creando cuenta...';
                warning.classList.add('text-success');
            }
            
            if (registerBtn) {
                registerBtn.disabled = true;
                registerBtn.classList.add('disabled');
            }

            setTimeout(() => {
                window.location.href = '/dashboard';
            }, 800);
        } else {
            if (warning) {
                warning.textContent = 'Por favor, corrija los errores antes de continuar.';
                warning.classList.add('text-danger');
            }
        }
    });
});
