package vn.pph.oms_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.pph.oms_api.dto.request.discount.DiscountCreationRequest;

public class DateRangeValidator implements ConstraintValidator <ValidDateRange, DiscountCreationRequest>{
    @Override
    public boolean isValid(DiscountCreationRequest discount, ConstraintValidatorContext context) {
        if (discount.getStartDate() == null || discount.getEndDate() == null) {
            return true; // Skip validation if values are null. Use @NotNull for mandatory validation.
        }
        return discount.getStartDate().isBefore(discount.getEndDate());
    }
}
