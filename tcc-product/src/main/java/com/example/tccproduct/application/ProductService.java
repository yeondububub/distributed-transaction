package com.example.tccproduct.application;

import com.example.tccproduct.application.dto.ProductReserveCommand;
import com.example.tccproduct.application.dto.ProductReserveResult;
import com.example.tccproduct.domain.Product;
import com.example.tccproduct.domain.ProductReservation;
import com.example.tccproduct.infrastructure.ProductRepository;
import com.example.tccproduct.infrastructure.ProductReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductReservationRepository productReservationRepository;

    public ProductReserveResult tryReserve(ProductReserveCommand command) {
        List<ProductReservation> reservations = productReservationRepository.findAllByRequestId(command.requestId());
        if (!reservations.isEmpty()) {
            Long totalPrice = reservations.stream()
                    .mapToLong(ProductReservation::getReservedPrice).sum();
            return new ProductReserveResult(totalPrice);
        }

        Long totalPrice = 0L;
        for (ProductReserveCommand.ReserveItem item : command.items()) {
            Product product = productRepository.findById(item.productId()).orElseThrow();
            Long price = product.reserve(item.reserveQuantity());
            totalPrice += price;
            productRepository.save(product);
            productReservationRepository.save(
                    new ProductReservation(
                            command.requestId(),
                            item.productId(),
                            item.reserveQuantity(),
                            price
                    )
            );
        }

        return new ProductReserveResult(totalPrice);
    }
}
