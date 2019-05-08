package dominio;

import dominio.repositorio.RepositorioProducto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo, String nombreCliente) {

        LocalDate localDate = LocalDate.now();
        Date fechaSolicitudGarantia = new Date();
        Date fechaFinGarantia = null;
        double precioGarantia = 0;

        boolean codigoValido = Producto.validarCodigo(codigo);

        // TODO: If the exception has not been thrown, always will be true.
        if (codigoValido) {
            Producto producto = this.repositorioProducto.obtenerPorCodigo(codigo);
            if (producto == null) {
                throw new GarantiaExtendidaException("Producto inexistente");
            }
            if (this.tieneGarantia(codigo)) {
                throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
            }

            int diaSemana = localDate.getDayOfWeek().getValue();
            int diasEfectivos = 0;

            if (producto.getPrecio() > 500000) {
                precioGarantia = producto.getPrecio() * 1.20;
                if (diaSemana == 1) {
                    diasEfectivos = this.diasEfectivos(199);
                } else {
                    int offset = (8 - diaSemana);
                    diasEfectivos = offset + this.diasEfectivos(199 - offset);
                }
            } else {
                diasEfectivos = 99;
                precioGarantia = producto.getPrecio() * 1.10;
            }

            localDate = localDate.plusDays(diasEfectivos);
            diaSemana = localDate.getDayOfWeek().getValue();

            if (diaSemana == 7) {
                localDate = localDate.plusDays(1);
            }

            fechaFinGarantia = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

            GarantiaExtendida garantia = new GarantiaExtendida(producto, fechaSolicitudGarantia, fechaFinGarantia,
                    precioGarantia, nombreCliente);

            this.repositorioGarantia.agregar(garantia);
        }

    }

    public static int diasEfectivos(double dias) {
        double restantes = -1;
        double localDias = dias;
        while (restantes != 0) {
            double efectivos = localDias - (Math.floor(localDias / 7) + 1);
            restantes = dias - efectivos;
            if (restantes == 0) {
                break;
            }
            if (efectivos > dias) {
                localDias = localDias - restantes;
            } else {
                localDias = localDias + restantes;
            }
        }

        return (int) localDias;
    }

    public boolean tieneGarantia(String codigo) {
        Producto producto = this.repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);
        if (producto == null) {
            return false;
        }

        return true;
    }

}
