package uk.gov.dwp.uc.pairtest.pricing;

import uk.gov.dwp.uc.pairtest.config.TicketConstants;

public class AultPricingStrategy implements PricingStrategy{
    @Override
    public int calculatePrice(int quantity) {
        return quantity * TicketConstants.ADULT_TICKET_PRICE;
    }
}
