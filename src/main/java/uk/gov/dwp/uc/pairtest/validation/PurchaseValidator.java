package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class PurchaseValidator {

    public void validate(Long accountId, TicketTypeRequest... ticketTypeRequests) {

        validateChildTicketPurchase(ticketTypeRequests);

    }

    private void validateChildTicketPurchase(TicketTypeRequest[] ticketTypeRequests) {
        int adultCount = 0;
        int childCount = 0;
        int infantCount = 0;

        for (TicketTypeRequest request : ticketTypeRequests){
            if(request.getTicketType() == TicketTypeRequest.Type.ADULT){
                adultCount += request.getNoOfTickets();
            }

            if(request.getTicketType() == TicketTypeRequest.Type.CHILD){
                childCount += request.getNoOfTickets();
            }

            if(request.getTicketType() == TicketTypeRequest.Type.INFANT){
                infantCount += request.getNoOfTickets();
            }
        }

        if((childCount > 0 || infantCount > 0) && adultCount == 0){
            throw new InvalidPurchaseException("Child/Infant Ticket cannot be purchased without Adult Ticket.");
        }
    }

}
