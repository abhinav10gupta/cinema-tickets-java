package uk.gov.dwp.uc.pairtest.domain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.calculator.TicketCalculator;
import uk.gov.dwp.uc.pairtest.pricing.PricingStrategyFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("TicketTypeRequest")
public class TicketServiceImplTest {

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

        TicketService service = createService(paymentService, seatReservationService);
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        service.purchaseTickets(1L, request);

        verify(paymentService).makePayment(1L, 25);
        verify(seatReservationService).reserveSeat(1L, 1);
    }

    @Test
    @DisplayName("should make the payment and reserve seta for an adult and child tickets")
    void shouldMakePaymentAndReserveOneSeatForAdultAndChildTicket(){
        TicketPaymentService paymentService = mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = mock(SeatReservationService.class);

        TicketService service = createService(paymentService, seatReservationService);
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        service.purchaseTickets(1L, adultRequest, childRequest);

        verify(paymentService).makePayment(1L, 65); // (2 * 25) + (1 * 15)
        verify(seatReservationService).reserveSeat(1L, 3); // 2 adults + 1 child
    }

    private TicketService createService(TicketPaymentService paymentService,
                                        SeatReservationService seatReservationService) {
        return new TicketServiceImpl(
                paymentService,
                seatReservationService,
                new TicketCalculator(new PricingStrategyFactory())
        );
    }
}