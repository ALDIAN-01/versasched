document.addEventListener('DOMContentLoaded', function() {
    const lugarSelect = document.getElementById('lugar');
    const fechaInput = document.getElementById('fecha');
    const horarioSelect = document.getElementById('idHorario');
    const checks = document.querySelectorAll('.svc-check');

    if (lugarSelect) {
        lugarSelect.addEventListener('change', toggleDireccion);
        toggleDireccion();
    }

    if (fechaInput) {
        const hoy = new Date().toISOString().split('T')[0];
        fechaInput.setAttribute('min', hoy);
        fechaInput.addEventListener('change', actualizarHorarios);
    }

    checks.forEach(function(chk) {
        chk.addEventListener('change', calcularTotal);
    });

    const form = document.getElementById('agendarForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            let ok = true;
            const checkedServices = document.querySelectorAll('.svc-check:checked');
            const svcError = document.getElementById('svcError');
            const horario = document.getElementById('idHorario');
            const lugar = document.getElementById('lugar');
            const direccion = document.getElementById('direccion');
            const fecha = document.getElementById('fecha');

            if (checkedServices.length === 0) {
                if (svcError) svcError.style.display = 'block';
                ok = false;
            } else if (svcError) {
                svcError.style.display = 'none';
            }

            if (!fecha || !fecha.value) {
                fecha?.classList.add('is-invalid');
                ok = false;
            } else {
                fecha?.classList.remove('is-invalid');
            }

            if (!horario || !horario.value) {
                horario?.classList.add('is-invalid');
                ok = false;
            } else {
                horario?.classList.remove('is-invalid');
            }

            if (!lugar || !lugar.value) {
                lugar?.classList.add('is-invalid');
                ok = false;
            } else {
                lugar?.classList.remove('is-invalid');
            }

            if (lugar?.value === 'Mi direccion' && direccion && !direccion.value.trim()) {
                direccion.classList.add('is-invalid');
                ok = false;
            } else {
                direccion?.classList.remove('is-invalid');
            }

            if (!ok) {
                e.preventDefault();
            }
        });
    }
});

function toggleDireccion() {
    const select = document.getElementById('lugar');
    const div = document.getElementById('direccionDiv');
    if (!select || !div) return;
    div.style.display = select.value === 'Mi direccion' ? 'block' : 'none';
}

function actualizarHorarios() {
    const fechaSeleccionada = document.getElementById('fecha')?.value;
    const selectHorario = document.getElementById('idHorario');
    if (!fechaSeleccionada || !selectHorario) return;

    fetch(`/api/citas/horarios-ocupados?fecha=${fechaSeleccionada}`)
        .then(response => response.json())
        .then(idsOcupados => {
            Array.from(selectHorario.options).forEach(option => {
                if (option.value === '') return;
                const isOcupado = idsOcupados.includes(parseInt(option.value));
                if (isOcupado) {
                    option.disabled = true;
                    option.text = option.text.replace(' (Ocupado)', '') + ' (Ocupado)';
                    option.style.color = '#999';
                } else {
                    option.disabled = false;
                    option.text = option.text.replace(' (Ocupado)', '');
                    option.style.color = 'initial';
                }
            });

            if (selectHorario.selectedOptions[0]?.disabled) {
                selectHorario.value = '';
            }
        })
        .catch(error => console.error('Error al verificar horarios:', error));
}

function calcularTotal() {
    const checks = document.querySelectorAll('.svc-check:checked');
    let total = 0;
    checks.forEach(function(c) {
        total += parseFloat(c.dataset.precio) || 0;
    });
    const box = document.getElementById('totalBox');
    const amt = document.getElementById('totalAmount');
    if (box && amt) {
        if (checks.length > 0) {
            box.style.display = 'flex';
            amt.textContent = '$' + total.toLocaleString('es-CO');
        } else {
            box.style.display = 'none';
        }
    }
}
