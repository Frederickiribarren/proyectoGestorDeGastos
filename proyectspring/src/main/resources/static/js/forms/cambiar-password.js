// Toggle password visibility
const togglePassword1 = document.getElementById('togglePassword1');
const togglePassword2 = document.getElementById('togglePassword2');
const inputPassword = document.getElementById('inputPassword');
const inputPassword2 = document.getElementById('inputPassword2');

if (togglePassword1 && inputPassword) {
  togglePassword1.addEventListener('click', function() {
    const type = inputPassword.getAttribute('type') === 'password' ? 'text' : 'password';
    inputPassword.setAttribute('type', type);
    this.classList.toggle('bi-eye');
    this.classList.toggle('bi-eye-slash');
  });
}
if (togglePassword2 && inputPassword2) {
  togglePassword2.addEventListener('click', function() {
    const type = inputPassword2.getAttribute('type') === 'password' ? 'text' : 'password';
    inputPassword2.setAttribute('type', type);
    this.classList.toggle('bi-eye');
    this.classList.toggle('bi-eye-slash');
  });
}

// Validación personalizada del formulario
const form = document.querySelector('form');
if (form) {
  form.addEventListener('submit', function(e) {
    const nuevaPassword = inputPassword.value;
    const confirmarPassword = inputPassword2.value;
    let passError = '';
    let confirmPassError = '';
    let valid = true;
    if (!nuevaPassword || nuevaPassword.length < 6) {
      passError = 'La nueva contraseña debe tener al menos 6 caracteres.';
      valid = false;
    }
    if (!confirmarPassword || confirmarPassword.length < 6) {
      confirmPassError = 'La confirmación debe tener al menos 6 caracteres.';
      valid = false;
    } else if (nuevaPassword !== confirmarPassword) {
      confirmPassError = 'Las contraseñas no coinciden.';
      valid = false;
    }
    document.getElementById('passError').textContent = passError;
    document.getElementById('confirmPassError').textContent = confirmPassError;
    if (!valid) {
      e.preventDefault();
    }
  });
}
