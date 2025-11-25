// Obtener token CSRF
const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

// Mostrar toasts simples en la esquina inferior (usa `configToastContainer` en el template)
function showToast(message, type = 'info', timeout = 3000) {
  const container = document.getElementById('configToastContainer');
  if (!container) {
    console.warn('No se encontró contenedor de toasts');
    return;
  }
  const id = 't' + Date.now();
  const el = document.createElement('div');
  el.className = `toast align-items-center text-white bg-${type} border-0 show mb-2`;
  el.setAttribute('role', 'alert');
  el.setAttribute('aria-live', 'assertive');
  el.setAttribute('aria-atomic', 'true');
  el.id = id;
  el.style.minWidth = '220px';
  el.innerHTML = `<div class="d-flex"><div class="toast-body">${message}</div><button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button></div>`;
  container.appendChild(el);
  setTimeout(() => { const t = document.getElementById(id); if (t) t.remove(); }, timeout);
}

// Cargar configuración de notificaciones al inicio
async function cargarConfiguracionNotificaciones() {
  try {
    const headers = {};
    if (csrfToken && csrfHeader) {
      headers[csrfHeader] = csrfToken;
    }
    const response = await fetch('/api/notificaciones/configuracion', {
      method: 'GET',
      headers: headers
    });
    if (response.ok) {
      const config = await response.json();
      document.getElementById('notif-email').checked = config.notificacionesEmail;
      document.getElementById('notif-transacciones').checked = config.alertasTransacciones;
      document.getElementById('notif-presupuesto').checked = config.alertasPresupuesto;
      document.getElementById('notif-reportes').checked = config.reportesMensuales;
      document.getElementById('notif-promociones').checked = config.promociones;
    }
  } catch (error) {
    console.error('Error al cargar configuración de notificaciones:', error);
  }
}

cargarConfiguracionNotificaciones();

// Guardar apariencia
const btnGuardarApariencia = document.getElementById('btnGuardarApariencia');
if (btnGuardarApariencia) {
  btnGuardarApariencia.addEventListener('click', async function() {
    const idioma = document.getElementById('language').value;
    const moneda = document.getElementById('currency').value;
    const btn = this;
    const textOriginal = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Guardando...';
    try {
      const headers = { 'Content-Type': 'application/json' };
      if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
      }
      const response = await fetch('/configuracion/apariencia', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify({tema: 'light', idioma, moneda})
      });
      const data = await response.json();
      if (data.success) {
        btn.innerHTML = '<i class="bi bi-check-circle me-2"></i>¡Guardado!';
        btn.classList.remove('btn-primary');
        btn.classList.add('btn-success');
        setTimeout(() => {
          btn.innerHTML = textOriginal;
          btn.classList.remove('btn-success');
          btn.classList.add('btn-primary');
          btn.disabled = false;
        }, 1500);
      } else {
        throw new Error(data.message);
      }
    } catch (error) {
      console.error('Error al guardar apariencia:', error);
      btn.innerHTML = '<i class="bi bi-x-circle me-2"></i>Error';
      btn.classList.add('btn-danger');
      setTimeout(() => {
        btn.innerHTML = textOriginal;
        btn.classList.remove('btn-danger');
        btn.disabled = false;
      }, 1500);
    }
  });
}

// Guardar notificaciones
const btnGuardarNotificaciones = document.getElementById('btnGuardarNotificaciones');
if (btnGuardarNotificaciones) {
  btnGuardarNotificaciones.addEventListener('click', async function() {
    const btn = this;
    const textOriginal = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Guardando...';
    try {
      const headers = { 'Content-Type': 'application/json' };
      if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
      }
      const response = await fetch('/api/notificaciones/configuracion', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify({
          notificacionesEmail: document.getElementById('notif-email').checked,
          alertasTransacciones: document.getElementById('notif-transacciones').checked,
          alertasPresupuesto: document.getElementById('notif-presupuesto').checked,
          reportesMensuales: document.getElementById('notif-reportes').checked,
          promociones: document.getElementById('notif-promociones').checked
        })
      });
      if (response.ok) {
        btn.innerHTML = '<i class="bi bi-check-circle me-2"></i>¡Guardado!';
        btn.classList.remove('btn-primary');
        btn.classList.add('btn-success');
        setTimeout(() => {
          btn.innerHTML = textOriginal;
          btn.classList.remove('btn-success');
          btn.classList.add('btn-primary');
          btn.disabled = false;
        }, 1500);
      } else {
        throw new Error('Error al guardar');
      }
    } catch (error) {
      console.error('Error al guardar notificaciones:', error);
      btn.innerHTML = '<i class="bi bi-x-circle me-2"></i>Error';
      btn.classList.add('btn-danger');
      setTimeout(() => {
        btn.innerHTML = textOriginal;
        btn.classList.remove('btn-danger');
        btn.disabled = false;
      }, 1500);
    }
  });
}

// Toggle 2FA
const btn2FA = document.getElementById('btn2FA');
if (btn2FA) {
  btn2FA.addEventListener('click', async function() {
    const btn = this;
    const estaActivado = btn.classList.contains('btn-success');
    btn.disabled = true;
    try {
      const headers = { 'Content-Type': 'application/json' };
      if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
      }
      const response = await fetch('/configuracion/autenticacion-dos-factores', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify({activar: !estaActivado})
      });
      const data = await response.json();
      if (data.success) {
        const icon = btn.querySelector('i');
        const span = btn.querySelector('span');
        if (data.activado) {
          btn.classList.remove('btn-outline-secondary');
          btn.classList.add('btn-success');
          icon.classList.remove('bi-toggle-off');
          icon.classList.add('bi-toggle-on');
          span.textContent = 'Activado';
        } else {
          btn.classList.remove('btn-success');
          btn.classList.add('btn-outline-secondary');
          icon.classList.remove('bi-toggle-on');
          icon.classList.add('bi-toggle-off');
          span.textContent = 'Desactivado';
        }
      }
      btn.disabled = false;
    } catch (error) {
      console.error('Error al actualizar 2FA:', error);
      showToast('Error al actualizar la autenticación de dos factores', 'danger');
      btn.disabled = false;
    }
  });
}

// Archivar datos antiguos
const btnArchivarDatos = document.getElementById('btnArchivarDatos');
if (btnArchivarDatos) {
  btnArchivarDatos.addEventListener('click', async function() {
    if (!confirm('¿Estás seguro de que deseas archivar los datos antiguos? Esta acción no se puede deshacer.')) {
      return;
    }
    const btn = this;
    const textOriginal = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Archivando...';
    try {
      const headers = { 'Content-Type': 'application/json' };
      if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
      }
      const response = await fetch('/configuracion/archivar-datos', {
        method: 'POST',
        headers: headers
      });
      const data = await response.json();
      if (data.success) {
        btn.innerHTML = '<i class="bi bi-check-circle me-2"></i>¡Archivado!';
        btn.classList.add('btn-success');
        setTimeout(() => {
          btn.innerHTML = textOriginal;
          btn.classList.remove('btn-success');
          btn.disabled = false;
        }, 2000);
      } else {
        throw new Error(data.message);
      }
    } catch (error) {
      console.error('Error al archivar datos:', error);
      btn.innerHTML = '<i class="bi bi-x-circle me-2"></i>Error';
      btn.classList.add('btn-danger');
      setTimeout(() => {
        btn.innerHTML = textOriginal;
        btn.classList.remove('btn-danger');
        btn.disabled = false;
      }, 2000);
    }
  });
}
