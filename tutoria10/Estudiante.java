/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.www.ejemplo0805;

/**
 *
 * @author pavil
 */
class Estudiante {


        private String carne;
        private String nombre;
        private String apellido;
        private String correo;
        private final String carrera;
 
        public Estudiante(String carne, String nombre, String apellido,
                          String correo, String carrera) {
            this.carne    = carne;
            this.nombre   = nombre;
            this.apellido = apellido;
            this.correo   = correo;
            this.carrera  = carrera;
        }
 
        public String getCarne()          { return carne; }
        public String getCorreo()         { return correo; }
        public String getCarrera()        { return carrera; }
 
        // Combina nombre y apellido en un solo metodo — la tabla no necesita
        // saber que son dos atributos distintos.
        public String getNombreCompleto() { return nombre + " " + apellido; }
        
        
    /**
     * @param carne the carne to set
     */
    public void setCarne(String carne) {
        this.carne = carne;
    }

    /**
     * @param correo the correo to set
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }
        
    }