package ke.unify.api.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import ke.unify.api.service.MessageService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageResource {

    private final MessageService messageService;

    public MessageResource(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/public-key")
    public Map<String, String> getPublicKey(){
        Map<String, String> result = new HashMap<>();
        result.put("publicKey", messageService.getPublicKey());
        return result;
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody Subscription subscription) throws JsonProcessingException {
        messageService.subscribe(subscription);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribe(String endpoint){
        messageService.unsubscribe(endpoint);
    }

}
