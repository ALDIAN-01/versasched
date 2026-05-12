document.addEventListener('DOMContentLoaded', function() {
    AOS.init({ duration: 1000, once: true });

    const title = "El arte de resaltar tu mirada natural";
    const intro = "Creemos que cada mirada cuenta una historia y que tu belleza natural es única.";
    const detail = "En Dilash, combinamos precisión artesanal con estándares de lujo para ofrecerte un servicio que no solo realza tu belleza, sino que renueva tu confianza.";

    async function typeWriter(id, text, speed) {
        const el = document.getElementById(id);
        if (!el) return;
        el.innerHTML = ""; // Limpieza inicial absoluta
        
        for (let i = 0; i < text.length; i++) {
            el.innerHTML += text.charAt(i);
            await new Promise(r => setTimeout(r, speed));
        }
        el.classList.add("done");
    }

    // Usamos Intersection Observer para disparar la animación al ver la sección
    const observer = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting) {
            startSequence();
            observer.disconnect();
        }
    }, { threshold: 0.4 });

    const target = document.getElementById('conocenos');
    if (target) observer.observe(target);

    async function startSequence() {
        await new Promise(r => setTimeout(r, 400)); // Espera a que termine el fade-in de la imagen
        await typeWriter("typing-title", title, 45);
        await typeWriter("typing-para-presentation", intro, 25);
        await typeWriter("typing-para-detail", detail, 15);
        
        const h = document.getElementById("highlights");
        if (h) h.classList.add("opacity-1");
    }

    // 5. Lógica de la galería
    const imagenes = ["/img/conocenos1.jpg", "/img/conocenos2.jpg", "/img/conocenos3.jpg"];
    let currentIndex = 0;
    const galeriaImg = document.getElementById("galeria-img");
    
    if (galeriaImg) {
        document.getElementById("nextBtn")?.addEventListener("click", () => {
            currentIndex = (currentIndex + 1) % imagenes.length;
            galeriaImg.style.opacity = 0;
            setTimeout(() => {
                galeriaImg.src = imagenes[currentIndex];
                galeriaImg.style.opacity = 1;
            }, 250);
        });

        document.getElementById("prevBtn")?.addEventListener("click", () => {
            currentIndex = (currentIndex - 1 + imagenes.length) % imagenes.length;
            galeriaImg.style.opacity = 0;
            setTimeout(() => {
                galeriaImg.src = imagenes[currentIndex];
                galeriaImg.style.opacity = 1;
            }, 250);
        });
    }
});