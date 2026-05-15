function togglePass(id, btn) {
    const input = document.getElementById(id);
    const icon = btn?.querySelector('i');
    if (!input || !icon) return;
    input.type = input.type === 'password' ? 'text' : 'password';
    icon.className = input.type === 'password' ? 'bi bi-eye-slash' : 'bi bi-eye';
}

function isValidEmail(value) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

function markInvalid(el, errId, msg) {
    if (!el) return;
    el.classList.add('is-invalid');
    if (!errId) return;
    const err = document.getElementById(errId);
    if (err) {
        err.textContent = msg || err.textContent;
        err.style.display = 'block';
    }
}

function markValid(el, errId) {
    if (!el) return;
    el.classList.remove('is-invalid');
    if (!errId) return;
    const err = document.getElementById(errId);
    if (err) {
        err.style.display = 'none';
    }
}

function attachOnlyLetters(id) {
    const el = document.getElementById(id);
    if (!el) return;
    el.addEventListener('input', function() {
        this.value = this.value.replace(/[^A-Za-záéíóúÁÉÍÓÚüÜñÑ\s'-]/g, '');
    });
}

function attachTogglePassButtons() {
    document.querySelectorAll('.btn-toggle-pass').forEach(function(btn) {
        btn.addEventListener('click', function() {
            const target = btn.dataset.target;
            if (target) {
                togglePass(target, btn);
            }
        });
    });
}
