package dominio;

import dominio.excepcion.GarantiaExtendidaException;

public class Producto {

	private String codigo;
	private String nombre;
	private double precio;

	public Producto(String codigo, String nombre, double precio) {

		this.codigo = codigo;
		this.nombre = nombre;
		this.precio = precio;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public static boolean validarCodigo(String codigo) throws GarantiaExtendidaException {
		if (codigo == null || codigo.length() == 0) {
			throw new GarantiaExtendidaException("Este producto no cuenta con garantía extendida");
		}

		long count = codigo.chars().mapToObj(c -> String.valueOf((char) c))
				.filter(c -> "aeiou".indexOf(c.toLowerCase()) >= 0).count();

		if (count == 3) {
			throw new GarantiaExtendidaException("Este producto no cuenta con garantía extendida");
		}

		return true;
	}
}
