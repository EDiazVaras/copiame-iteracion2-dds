package ar.utn.dds.copiame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// es un test de integracion IT ya que estoy probando el programa completo
public class CopiameAppIT {

    @Test
    public void testBronceTexto() throws Exception {
        // Armado del Escenario
        Lote lote = new Lote("src/test/resources/lote1");
        lote.validar();
        lote.cargar();  //lee todos los archivos de lote
        float umbral = 0.5f;
        AnalsisDeCopia analisis = new AnalsisDeCopia(umbral, lote); //INSTANCIAMOS ANALISIS DE COPIA
        // Ejecución
        ResultadoLote resultado = analisis.procesar();
        // Chequeo
        assertEquals(1, resultado.getPosiblesCopias().size(),
                "Hay al menos una copia, ya que Pepe y Raúl se copiaron ");
    }

}
