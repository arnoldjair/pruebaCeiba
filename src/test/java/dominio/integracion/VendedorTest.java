package dominio.integracion;

import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Vendedor;
import dominio.Producto;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String COMPUTADOR_LENOVO = "Computador Lenovo";
	private static final String USUARIO = "Alejandro Plaza";
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
		repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void generarGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), USUARIO);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));

	}

	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), USUARIO);
		try {
			
			vendedor.generarGarantia(producto.getCodigo(), USUARIO);
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
		}
	}

	@Test
	public void testFechaFinal() {
		String date = "2018-08-16";
		String endDate = "2019-04-06";
		LocalDate localDate = LocalDate.parse(date);
		LocalDate finalDate = null;
		LocalDate testFinalDate = LocalDate.parse(endDate);

		int diasEfectivos = Vendedor.diasEfectivos(199);

		finalDate = localDate.plusDays(diasEfectivos);

		Assert.assertEquals(finalDate, testFinalDate);
	}
}
