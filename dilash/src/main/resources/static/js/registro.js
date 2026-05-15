document.addEventListener('DOMContentLoaded', function() {
    const soloLetras = /^[A-Za-záéíóúÁÉÍÓÚüÜñÑ\s'-]+$/;
    const emailReg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    attachOnlyLetters('nombre');
    attachOnlyLetters('apellido');

    const telefono = document.getElementById('telefono');
    if (telefono) {
        telefono.addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '');
        });
    }

    const contrasena = document.getElementById('contrasena');
    if (contrasena) {
        contrasena.addEventListener('input', function() {
            checkStrength(this.value);
        });
    }

    attachTogglePassButtons();

    const form = document.getElementById('regForm');
    if (!form) return;

    form.addEventListener('submit', function(e) {
        let ok = true;
        const nombreEl = document.getElementById('nombre');
        const apellidoEl = document.getElementById('apellido');
        const correoEl = document.getElementById('correo');
        const telefonoEl = document.getElementById('telefono');
        const contraEl = document.getElementById('contrasena');
        const confEl = document.getElementById('confirmar');
        const terminosEl = document.getElementById('terminos');

        if (!nombreEl.value.trim() || nombreEl.value.trim().length < 2 || !soloLetras.test(nombreEl.value.trim())) {
            markInvalid(nombreEl);
            ok = false;
        } else {
            markValid(nombreEl);
        }

        if (!apellidoEl.value.trim() || apellidoEl.value.trim().length < 2 || !soloLetras.test(apellidoEl.value.trim())) {
            markInvalid(apellidoEl);
            ok = false;
        } else {
            markValid(apellidoEl);
        }

        if (!correoEl.value || !emailReg.test(correoEl.value)) {
            markInvalid(correoEl);
            ok = false;
        } else {
            markValid(correoEl);
        }

        if (!telefonoEl.value || !/^\d{7,15}$/.test(telefonoEl.value)) {
            markInvalid(telefonoEl);
            ok = false;
        } else {
            markValid(telefonoEl);
        }

        if (!contraEl.value || contraEl.value.length < 6) {
            markInvalid(contraEl, 'contrasenaError', 'Mínimo 6 caracteres.');
            ok = false;
        } else {
            markValid(contraEl, 'contrasenaError');
        }

        if (!confEl.value || confEl.value !== contraEl.value) {
            markInvalid(confEl, 'confirmarError', 'Las contraseñas no coinciden.');
            ok = false;
        } else {
            markValid(confEl, 'confirmarError');
        }

        if (!terminosEl || !terminosEl.checked) {
            markInvalid(terminosEl, 'terminosError', 'Debes aceptar los términos y condiciones.');
            ok = false;
        } else {
            markValid(terminosEl, 'terminosError');
        }

        if (!ok) {
            e.preventDefault();
        }
    });
});

function checkStrength(val) {
    const bar = document.getElementById('strengthBar');
    const text = document.getElementById('strengthText');

    if (!bar || !text) return;
    if (!val) {
        bar.style.background = '#eee';
        bar.style.width = '0';
        text.textContent = '';
        return;
    }

    let score = 0;
    if (val.length >= 6) score++;
    if (val.length >= 10) score++;
    if (/[A-Z]/.test(val) && /[a-z]/.test(val)) score++;
    if (/[0-9]/.test(val)) score++;
    if (/[^A-Za-z0-9]/.test(val)) score++;

    const levels = ['', 'Muy débil', 'Débil', 'Regular', 'Fuerte', 'Muy fuerte'];
    const colors = ['', '#ef4444', '#f97316', '#eab308', '#22c55e', '#16a34a'];
    bar.style.background = colors[score] || '#eee';
    bar.style.width = (score * 20) + '%';
    text.textContent = levels[score] || '';
    text.style.color = colors[score] || '#000';
}
