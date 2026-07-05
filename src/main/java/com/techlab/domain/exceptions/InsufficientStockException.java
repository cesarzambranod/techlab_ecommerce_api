package com.techlab.domain.exceptions;

/**
 * Excepción lanzada cuando no hay stock suficiente para completar un pedido.
 */
public class InsufficientStockException extends RuntimeException {

    private final Long productId;
    private final Integer availableStock;
    private final Integer requestedQuantity;

    public InsufficientStockException(Long productId, Integer availableStock, Integer requestedQuantity) {
        super(String.format("Stock insuficiente para el producto ID %d. Disponible: %d, Solicitado: %d",
                productId, availableStock, requestedQuantity));
        this.productId = productId;
        this.availableStock = availableStock;
        this.requestedQuantity = requestedQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }
}
