import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingManagerTest {

    private IPaymentGateway paymentGateway;
    private INotificationService notificationService;
    private IEventRepository eventRepository;
    private BookingManager bookingManager;

    @BeforeEach
    void setUp() {
        paymentGateway = mock(IPaymentGateway.class);
        notificationService = mock(INotificationService.class);
        eventRepository = mock(IEventRepository.class);

        bookingManager = new BookingManager(
                paymentGateway,
                notificationService,
                eventRepository
        );
    }

    @Test
    void happyPath_validBooking_returnsTrue() {
        when(eventRepository.isSoldOut("E1")).thenReturn(false);
        when(paymentGateway.processPayment("U1", 50.0)).thenReturn("TX123");

        boolean result = bookingManager.bookTicket("E1", "U1", 50.0);

        assertTrue(result);
        verify(paymentGateway, times(1)).processPayment("U1", 50.0);
        verify(eventRepository, times(1)).saveBooking("E1", "U1", "TX123");
        verify(notificationService, times(1)).sendConfirmation("U1", "E1");
    }
    @Test
    void invalidInput_paymentBookingNotificationNeverCalled() {
        boolean result = bookingManager.bookTicket("", "U1", 50.0);

        assertFalse(result);

        verify(paymentGateway, never()).processPayment(anyString(), anyDouble());
        verify(eventRepository, never()).saveBooking(anyString(), anyString(), anyString());
        verify(notificationService, never()).sendConfirmation(anyString(), anyString());
    }


    @Test
    void soldOut_eventChecked_paymentBookingNotificationNeverCalled() {
        when(eventRepository.isSoldOut("E1")).thenReturn(true);

        boolean result = bookingManager.bookTicket("E1", "U1", 50.0);

        assertFalse(result);

        verify(eventRepository, times(1)).isSoldOut("E1");
        verify(paymentGateway, never()).processPayment(anyString(), anyDouble());
        verify(eventRepository, never()).saveBooking(anyString(), anyString(), anyString());
        verify(notificationService, never()).sendConfirmation(anyString(), anyString());
    }

}


