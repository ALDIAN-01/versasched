document.addEventListener('DOMContentLoaded', function() {
    attachTogglePassButtons();

    const form = document.getElementById('loginForm');
    if (!form) return;

    form.addEventListener('submit', function(e) {
        let valid = true;
        const correo = document.getElementById('correo');
        const contrasena = document.getElementById('contrasena');

        if (!correo.value || !isValidEmail(correo.value)) {
            markInvalid(correo);
            valid = false;
        } else {
            markValid(correo);
        }

        if (!contrasena.value || contrasena.value.length < 4) {
            markInvalid(contrasena);
            const errorEl = document.getElementById('contrasenaError');
            if (errorEl) {
                errorEl.style.display = 'block';
                errorEl.textContent = 'La contraseña es requerida.';
            }
            valid = false;
        } else {
            markValid(contrasena, 'contrasenaError');
        }

        if (!valid) {
            e.preventDefault();
        }
    });
});
