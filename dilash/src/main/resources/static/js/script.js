/**
 * Script principal de la página de inicio (index.html)
 * Incluye: animaciones AOS, typewriter, galería y efectos de scroll
 */

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar AOS (Animate On Scroll)
    AOS.init({ duration: 1000, once: true });

    // Textos para la animación de escritura
    const title = "El arte de resaltar tu mirada natural";
    const intro = "Creemos que cada mirada cuenta una historia y que tu belleza natural es única.";
    const detail = "En Dilash, combinamos precisión artesanal con estándares de lujo para ofrecerte un servicio que no solo realza tu belleza, sino que renueva tu confianza.";

    /**
     * Efecto de escritura (typewriter) con velocidad configurable.
     */
    async function typeWriter(id, text, speed) {
        const el = document.getElementById(id);
        if (!el) return;
        el.innerHTML = "";
        
        for (let i = 0; i < text.length; i++) {
            el.innerHTML += text.charAt(i);
            await new Promise(r => setTimeout(r, speed));
        }
        el.classList.add("done");
    }

    /**
     * Usa Intersection Observer para activar las animaciones al verlas en pantalla.
     */
    const observer = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting) {
            startSequence();
            observer.disconnect();
        }
    }, { threshold: 0.4 });

    const target = document.getElementById('conocenos');
    if (target) observer.observe(target);

    /**
     * Secuencia de animaciones: títulos y detalles con efecto typewriter.
     */
    async function startSequence() {
        await new Promise(r => setTimeout(r, 400));
        await typeWriter("typing-title", title, 45);
        await typeWriter("typing-para-presentation", intro, 25);
        await typeWriter("typing-para-detail", detail, 15);
        
        const h = document.getElementById("highlights");
        if (h) h.classList.add("opacity-1");
    }

    /**
     * Control de galería: navegación anterior/siguiente con transición de opacidad.
     */
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