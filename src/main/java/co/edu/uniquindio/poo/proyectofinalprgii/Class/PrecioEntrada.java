package co.edu.uniquindio.poo.proyectofinalprgii.Class;


public class PrecioEntrada {

        private double precioBase;
        private double impuesto;
        private double comision;

        public PrecioEntrada(double precioBase, double impuesto, double comision) {
            this.precioBase = precioBase;
            this.impuesto = impuesto;
            this.comision = comision;
        }

        public double calcularTotal() {
            return precioBase + impuesto + comision;
        }

        public double getPrecioBase() { return precioBase; }
        public double getImpuesto()   { return impuesto; }
        public double getComision()   { return comision; }
    }

