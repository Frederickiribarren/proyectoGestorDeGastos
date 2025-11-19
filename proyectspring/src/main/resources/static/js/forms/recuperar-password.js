// Validación personalizada del formulario de recuperación
const form = document.querySelector('form');
if (form) {
  form.addEventListener('submit', function(e) {
    const email = document.getElementById('inputEmail').value.trim();
    let errorMsg = '';
    if (!email) {
      errorMsg = 'El correo electrónico es obligatorio.';
    } else if (!/^\S+@\S+\.\S+$/.test(email)) {
      errorMsg = 'Ingrese un correo electrónico válido.';
    }
    const emailError = document.getElementById('emailError');
    if (errorMsg) {
      e.preventDefault();
      emailError.textContent = errorMsg;
    } else {
      emailError.textContent = '';
    }
  });
}
