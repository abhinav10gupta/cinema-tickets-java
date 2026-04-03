package uk.gov.dwp.uc.pairtest.pricing;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Map;

public class PricingStrategyFactory {

    private final Map<TicketTypeRequest.Type, PricingStrategy> strategyMap;

    public PricingStrategyFactory() {
        this.strategyMap = Map.of(
                TicketTypeRequest.Type.ADULT, new AultPricingStrategy(),
                TicketTypeRequest.Type.CHILD, new ChildPricingStrategy(),
                TicketTypeRequest.Type.INFANT, new InfantPricingStrategy()
        );
    }

    public PricingStrategy getStrategy(TicketTypeRequest.Type type) {
        return strategyMap.get(type);
    }
}
