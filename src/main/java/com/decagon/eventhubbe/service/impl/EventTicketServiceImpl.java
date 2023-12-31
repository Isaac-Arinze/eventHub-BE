package com.decagon.eventhubbe.service.impl;

import com.decagon.eventhubbe.domain.entities.AppUser;
import com.decagon.eventhubbe.domain.entities.Event;
import com.decagon.eventhubbe.domain.entities.Payment;
import com.decagon.eventhubbe.domain.repository.EventRepository;
import com.decagon.eventhubbe.domain.repository.PaymentRepository;
import com.decagon.eventhubbe.dto.response.EventTicketResponse;
import com.decagon.eventhubbe.dto.response.TicketsSalesResponse;
import com.decagon.eventhubbe.exception.EventNotFoundException;
import com.decagon.eventhubbe.exception.UnauthorizedException;
import com.decagon.eventhubbe.service.EventTicketService;
import com.decagon.eventhubbe.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventTicketServiceImpl implements EventTicketService {
    private final AppUserServiceImpl appUserService;
    private final EventRepository eventRepository;
    private final PaymentRepository paymentRepository;


    @Override
    public List<TicketsSalesResponse> trackTicketSales(String eventId){
        String email = UserUtils.getUserEmailFromContext();
        AppUser appUser = appUserService.getUserByEmail(email);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(()-> new EventNotFoundException(eventId));
        if(!event.getAppUser().getId().equals(appUser.getId())){
            throw new UnauthorizedException("Event Created By Another User");
        }
        List<TicketsSalesResponse> ticketsSalesResponses = new ArrayList<>();
        Map<String, TicketsSalesResponse> responseMap = new HashMap<>();

        List<Payment> payments = paymentRepository.findAllByEvent(event);

        for (Payment payment : payments) {
            String eventTicketId = payment.getEventTicket().getId();

            TicketsSalesResponse ticketsSalesResponse = responseMap.get(eventTicketId);
            if (ticketsSalesResponse == null) {
                ticketsSalesResponse = new TicketsSalesResponse();
                ticketsSalesResponse.setEventTicketResponse(EventTicketResponse.builder()
                        .ticketClass(payment.getEventTicket().getTicketClass())
                        .ticketPrice(payment.getEventTicket().getTicketPrice())
                        .id(payment.getEventTicket().getId())
                        .description(payment.getEventTicket().getDescription())
                        .quantity(payment.getEventTicket().getQuantity())
                        .build());
                responseMap.put(eventTicketId, ticketsSalesResponse);
            }
            if(ticketsSalesResponse.getAmount() == null && ticketsSalesResponse.getQuantitySold() == null){
                ticketsSalesResponse.setAmount(payment.getAmount());
                ticketsSalesResponse.setQuantitySold(payment.getQty());
            }else{
                assert ticketsSalesResponse.getAmount() != null;
                ticketsSalesResponse.setAmount(ticketsSalesResponse.getAmount().add(payment.getAmount()));
                ticketsSalesResponse.setQuantitySold(ticketsSalesResponse.getQuantitySold() + payment.getQty());
            }
        }
        for (String eventTicketId : responseMap.keySet()) {
            TicketsSalesResponse ticketsSalesResponse = responseMap.get(eventTicketId);
          ticketsSalesResponses.add(ticketsSalesResponse);
        }
        return ticketsSalesResponses;
    }
}
