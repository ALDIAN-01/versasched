package dilash.service;

import dilash.model.Servicio;
import dilash.repository.ServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ServicioService servicioService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTodos_returnsAllServicios() {
        Servicio s1 = new Servicio();
        s1.setIdServicio(1L);
        s1.setNombre("Corte");

        Servicio s2 = new Servicio();
        s2.setIdServicio(2L);
        s2.setNombre("Peinado");

        when(servicioRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        List<Servicio> result = servicioService.getTodos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Corte", result.get(0).getNombre());
    }

    @Test
    void obtenerPorId_returnsPresentWhenFound_andEmptyWhenNotFound() {
        Servicio s = new Servicio();
        s.setIdServicio(5L);

        when(servicioRepository.findById(5L)).thenReturn(Optional.of(s));

        Optional<Servicio> found = servicioService.obtenerPorId(5L);
        assertTrue(found.isPresent());
        assertEquals(5L, found.get().getIdServicio());

        when(servicioRepository.findById(6L)).thenReturn(Optional.empty());
        Optional<Servicio> notFound = servicioService.obtenerPorId(6L);
        assertFalse(notFound.isPresent());
    }
}
