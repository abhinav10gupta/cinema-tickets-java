package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class PurchaseValidator {

    public void validate(Long accountId, TicketTypeRequest... ticketTypeRequests) {

        validateAccountId(accountId);
        validateRequestPresent(ticketTypeRequests);
        validateChildTicketPurchase(ticketTypeRequests);

    }

    private void validateAccountId(Long accountId) {
        if(accountId == null || accountId<= 0){
            throw new InvalidPurchaseException("Invalid Account ID");
        }
    }

    private void validateRequestPresent(TicketTypeRequest... ticketTypeRequests) {
        if(ticketTypeRequests == null || ticketTypeRequests.length == 0){
            throw new InvalidPurchaseException("Atleast one ticket is required in the request.");
        }
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