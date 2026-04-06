package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class PurchaseValidator {

    public void validate(Long accountId, TicketTypeRequest... ticketTypeRequests) {

        validateAccountId(accountId);
        validateRequestPresent(ticketTypeRequests);
        validateChildTicketPurchase(ticketTypeRequests);
        validateInfantTicketGreaterThanAdult(ticketTypeRequests);
        validateMaxTickets(ticketTypeRequests);
    }

    private void validateMaxTickets(TicketTypeRequest[] ticketTypeRequests) {
        TicketCountSummary ticketCountSummary = getTicketCountSummary(ticketTypeRequests);

        int totalTicketCount = ticketCountSummary.adultCount + ticketCountSummary.childCount + ticketCountSummary.infantCount;

        if (totalTicketCount > TicketTypeRequest.Type.MAX_TICKETS) {
            throw new InvalidPurchaseException("Maximum 25 Tickets can be purchased at a time.");
        }
    }

    private void validateInfantTicketGreaterThanAdult(TicketTypeRequest[] ticketTypeRequests) {
        TicketCountSummary ticketCountSummary = getTicketCountSummary(ticketTypeRequests);

        if (ticketCountSummary.infantCount > ticketCountSummary.adultCount) {
            throw new InvalidPurchaseException("Number ofInfant Tickets cannot be greater tha number of Adult Tickets.");
        }
    }

    private void validateAccountId(Long accountId) {
        if(accountId == null || accountId<= 0){
            throw new InvalidPurchaseException("Invalid Account ID");
        }
    }

    private void validateRequestPresent(TicketTypeRequest... ticketTypeRequests) {
        if(ticketTypeRequests == null || ticketTypeRequests.length == 0){
            throw new InvalidPurchaseException("At least one ticket is required in the request.");
        }
    }

    private void validateChildTicketPurchase(TicketTypeRequest[] ticketTypeRequests) {
        TicketCountSummary ticketCountSummary = getTicketCountSummary(ticketTypeRequests);

        if ((ticketCountSummary.childCount > 0 || ticketCountSummary.infantCount > 0)
                && ticketCountSummary.adultCount == 0) {
            throw new InvalidPurchaseException("Child/Infant Ticket cannot be purchased without Adult Ticket.");
        }
    }

    private TicketCountSummary getTicketCountSummary(TicketTypeRequest[] ticketTypeRequests) {
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

        return new TicketCountSummary(adultCount, childCount, infantCount);
    }

    private record TicketCountSummary(int adultCount, int childCount, int infantCount) {
    }
}