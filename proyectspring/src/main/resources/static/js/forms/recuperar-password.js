document.addEventListener('DOMContentLoaded', function () {
  // Validación personalizada del formulario de recuperación
  const form = document.querySelector('form');
  if (form) {
    form.addEventListener('submit', function(e) {
      const emailInput = document.getElementById('inputEmail');
      const email = emailInput ? emailInput.value.trim() : '';
      let errorMsg = '';
      if (!email) {
        errorMsg = 'El correo electrónico es obligatorio.';
      } else if (!/^\S+@\S+\.\S+$/.test(email)) {
        errorMsg = 'Ingrese un correo electrónico válido.';
      }
      const emailError = document.getElementById('emailError');
      if (errorMsg) {
        e.preventDefault();
        if (emailError) emailError.textContent = errorMsg;
      } else {
        if (emailError) emailError.textContent = '';
      }
    });
  }
});
