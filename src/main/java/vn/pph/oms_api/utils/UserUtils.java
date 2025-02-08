package vn.pph.oms_api.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.Cart;
import vn.pph.oms_api.model.User;
import vn.pph.oms_api.repository.CartRepository;
import vn.pph.oms_api.repository.ProductRepository;
import vn.pph.oms_api.repository.UserRepository;
import vn.pph.oms_api.service.CartService;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserUtils {
    UserRepository userRepository;
    CartRepository cartRepository;

    public Cart checkCartOfUser(Long userId) {
        log.info("Checking cart for userId {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User {} not found", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Cart not found for userId {}", userId);
                    return new AppException(ErrorCode.CART_NOT_FOUND);
                });
    }

    public User checkUserExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

}
