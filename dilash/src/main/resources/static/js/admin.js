document.addEventListener('DOMContentLoaded', function() {
    const flash = document.getElementById('flashMsg');
    if (flash) {
        setTimeout(function() {
            flash.style.transition = 'opacity .5s';
            flash.style.opacity = '0';
        }, 4000);
    }

    calcularStats();
    actualizarContador();

    const hoy = new Date().toISOString().split('T')[0];
    const editarFecha = document.getElementById('editarFecha');
    if (editarFecha) {
        editarFecha.setAttribute('min', hoy);
    }

    document.querySelectorAll('.estado-select').forEach(function(select) {
        select.addEventListener('change', function() {
            this.form.submit();
        });
    });

    document.querySelectorAll('.btn-filter').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            aplicarFiltros();
        });
    });

    document.querySelectorAll('.btn-clear').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            limpiarFiltros();
        });
    });

    document.querySelectorAll('.btn-action.edit').forEach(function(btn) {
        btn.addEventListener('click', function() {
            abrirEditar(btn);
        });
    });

    document.querySelectorAll('.btn-action.delete').forEach(function(btn) {
        btn.addEventListener('click', function() {
            confirmarEliminar(btn);
        });
    });

    document.querySelectorAll('#filtroCliente, #filtroDesde, #filtroHasta, #filtroEstado').forEach(function(input) {
        input.addEventListener('input', aplicarFiltros);
        input.addEventListener('change', aplicarFiltros);
    });
});

function calcularStats() {
    const rows = document.querySelectorAll('#citasBody tr');
    let ag = 0, re = 0, ingRe = 0, ingAg = 0;

    rows.forEach(function(r) {
        const estado = r.dataset.estado?.trim();
        const txt = r.querySelector('td:nth-child(6)')?.textContent?.trim().replace(/[$.\s]/g, '') || '0';
        const total = parseFloat(txt) || 0;

        if (estado === 'Agendada') {
            ag++;
            ingAg += total;
        } else if (estado === 'Realizada') {
            re++;
            ingRe += total;
        }
    });

    document.getElementById('statAgendadas').textContent = ag;
    document.getElementById('statRealizadas').textContent = re;
    document.getElementById('statIngresos').textContent = '$' + ingRe.toLocaleString('es-CO');
    document.getElementById('statIngresosAgendadas').textContent = ingAg > 0 ? 'Esperados: $' + ingAg.toLocaleString('es-CO') : '';
}

function aplicarFiltros() {
    const qC = document.getElementById('filtroCliente')?.value.toLowerCase();
    const qD = document.getElementById('filtroDesde')?.value;
    const qH = document.getElementById('filtroHasta')?.value;
    const qE = document.getElementById('filtroEstado')?.value;

    document.querySelectorAll('#citasBody tr').forEach(function(r) {
        const ok = (!qC || r.dataset.cliente?.toLowerCase().includes(qC))
                && (!qD || r.dataset.fecha >= qD)
                && (!qH || r.dataset.fecha <= qH)
                && (!qE || r.dataset.estado === qE);
        r.style.display = ok ? '' : 'none';
    });

    actualizarContador();
}

function limpiarFiltros() {
    document.getElementById('filtroCliente').value = '';
    document.getElementById('filtroDesde').value = '';
    document.getElementById('filtroHasta').value = '';
    document.getElementById('filtroEstado').value = '';
    aplicarFiltros();
}

function actualizarContador() {
    const v = document.querySelectorAll('#citasBody tr:not([style*="display: none"])').length;
    document.getElementById('contadorVisible').textContent = v;
}

function abrirEditar(btn) {
    document.getElementById('editarIdCita').value = btn.dataset.id;
    document.getElementById('editarCliente').textContent = btn.dataset.cliente;
    document.getElementById('editarFecha').value = btn.dataset.fecha;
    document.getElementById('editarObs').value = btn.dataset.observaciones || '';

    const hora = btn.dataset.hora;
    const sel = document.getElementById('editarHorario');
    if (sel) {
        for (let i = 0; i < sel.options.length; i++) {
            if (sel.options[i].text.startsWith(hora?.substring(0, 5) || '__')) {
                sel.selectedIndex = i;
                break;
            }
        }
    }

    const modalEditar = new bootstrap.Modal(document.getElementById('modalEditar'));
    modalEditar.show();
}

function confirmarEliminar(btn) {
    document.getElementById('eliminarIdCita').value = btn.dataset.id;
    document.getElementById('modalClienteNombre').textContent = btn.dataset.cliente;
    const modalEliminar = new bootstrap.Modal(document.getElementById('modalEliminar'));
    modalEliminar.show();
}
