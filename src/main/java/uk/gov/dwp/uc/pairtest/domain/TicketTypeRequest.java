package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public record TicketTypeRequest(Type type, int noOfTickets) {


    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public enum Type {
        ADULT, CHILD , INFANT;
        public static final int MAX_TICKETS = 25;
    }

}
