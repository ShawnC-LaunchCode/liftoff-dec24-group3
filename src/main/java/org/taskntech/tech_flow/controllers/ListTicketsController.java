package org.taskntech.tech_flow.controllers;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.taskntech.tech_flow.exceptions.TicketNotFoundException;
import org.taskntech.tech_flow.models.PriorityValue;
import org.taskntech.tech_flow.models.StatusUpdates;
import org.taskntech.tech_flow.models.Ticket;
import org.taskntech.tech_flow.service.TicketService;

import java.util.ArrayList;
import java.util.List;

/*
 * Model is a interface that is used to add data to the model.
 * @PathVariable = is used to bind the URL path variable to a method parameter.
 * @Valid = is used to validate the object before saving it to the database.
 * @ModelAttribute = is used to bind the form data to a model object.
 *
 * */

@Controller
@RequestMapping("/tickets")
public class ListTicketsController {

        @Autowired
        private TicketService ticketService;

        // List all tickets
        @GetMapping
        public String listTickets(Model model) {
                List<Ticket> tickets = ticketService.getAllTickets();
                if (tickets == null) {
                        tickets = new ArrayList<>(); // Handle null by initializing an empty list
                }
                model.addAttribute("tickets", tickets);
                return "tickets/list";
        }


        // Display the form to create a new ticket
        @GetMapping("/create")
        public String displayCreateTicketForm(Model model) {
                // Creates an empyt ticket object  and adds it to the model
                model.addAttribute("ticket", new Ticket()); // remember, use of new keyword creates new object
                // Gets all the values from PriorityValues (High, MEDIUM, LOW)
                model.addAttribute("priorityValues", PriorityValue.values());
                // Gets all enum values from StatusUpdates (NOT STARTED, IN PROGRESS, etc)
                model.addAttribute("statusValues", StatusUpdates.values());
                // Returns path to Thymeleaf that will display the form
                return "tickets/create";
        }

        // Display edit form for existing ticket
        @GetMapping("/edit/{ticketId}")
        public String showEditForm(@PathVariable Integer ticketId, Model model) {
                // Using optional for cases of if the ticket does not exist
                Ticket ticket = ticketService.findTicketById(ticketId);

                // Try to find the ticket and store it
                if (ticket == null) {
                        throw new TicketNotFoundException("Ticket not found with ID: " + ticketId);
                }
                model.addAttribute("ticket", ticket); // Adds the existing ticket
                model.addAttribute("priorityValues", PriorityValue.values()); // Adds the priority values
                model.addAttribute("statusValues", StatusUpdates.values()); // Adds the status values
                return "tickets/edit"; // Returns the edit form
        }

        // Process the ticket creation form
        @PostMapping("/create")
        public String processCreateTicketForm(
                @Valid @ModelAttribute("ticket") Ticket ticket,
                BindingResult bindingResult,
                Model model) {
                if (bindingResult.hasErrors()) {
                        model.addAttribute("priorityValues", PriorityValue.values());
                        model.addAttribute("statusValues", StatusUpdates.values());
                        model.addAttribute("errorMessage", "Please fix the errors in the form");
                        return "tickets/create";
                }

                try {
                        ticketService.createTicket(ticket);
                        return "redirect:/tickets";
                } catch (ValidationException e) {
                        model.addAttribute("priorityValues", PriorityValue.values());
                        model.addAttribute("statusValues", StatusUpdates.values());
                        model.addAttribute("errorMessage", "Error creating ticket: " + e.getMessage());
                        return "tickets/create";
                }
        }

        // Process the edit form submission
        @PostMapping("/edit/{ticketId}")
        public String processEditForm(@PathVariable Integer ticketId,
                                      @Valid @ModelAttribute("ticket") Ticket ticket,
                                      BindingResult bindingResult,
                                      Model model) {
                if (bindingResult.hasErrors()) {
                        model.addAttribute("priorityValues", PriorityValue.values());
                        model.addAttribute("statusValues", StatusUpdates.values());
                        model.addAttribute("errorMessage", "Please fix the errors in the form");
                        return "tickets/edit";
                }

                try {
                        ticket.setTicketId(ticketId);
                        ticket.setLastEdited();

                        // Get current ticket first
                        Ticket currentTicket = ticketService.findTicketById(ticketId);

                        ticketService.updateTicket(ticket);
                        return "redirect:/tickets";
                } catch (ValidationException ve) {
                        model.addAttribute("errorMessage", "Invalid status transition: " + ve.getMessage());
                        model.addAttribute("priorityValues", PriorityValue.values());
                        model.addAttribute("statusValues", StatusUpdates.values());
                        return "tickets/edit";
                } catch (Exception e) {
                        model.addAttribute("errorMessage", "Error updating ticket: " + e.getMessage());
                        model.addAttribute("priorityValues", PriorityValue.values());
                        model.addAttribute("statusValues", StatusUpdates.values());
                        return "tickets/edit";
                }

}
                // Setter for unit testing
                public void setTicketService (TicketService ticketService){
                        this.ticketService = ticketService;
                }
}
