package com.payments;

import com.payments.service.PaymentServiceTest;
import com.payments.service.UserServiceTest;
import com.payments.exception.ExceptionTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        UserServiceTest.class,
        PaymentServiceTest.class,
        ExceptionTest.class
})
public class AllTests {
}
