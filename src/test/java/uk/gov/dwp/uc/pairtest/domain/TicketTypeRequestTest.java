package uk.gov.dwp.uc.pairtest.domain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TicketTypeRequest")
public class TicketTypeRequestTest {

    @Test
    @DisplayName("should create request with valid type and quantity")
    void shouldCreateRequestWithValidTypeAndQuantity() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        assertEquals(TicketTypeRequest.Type.ADULT, request.getTicketType());
        assertEquals(2, request.getNoOfTickets());
    }

    @Test
    @DisplayName("should handle adult ticket types")
    void shouldHandleAdultTicketTypes() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        assertEquals(TicketTypeRequest.Type.ADULT, request.getTicketType());
    }
}