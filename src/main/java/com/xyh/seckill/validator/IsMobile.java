package com.xyh.seckill.validator;

/**
 * @author xyh
 * @date 2021/11/2 15:58
 */

import com.xyh.seckill.vo.IsMobileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
* @description: 验证手机号
* @param: null 
* @return: 
* @author xyh
* @date: 2021/11/2 15:59
*/
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {IsMobileValidator.class}
)
public @interface IsMobile {
    boolean required()default true;

    String message() default "{手机号码格式错误}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
