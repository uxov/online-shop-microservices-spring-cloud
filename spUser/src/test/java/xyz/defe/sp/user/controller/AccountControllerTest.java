package xyz.defe.sp.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.exception.WarnException;
import xyz.defe.sp.user.service.AccountService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @Mock
    private AccountService accountService;
    @InjectMocks
    private AccountController accountController;

    @Test
    void verifyReturnsAccountWhenCredentialsAreValid() {
        String username = "validUser";
        String password = "validPassword";
        Account mockAccount = new Account();
        mockAccount.setUname(username);

        when(accountService.verify(username, password)).thenReturn(mockAccount);

        Account result = accountController.verify(username, password);

        assertNotNull(result);
        assertEquals(username, result.getUname());
        verify(accountService).verify(username, password);
    }

    @Test
    void verifyThrowsExceptionWhenCredentialsAreInvalid() {
        String username = "invalidUser";
        String password = "invalidPassword";

        when(accountService.verify(username, password)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> accountController.verify(username, password));

        assertTrue(exception.getMessage().contains("account verify failed"));
        verify(accountService).verify(username, password);
    }

    @Test
    void verifyThrowsExceptionWhenUsernameIsNull() {
        String password = "validPassword";

        when(accountService.verify(null, password)).thenThrow(new WarnException("uname or pwd is empty"));

        Exception exception = assertThrows(Exception.class, () -> accountController.verify(null, password));

        assertEquals("uname or pwd is empty", exception.getMessage());
        verify(accountService).verify(null, password);
    }

    @Test
    void verifyThrowsExceptionWhenPasswordIsNull() {
        String username = "validUser";

        when(accountService.verify(username, null)).thenThrow(new WarnException("uname or pwd is empty"));

        Exception exception = assertThrows(Exception.class, () -> accountController.verify(username, null));

        assertEquals("uname or pwd is empty", exception.getMessage());
        verify(accountService).verify(username, null);
    }
}