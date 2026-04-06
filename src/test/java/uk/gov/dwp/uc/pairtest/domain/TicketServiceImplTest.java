package uk.gov.dwp.uc.pairtest.domain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.calculator.TicketCalculator;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.pricing.PricingStrategyFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("should make payment and reserve seats for adult and infant tickets")
    void shouldMakePaymentAndReserveSeatsForAdultAndInfantTickets() {
        TicketPaymentService paymentService = mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = mock(SeatReservationService.class);

        TicketService service = createService(paymentService, seatReservationService);

        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        service.purchaseTickets(1L, adultRequest, infantRequest);

        verify(paymentService).makePayment(1L, 25);
        verify(seatReservationService).reserveSeat(1L, 1);
    }

    @Test
    @DisplayName("should throw exception when child ticket is purchased without adult ticket")
    void shouldThrowExceptionChildTicketPurchasedWithoutAdultTicket() {
        TicketPaymentService paymentService = mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = mock(SeatReservationService.class);

        TicketService service = new TicketServiceImpl(
                paymentService,
                seatReservationService,
                new TicketCalculator(new PricingStrategyFactory())
        );

        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, childRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should throw exception when infant ticket is purchased without adult ticket")
    void shouldThrowExceptionInfantTicketPurchasedWithoutAdultTicket() {
        TicketPaymentService paymentService = mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = mock(SeatReservationService.class);

        TicketService service = new TicketServiceImpl(
                paymentService,
                seatReservationService,
                new TicketCalculator(new PricingStrategyFactory())
        );

        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, infantRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should throw Exception if infant count > Adult")
    void shouldThrowExceptionIfInfantCountIsGreaterThanAdult(){
        TicketPaymentService paymentService = mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = mock(SeatReservationService.class);

        TicketService service = new TicketServiceImpl(
                paymentService,
                seatReservationService,
                new TicketCalculator(new PricingStrategyFactory())
        );
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, adultRequest, infantRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should throw Exception if Total Ticket count > 25")
    void shouldThrowExceptionIfTotalTicketIsGreaterThanTwentyFive(){
        TicketPaymentService paymentService = mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = mock(SeatReservationService.class);

        TicketService service = new TicketServiceImpl(
                paymentService,
                seatReservationService,
                new TicketCalculator(new PricingStrategyFactory())
        );
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, adultRequest, childRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should throw Exception if Account ID is Invalid")
    void shouldThrowExceptionAccountIDInvalid(){

        TicketPaymentService paymentService = mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = mock(SeatReservationService.class);

        TicketService service = new TicketServiceImpl(
                paymentService,
                seatReservationService,
                new TicketCalculator(new PricingStrategyFactory())
        );

        TicketTypeRequest adultReqest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,1);

        assertThrows(InvalidPurchaseException.class, ()-> service.purchaseTickets(0L, adultReqest));

        verifyNoInteractions((paymentService));
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("Should throw Exception if Ticket Request contains NULL")
    void shouldThrowExceptionTicketRequestNULL(){

        TicketPaymentService paymentService = mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = mock(SeatReservationService.class);

        TicketService service = new TicketServiceImpl(
                paymentService,
                seatReservationService,
                new TicketCalculator(new PricingStrategyFactory())
        );

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, (TicketTypeRequest[]) null));

        verifyNoInteractions((paymentService));
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("Should throw Exception if Ticket Request is empty")
    void shouldThrowExceptionTicketRequestIsEmpty(){

        TicketPaymentService paymentService = mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = mock(SeatReservationService.class);

        TicketService service = new TicketServiceImpl(
                paymentService,
                seatReservationService,
                new TicketCalculator(new PricingStrategyFactory())
        );

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L));

        verifyNoInteractions((paymentService));
        verifyNoInteractions(seatReservationService);
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