package com.api.rapientrega.models;

import jakarta.persistence.*;

@Entity
@Table(name = "calificaciones")
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calificacion")
    private Integer idCalificacion;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_pedido", nullable = false, unique = true)
    private Pedido pedido;

    @Column(name = "puntuacion_restaurante", nullable = false)
    private Integer puntuacionRestaurante;

    @Column(name = "comentario_restaurante", length = 255)
    private String comentarioRestaurante;

    @Column(name = "puntuacion_repartidor", nullable = false)
    private Integer puntuacionRepartidor;

    @Column(name = "comentario_repartidor", length = 255)
    private String comentarioRepartidor;

    public Calificacion() {
    }

    public Integer getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(Integer idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Integer getPuntuacionRestaurante() {
        return puntuacionRestaurante;
    }

    public void setPuntuacionRestaurante(Integer puntuacionRestaurante) {
        this.puntuacionRestaurante = puntuacionRestaurante;
    }

    public String getComentarioRestaurante() {
        return comentarioRestaurante;
    }

    public void setComentarioRestaurante(String comentarioRestaurante) {
        this.comentarioRestaurante = comentarioRestaurante;
    }

    public Integer getPuntuacionRepartidor() {
        return puntuacionRepartidor;
    }

    public void setPuntuacionRepartidor(Integer puntuacionRepartidor) {
        this.puntuacionRepartidor = puntuacionRepartidor;
    }

    public String getComentarioRepartidor() {
        return comentarioRepartidor;
    }

    public void setComentarioRepartidor(String comentarioRepartidor) {
        this.comentarioRepartidor = comentarioRepartidor;
    }
}
