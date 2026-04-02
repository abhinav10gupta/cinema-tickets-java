package uk.gov.dwp.uc.pairtest.domain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("TicketTypeRequest")
public class TicketTypeRequestTest {

    @Test
    @DisplayName("should create request with valid type and quantity")
    void shouldCreateRequestWithValidTypeAndQuantity() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        assertEquals(TicketTypeRequest.Type.ADULT, request.getTicketType());
        assertEquals(2, request.getNoOfTickets());
    }

    @Test
    @DisplayName("should make payment and reserve one seat for one adult ticket")
    void shouldMakePaymentAndReserveOneSeatForOneAdultTicket() {
        TicketPaymentService paymentService = mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = mock(SeatReservationService.class);

        TicketService service = new TicketServiceImpl(paymentService, seatReservationService);
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        service.purchaseTickets(1L, request);

        verify(paymentService).makePayment(1L, 25);
        verify(seatReservationService).reserveSeat(1L, 1);
    }
}