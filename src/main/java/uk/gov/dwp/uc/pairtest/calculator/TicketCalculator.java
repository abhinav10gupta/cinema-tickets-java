package uk.gov.dwp.uc.pairtest.calculator;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.pricing.PricingStrategy;
import uk.gov.dwp.uc.pairtest.pricing.PricingStrategyFactory;

public class TicketCalculator {

    private final PricingStrategyFactory pricingStrategyFactory;

    public TicketCalculator(PricingStrategyFactory pricingStrategyFactory) {
        this.pricingStrategyFactory = pricingStrategyFactory;
    }

    public int calculateTotalAmount(TicketTypeRequest... requests){
        int totalAmount = 0;
        for( TicketTypeRequest request : requests){
            PricingStrategy ps = pricingStrategyFactory.getStrategy(request.getTicketType());
            totalAmount += ps.calculatePrice(request.getNoOfTickets());
        }
        return totalAmount;
    }

    public int calculateTotalSeats(TicketTypeRequest... requests) {
        int totalSeats = 0;

        for (TicketTypeRequest request : requests) {
            if (request.getTicketType() == TicketTypeRequest.Type.ADULT
                    || request.getTicketType() == TicketTypeRequest.Type.CHILD) {
                totalSeats += request.getNoOfTickets();
            }
        }
        return totalSeats;
    }
}
