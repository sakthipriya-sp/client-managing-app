package com.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

import com.crud.model.Client;
import com.crud.model.ClientDto;
import com.crud.repository.ClientRepo;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepo clientRepo;

    @GetMapping({"", "/"})
    public String getClients(Model model) {
        var clients = clientRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("clients", clients);
        return "clients/index";
    }

    // Display the new client form
    @GetMapping("/create")
    public String createClient(Model model) {
        ClientDto clientDto = new ClientDto();
        model.addAttribute("clientDto", clientDto);
        return "clients/create"; // Make sure this matches your template's location
    }
    
    @PostMapping("/create")
    public String createClient(
    		@Valid @ModelAttribute ClientDto clientDto,
    		BindingResult result) {
    	if(clientRepo.findByEmail(clientDto.getEmail()) !=null) {
    		result.addError(
    				new FieldError("clientDto" , "email" , clientDto.getEmail(),
    						false,null,null,"Email address is already used"));
    	}
    	
    	
    	if(result.hasErrors()) {
    		return "clients/create";
    	}
    	
    	Client client = new Client();
    	client.setFirstName(clientDto.getFirstName());
    	client.setLastName(clientDto.getLastName());
    	client.setEmail(clientDto.getEmail());
    	client.setPhone(clientDto.getPhone());
    	client.setAddress(clientDto.getAddress());
    	client.setStatus(clientDto.getStatus());
    	client.setCreatedAt( new Date());

    	clientRepo.save(client);
    	return "redirect:/clients";
    }
    
    @GetMapping("/edit")
    public String editClient(Model model, @RequestParam int id) {
    	Client client = clientRepo.findById(id).orElse(null);
    	if(client==null) {
    		return "redirect:/clients";
    	}
    	ClientDto clientDto = new ClientDto();
    	clientDto.setFirstName(client.getFirstName());
    	clientDto.setLastName(client.getLastName());
    	clientDto.setEmail(client.getEmail());
    	clientDto.setPhone(client.getPhone());
    	clientDto.setAddress(client.getAddress());
    	clientDto.setStatus(client.getStatus());
    	
    	model.addAttribute("client" , client);
    	model.addAttribute("clientDto",clientDto);
    	
    	return "clients/edit";
    	
    }
    
    @PostMapping("/edit")
    public String editClient(
    		Model model,
    		@RequestParam int id,
    		@Valid @ModelAttribute ClientDto  clientDto,BindingResult result) {
    	
    	Client client =clientRepo.findById(id).orElse(null);
    	
    	if(client == null) {
    		return "redirect:/clients";
    	}
    	
    	model.addAttribute("client",client);
    	
    	if(result.hasErrors()) {
    		return "clients/edit";
    	}
    	
    	//update
    	
    	client.setFirstName(clientDto.getFirstName());
    	client.setLastName(clientDto.getLastName());
    	client.setEmail(clientDto.getEmail());
    	client.setPhone(clientDto.getPhone());
    	client.setAddress(clientDto.getAddress());
    	client.setStatus(clientDto.getStatus());
    	
    	try {
    		clientRepo.save(client);
    	}
    	catch(Exception ex) {
    		result.addError(
    				new FieldError("clientDto" , "email" , clientDto.getEmail(),
    						false, null,null,"Email address is already used"));
    		return "clients/edit";
    	}
    	
    	
    	   return "redirect:/clients";
    }
    
    @GetMapping("/delete")
    public String deleteClient(@RequestParam int id, Model model, RedirectAttributes redirectAttributes) {
        Client client = clientRepo.findById(id).orElse(null);
        
        if (client != null) {
            clientRepo.delete(client);
            redirectAttributes.addFlashAttribute("successMessage", 
                String.format("Client '%s %s' has been successfully deleted.", 
                              client.getFirstName(), client.getLastName()));
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", 
                String.format("Client with ID %d could not be found. Please try again.", id));
        }
        
        return "redirect:/clients";
    }

}
