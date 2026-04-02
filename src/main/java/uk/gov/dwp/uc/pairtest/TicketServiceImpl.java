package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.config.TicketConstants;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.tic
     */
    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService paymentService,
                             SeatReservationService seatReservationService) {
        this.ticketPaymentService = paymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        int totalAmount = 0;
        int totalSeats = 0;

        for(TicketTypeRequest request: ticketTypeRequests ){
            if(request.getTicketType() == TicketTypeRequest.Type.ADULT){
                totalAmount += request.getNoOfTickets() * TicketConstants.ADULT_TICKET_PRICE;
                totalSeats += request.getNoOfTickets();
            }
            if(request.getTicketType() == TicketTypeRequest.Type.CHILD){
                totalAmount += request.getNoOfTickets() * TicketConstants.CHILD_TICKET_PRICE;
                totalSeats += request.getNoOfTickets();
            }
        }
        System.out.println("Making payment for amount: " + totalAmount);
        ticketPaymentService.makePayment(accountId, totalAmount);

        System.out.println("Reserving seats: " + totalSeats);
        seatReservationService.reserveSeat(accountId, totalSeats);
    }
}