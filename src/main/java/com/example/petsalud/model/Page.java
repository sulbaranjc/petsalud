package com.example.petsalud.model;

import java.util.List;

/**
 * Envuelve el resultado paginado de cualquier consulta.
 * Contiene los elementos de la página actual más los metadatos
 * necesarios para renderizar el control de navegación.
 *
 * @param <T> tipo del elemento que se pagina
 */
public class Page<T> {

    private final List<T> content;
    private final int     pageNumber;    // 1-indexed
    private final int     pageSize;
    private final long    totalElements;

    public Page(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content       = content;
        this.pageNumber    = pageNumber;
        this.pageSize      = pageSize;
        this.totalElements = totalElements;
    }

    public List<T>  getContent()       { return content; }
    public int      getPageNumber()    { return pageNumber; }
    public int      getPageSize()      { return pageSize; }
    public long     getTotalElements() { return totalElements; }

    /** Total de páginas, mínimo 1 aunque no haya resultados. */
    public int getTotalPages() {
        return totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / pageSize);
    }

    public boolean hasPrevious() { return pageNumber > 1; }
    public boolean hasNext()     { return pageNumber < getTotalPages(); }

    /** Número del primer elemento mostrado en esta página (1-indexed). */
    public long getFrom() {
        return totalElements == 0 ? 0 : (long)(pageNumber - 1) * pageSize + 1;
    }

    /** Número del último elemento mostrado en esta página. */
    public long getTo() {
        return Math.min((long) pageNumber * pageSize, totalElements);
    }
}
