package com.alura.literalura.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;
    private Integer numeroDescargas;
    private String idioma;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    public Libro() {}

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.numeroDescargas = datosLibro.numeroDescargas();
        this.idioma = datosLibro.idiomas().get(0);
        this.autor = new Autor(datosLibro.autores().get(0));
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Integer getNumeroDescargas() { return numeroDescargas; }
    public void setNumeroDescargas(Integer numeroDescargas) { this.numeroDescargas = numeroDescargas; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }

    @Override
    public String toString() {
        return "Título: " + titulo +
                "\nAutor: " + autor.getNombre() +
                "\nAño de nacimiento: " + autor.getAnioNacimiento() +
                "\nAño de fallecimiento: " + autor.getAnioFallecimiento() +
                "\nIdioma: " + idioma +
                "\nNúmero de descargas: " + numeroDescargas;
    }
}