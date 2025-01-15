package org.taskntech.tech_flow.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taskntech.tech_flow.data.TicketRepository;
import org.taskntech.tech_flow.exceptions.TicketNotFoundException;
import org.taskntech.tech_flow.models.PriorityValue;
import org.taskntech.tech_flow.models.StatusUpdates;
import org.taskntech.tech_flow.models.Ticket;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

        @Autowired // Used to inject dependencies
        private TicketRepository ticketRepository; // Used to access the repository

        // Add setter for testing
        public void setTicketRepository(TicketRepository ticketRepository) {
                this.ticketRepository = ticketRepository;
        }

        // Create a new ticket
        public Ticket createTicket(Ticket ticket) {
                return ticketRepository.save(ticket);
        }

        // Read all tickets
        public List<Ticket> getAllTickets() { // returns a list type of all tickets called "getAllTickets"
                List<Ticket> tickets = new ArrayList<>(); // creating an empty list to store the tickets
                ticketRepository.findAll().forEach(ticket -> { // Iterates over repo/database, returns all tickets and adds them to the list
                        tickets.add(ticket);
                });
                return tickets;
        }

        // Update a ticket
        public Ticket updateTicket(Ticket ticket) {
                if (ticketRepository.existsById(ticket.getTicketId())) { // checks if the ticket exists
                        return ticketRepository.save(ticket); // returns the updated ticket
                }
                return null; // Or throw exception
        }

        // Delete a ticket
        public void deleteTicket(Integer ticketId) {
                ticketRepository.deleteById(ticketId);
        }

        // Update ticket status
        // Using optional<> since it returns null or the ticket
        public Ticket updateTicketStatus(Integer ticketId, StatusUpdates newStatus) {
                // Fetch the ticket by its ticketId
                Optional<Ticket> retrievedTicket = ticketRepository.findById(ticketId);

                // Check if the ticket exists
                if (retrievedTicket.isPresent()) {
                        Ticket ticket = retrievedTicket.get(); // assign the ticket object to the variable ticket

                        // Validate status transition
                        if (!isValidStatusTransition(ticket.getStatus(), newStatus)) {
                                throw new ValidationException(" Opps, invalid status transition from " +
                                        ticket.getStatus() + " to " + newStatus);
                        }

                        // Store the previous status before updating
                        ticket.setPreviousStatus(ticket.getStatus());

                        // Update the status
                        ticket.setStatus(newStatus);
                        ticket.setStatusLastUpdated(LocalDateTime.now());
                        ticket.setLastEdited();

                        return ticketRepository.save(ticket);
                } else {
                        throw new TicketNotFoundException("Ticket not found with ID: " + ticketId);
                }
        }

        // Add or update note
        public Ticket addOrUpdateNote(Integer ticketId, String note) {
                // Fetch the ticket by its ticketId
                Optional<Ticket> retrievedTicket = ticketRepository.findById(ticketId);

                // Check if the ticket exists
                if (retrievedTicket.isPresent()) {
                        Ticket ticket = retrievedTicket.get();
                        // Update the note
                        ticket.setNotes(note);
                        return ticketRepository.save(ticket);
                } else {
                        // throw a customer exception
                        throw new TicketNotFoundException("Ticket not found with ID: " + ticketId);
                }
        }

        // Update ticket priority
        public Ticket updateTicketPriority(Integer ticketId, PriorityValue newPriority) {
                // Fetch the ticket by its ticketId
                Optional<Ticket> retrievedTicket = ticketRepository.findById(ticketId);

                // Check if ticket exists
                if (retrievedTicket.isPresent()) {
                        Ticket ticket = retrievedTicket.get(); // assigning the ticket object to the ticket variable
                        ticket.setPriority(newPriority);
                        return ticketRepository.save(ticket);
                } else {
                        throw new TicketNotFoundException("Ticket not found with ID: " + ticketId);
                }
        }

        // Update client department
        public Ticket updateClientDepartment(Integer ticketId, String newClientDepartment) {
                // Fetch the ticket details
                Optional<Ticket> retrievedTicket = ticketRepository.findById(ticketId);

                // Check if ticket exist
                if (retrievedTicket.isPresent()) {
                        Ticket ticket = retrievedTicket.get(); // if present, we hold it under "ticket"
                        ticket.setClientDepartment(newClientDepartment);
                        return ticketRepository.save(ticket);
                } else {
                        throw new TicketNotFoundException("Ticket not found with ID: " + ticketId);
                }
        }

        // Update ticket details
        public Ticket updateTicketDetails(Integer ticketId, String newTicketDetails) {
                // Fetch the ticket detials
                Optional<Ticket> retrievedTicket = ticketRepository.findById(ticketId);

                // Check if ticket exist
                if (retrievedTicket.isPresent()) {
                        Ticket ticket = retrievedTicket.get();
                        ticket.setDetails(newTicketDetails);
                        return ticketRepository.save(ticket);
                } else {
                        throw new TicketNotFoundException("Ticket not found with ID: " + ticketId);
                }
        }

        public Ticket findTicketById(Integer ticketId) {
                return ticketRepository.findById(ticketId).orElse(null);
        }

        // This method is used when a user wants to change the status of a ticket.
        // It returns a boolean that handles all StatusUpdate ENUMS (not_started, in_progress, delayed, etc.) and checks if they are valid
        public boolean isValidStatusTransition(StatusUpdates currentStatus, StatusUpdates newStatus) {

                // Adding a method to check if the status transition is valid
                // This method takes the current and desired new status as parameters
                switch (currentStatus) { // Evaluates the current ticket status
                        case NOT_STARTED: // If the current status is NOT_STARTED
                                // Only allows transition to IN_PROGRESS
                                return newStatus == StatusUpdates.IN_PROGRESS; // This updates the status to IN_PROGRESS

                        // Only 2 possible transitions from IN_PROGRESS. Either its DELAYED, or RESOLVED
                        case IN_PROGRESS:
                                return newStatus == StatusUpdates.DELAYED ||
                                        newStatus == StatusUpdates.RESOLVED; // OPTIONS: Moves ticket to DELAYED or RESOLVED

                        // Only 2 possible transitions from DELAYED. Either its IN_PROGRESS, or RESOLVED
                        case DELAYED:
                                return newStatus == StatusUpdates.IN_PROGRESS ||
                                        newStatus == StatusUpdates.RESOLVED; // OPTIONS: Moves ticket to IN_PROGRESS or RESOLVED

                        // Added StatusUpdates.RESOLVED because ticket may need to be re-opened by user
                        case RESOLVED:
                                return newStatus == StatusUpdates.CLOSED ||
                                        newStatus == StatusUpdates.IN_PROGRESS; // OPTIONS: Moves ticket to CLOSED or back to IN_PROGRESS

                        // Only 1 possible transition from CLOSED.
                        case CLOSED:
                                return false; // Ticket is closed and cant be re-opened.
                }
                return false; // IntelliJ forced me to add. I assumed for any unexpected cases, return false.

        }
}