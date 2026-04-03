package uk.gov.dwp.uc.pairtest.pricing;

import uk.gov.dwp.uc.pairtest.config.TicketConstants;

public class ChildPricingStrategy implements PricingStrategy{
    @Override
    public int calculatePrice(int quantity) {
        return quantity * TicketConstants.CHILD_TICKET_PRICE;
    }
}
