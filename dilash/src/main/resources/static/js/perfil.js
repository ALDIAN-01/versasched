document.addEventListener('DOMContentLoaded', function() {
    const emailReg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    attachOnlyLetters('nombre');
    attachOnlyLetters('apellido');

    const telefono = document.getElementById('telefonoP');
    if (telefono) {
        telefono.addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '');
        });
    }

    attachTogglePassButtons();

    const datosForm = document.getElementById('datosForm');
    if (datosForm) {
        datosForm.addEventListener('submit', function(e) {
            let ok = true;
            const nombre = document.getElementById('nombre');
            const apellido = document.getElementById('apellido');
            const correo = document.getElementById('correoP');
            const tel = document.getElementById('telefonoP');

            if (!nombre.value.trim() || nombre.value.trim().length < 2 || !/^[A-Za-záéíóúÁÉÍÓÚüÜñÑ\s'-]+$/.test(nombre.value.trim())) {
                markInvalid(nombre);
                ok = false;
            } else {
                markValid(nombre);
            }

            if (!apellido.value.trim() || apellido.value.trim().length < 2 || !/^[A-Za-záéíóúÁÉÍÓÚüÜñÑ\s'-]+$/.test(apellido.value.trim())) {
                markInvalid(apellido);
                ok = false;
            } else {
                markValid(apellido);
            }

            if (!correo.value || !emailReg.test(correo.value)) {
                markInvalid(correo);
                ok = false;
            } else {
                markValid(correo);
            }

            if (!tel.value || !/^\d{7,15}$/.test(tel.value)) {
                markInvalid(tel);
                ok = false;
            } else {
                markValid(tel);
            }

            if (!ok) {
                e.preventDefault();
            }
        });
    }

    const passForm = document.getElementById('passForm');
    if (passForm) {
        passForm.addEventListener('submit', function(e) {
            let ok = true;
            const act = document.getElementById('contrasenaActual');
            const nva = document.getElementById('contrasenaNueva');
            const conf = document.getElementById('confirmarNueva');

            if (!act.value) {
                markInvalid(act, 'actError', 'Ingresa tu contraseña actual.');
                ok = false;
            } else {
                markValid(act, 'actError');
            }

            if (!nva.value || nva.value.length < 6) {
                markInvalid(nva);
                ok = false;
            } else {
                markValid(nva);
            }

            if (!conf.value || conf.value !== nva.value) {
                markInvalid(conf);
                ok = false;
            } else {
                markValid(conf);
            }

            if (!ok) {
                e.preventDefault();
            }
        });
    }
});
