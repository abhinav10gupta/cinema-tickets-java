package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.calculator.TicketCalculator;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validation.PurchaseValidator;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.tic
     */
    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;
    private final TicketCalculator ticketCalculator;
    private final PurchaseValidator purchaseValidator;

    public TicketServiceImpl(TicketPaymentService paymentService,
                             SeatReservationService seatReservationService, TicketCalculator ticketCalculator) {
        this.ticketPaymentService = paymentService;
        this.seatReservationService = seatReservationService;
        this.ticketCalculator = ticketCalculator;
        this.purchaseValidator = new PurchaseValidator();
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        purchaseValidator.validate(accountId, ticketTypeRequests);

        int totalAmount = ticketCalculator.calculateTotalAmount(ticketTypeRequests);
        int totalSeats = ticketCalculator.calculateTotalSeats(ticketTypeRequests);

        System.out.println("Making payment for amount: " + totalAmount);
        ticketPaymentService.makePayment(accountId, totalAmount);

        System.out.println("Reserving seats: " + totalSeats);
        seatReservationService.reserveSeat(accountId, totalSeats);
    }

}