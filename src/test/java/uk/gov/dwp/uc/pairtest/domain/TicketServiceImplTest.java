package uk.gov.dwp.uc.pairtest.domain;
import org.junit.jupiter.api.BeforeEach;
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

    private TicketPaymentService paymentService;
    private SeatReservationService seatReservationService;
    private TicketService service;

    @BeforeEach
    void setUp(){
        paymentService = mock(TicketPaymentService.class);
        seatReservationService = mock(SeatReservationService.class);
        service = createService();
    }

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
        TicketTypeRequest request = adultRequest(1);

        service.purchaseTickets(1L, request);

        verify(paymentService).makePayment(1L, 25);
        verify(seatReservationService).reserveSeat(1L, 1);
    }

    @Test
    @DisplayName("should make the payment and reserve seat for an adult and child tickets")
    void shouldMakePaymentAndReserveOneSeatForAdultAndChildTicket(){

        TicketTypeRequest adultRequest = adultRequest(2);
        TicketTypeRequest childRequest = childRequest(1);

        service.purchaseTickets(1L, adultRequest, childRequest);

        verify(paymentService).makePayment(1L, 65); // (2 * 25) + (1 * 15)
        verify(seatReservationService).reserveSeat(1L, 3); // 2 adults + 1 child
    }

    @Test
    @DisplayName("should make payment and reserve seats for adult, child and infant tickets")
    void shouldMakePaymentAndReserveSeatsForAdultChildAndInfantTickets() {


        TicketTypeRequest adultRequest = adultRequest(2);
        TicketTypeRequest childRequest = childRequest(1);
        TicketTypeRequest infantRequest = infantRequest(1);

        service.purchaseTickets(1L, adultRequest, childRequest, infantRequest);

        verify(paymentService).makePayment(1L, 65);
        verify(seatReservationService).reserveSeat(1L, 3);
    }

    @Test
    @DisplayName("should make payment and reserve seats for adult and infant tickets")
    void shouldMakePaymentAndReserveSeatsForAdultAndInfantTickets() {
        TicketService service = createService();

        TicketTypeRequest adultRequest = adultRequest(1);
        TicketTypeRequest infantRequest = infantRequest(1);

        service.purchaseTickets(1L, adultRequest, infantRequest);

        verify(paymentService).makePayment(1L, 25);
        verify(seatReservationService).reserveSeat(1L, 1);
    }

    @Test
    @DisplayName("should throw exception when child ticket is purchased without adult ticket")
    void shouldThrowExceptionChildTicketPurchasedWithoutAdultTicket() {
        TicketService service = createService();

        TicketTypeRequest childRequest = childRequest(1);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, childRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should throw exception when infant ticket is purchased without adult ticket")
    void shouldThrowExceptionInfantTicketPurchasedWithoutAdultTicket() {
        TicketService service = createService();

        TicketTypeRequest infantRequest = infantRequest(1);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, infantRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should throw Exception if infant count > Adult")
    void shouldThrowExceptionIfInfantCountIsGreaterThanAdult(){
        TicketService service = createService();
        TicketTypeRequest adultRequest = adultRequest(2);
        TicketTypeRequest infantRequest = infantRequest(5);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, adultRequest, infantRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should accept if Infant count = Adult count")
    void shouldAcceptInfantAndAdultCountIsEqual(){
        TicketService service = createService();
        TicketTypeRequest adultRequest = adultRequest(2);
        TicketTypeRequest infantRequest = infantRequest(2);

        service.purchaseTickets(1L, adultRequest, infantRequest);

        verify(paymentService).makePayment(1L, 50);
        verify(seatReservationService).reserveSeat(1L, 2);
    }


    @Test
    @DisplayName("should throw Exception if Total Ticket count > 25")
    void shouldThrowExceptionIfTotalTicketIsGreaterThanTwentyFive(){
        TicketService service = createService();
        TicketTypeRequest adultRequest = adultRequest(20);
        TicketTypeRequest childRequest = childRequest(6);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, adultRequest, childRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should throw Exception if Total Ticket count > 25 with Adult and Infant")
    void shouldThrowExceptionIfTotalTicketIsGreaterThanTwentyFiveTwo(){
        TicketService service = createService();

        TicketTypeRequest adultRequest = adultRequest(20);
        TicketTypeRequest infantRequest = infantRequest(6);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, adultRequest, infantRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should throw Exception if Total Ticket count > 25 with Adult, Child and Infant")
    void shouldThrowExceptionIfTotalTicketIsGreaterThanTwentyFiveThree(){
        TicketService service = createService();

        TicketTypeRequest adultRequest = adultRequest(20);
        TicketTypeRequest childRequest = childRequest(3);
        TicketTypeRequest infantRequest = infantRequest(3);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, adultRequest, childRequest, infantRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should accept if Max Ticket count = 25")
    void shouldAcceptMaxTicketLessThanEqualTwentyFive(){
        TicketService service = createService();
        TicketTypeRequest adultRequest = adultRequest(20);
        TicketTypeRequest childRequest = childRequest(5);

        service.purchaseTickets(1L, adultRequest, childRequest);

        verify(paymentService).makePayment(1L, 575);
        verify(seatReservationService).reserveSeat(1L, 25);
    }

    @Test
    @DisplayName("should throw Exception if Account ID is Invalid")
    void shouldThrowExceptionAccountIDInvalid(){

        TicketService service = createService();

        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,1);

        assertThrows(InvalidPurchaseException.class, ()-> service.purchaseTickets(0L, adultRequest));

        verifyNoInteractions((paymentService));
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("Should throw Exception if Ticket Request contains NULL")
    void shouldThrowExceptionTicketRequestNULL(){

        TicketService service = createService();

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, (TicketTypeRequest[]) null));

        verifyNoInteractions((paymentService));
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("Should throw Exception if Ticket Request is empty")
    void shouldThrowExceptionTicketRequestIsEmpty(){

        TicketService service = createService();

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L));

        verifyNoInteractions((paymentService));
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should throw exception when ticket quantity is zero")
    void shouldThrowExceptionWhenTicketQuantityIsZero() {
        TicketService service = createService();

        TicketTypeRequest adultRequest = adultRequest(0);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, adultRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    @DisplayName("should throw exception when ticket quantity is negative")
    void shouldThrowExceptionWhenTicketQuantityIsNegative() {
        TicketService service = createService();

        TicketTypeRequest adultRequest = adultRequest(-1);

        assertThrows(InvalidPurchaseException.class, () ->
                service.purchaseTickets(1L, adultRequest)
        );

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    private TicketService createService() {
        return new TicketServiceImpl(
                paymentService,
                seatReservationService,
                new TicketCalculator(new PricingStrategyFactory())
        );
    }

    private TicketTypeRequest adultRequest(int quantity){
        return new TicketTypeRequest(TicketTypeRequest.Type.ADULT, quantity);
    }

    private TicketTypeRequest childRequest(int quantity){
        return new TicketTypeRequest(TicketTypeRequest.Type.CHILD, quantity);
    }

    private TicketTypeRequest infantRequest(int quantity){
        return new TicketTypeRequest(TicketTypeRequest.Type.INFANT, quantity);
    }
}