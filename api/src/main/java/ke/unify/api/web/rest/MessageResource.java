package ke.unify.api.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ke.unify.api.service.MessageService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MessageResource {

    private final MessageService messageService;

    public MessageResource(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/public-key")
    public String getPublicKey(){
        return messageService.getPublicKey();
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody Subscription subscription) throws JsonProcessingException {
        System.out.println(new ObjectMapper().writeValueAsString(subscription));
        messageService.subscribe(subscription);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribe(String endpoint){
        messageService.unsubscribe(endpoint);
    }

}
