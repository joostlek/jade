# MicroServices workshop
Workshop voor het ontwikkelen van een microservice voor de cursus integration and communication van de Hogeschool Utrecht.

De documenten voor de workshop zijn te vinden in ```/docs```. Doorloop voor het begin van de workshop de stappen van setup.

## Requirements
* Java SE 8 of 11
* RabbitMQ
* Erlang

## Spring
Om de REST web service te ontwikkelen wordt gebruik gemaakt van twee Spring Boot maven applicaties met de hieronderstaande dependencies.

* Servlet web application with Spring MVC and Tomcat
* JPA persist data in SQL stores with Java Persistence API using Spring Data and Hibernate
* H2 database (with embedded support)
* Actuator to help monitor and manage the application
* RabbitMQ Advanced Message Queuing Protocol via Spring Rabbit

## Architectuur

Zoals je ziet zijn er 2 applicaties in het project:
* Converter
* Frontend

De casus is een Youtube naar mp3 converter. Het idee is dat de front-end een request maakt naar de backend, in die request zit een youtube url. Om binnen de scope van de workshop te blijven gaan we het convert gedeelte vervangen door een ```Thread.sleep(10000)```.

De Converter is nog leeg en die gaan we in de volgende stap invullen.

## Stap 1 - Bekijk de frontend

Start de FrontendApplication.

Ga naar ```localhost:8100/convert```. Je ziet hier dat er nu een link wordt gevraagd, als je die invult wordt je doorgestuurd naar een pagina waar staat dat hij wordt geconverteerd. In feite doet hij nu niks.

Wat je eigenlijk wilt dat er gebeurd is dat er een request gaat naar de converter. Hoe doe je dat? We kunnen hier REST voor gebruiken!

## Stap 2 - Bouw de Converter

Momenteel is de converter nog leeg. De Converter gaat 2 dingen doen:

* Het aannemen van links
* Het persisteren van die links

Het gaat om 1 object draaien: Link

```java
@Entity
public class Link {
    @Id
    @GeneratedValue
    private Long id;
    
    private String link;
    
    public Link() {}
    public Link(String link) {
        this.link = link;
    }
}
```

Maak hier een Repository voor in LinkRepository 

```java
@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
}
```

We maken ook een Service Interface, deze definieert de functie die wordt aangeroepen om de converter te laten werken

```java
public interface ConverterServiceInterface {
    void convert(Link link);
}
```

Deze implementeren we in een Service, hier gaat de magie gebeuren die de link naar de achterkant gooit

```java
@Service
public class ConverterService implements ConverterServiceInterface {
    @Autowired
    private LinkRepository linkRepository;

    @Override
    public void convert(Link link) {
        try {
            Thread.sleep(10000);
        } catch (InterrruptedException e) {
            e.printStackTrace();
        }
        this.linkRepository.save(link);
    }
}
```

Ook moeten we de actie krijgen waarop de converter gaat werken, dit gebeurd in de Controller

```java
@RestController
public class ConverterController {
    @Autowired
    private ConverterServiceInterface converterService;
    
    @PostMapping("/api/convert")
    public void receiveLink(@RequestBody Link link) {
        this.converterService.convert(link);
    }
}
```

De converter is voor nu af.

We hoeven er alleen nog maar voor te zorgen dat de front end webservice de converter kan aanroepen.

## Stap 3 - Koppel de frontend aan de backend via HTTP

De backend heeft nu een REST API.

De frontend kan hier nu gebruik van maken.

In de FrontEndservice voegen we nu in de FrontendService.convertYoutubeLink() een manier toe om met de backend te communiceren.

```java
@Service
public class FrontendService implements FrontendServiceInterface {
    @Override
    public void convertYoutubeLink(String link) {
        RestTemplate restTemplate = new RestTemplate();
        String converterUrl = "http://localhost:8101/api/convert";
        ResponseEntity<String> authenticateResponse = restTemplate.getForEntity(converterUrl, String.class);
        LinkDTO linkDTO = new LinkDTO();
        linkDTO.setLink(link);
        restTemplate.postForEntity(converterUrl, linkDTO, String.class);
    }
}
```

Als je nu allebei de applicatie hebt opgestart, dan maakt de frontend een request naar de backend.

Maak nu alvast even een paar screenshots en knal die alvast in je word document voor de leraar.

## Stap 4 - Uitbreiden?

Stel, jouw service wordt nu ineens heeel druk bezocht. Dan kan je niet veroorloven om 10 seconden per gebruiker te kunnen hendelen. Dan zou je maar 360 klanten per uur serveren. Hoe los je dit op?

* Multi-threading
* Meer servers

Het nadeel aan Multi-threading is dat je ook ergens tegen een limiet aan gaat lopen, dus moet je met meer machines.

Meer servers we go!

Maar hoe dan? 

Message queue!  👉🏻😎👉🏻

## Stap 5 - Message queue aan de front-end

Message queues hebben een Exchange en een routing key. De exchange is het onderwerp, en de routing key is de bestemming van de message.

Deze Config klasse zorgt ervoor dat alle berichten over de converter exchange gaat.

```java
// LET OP imports van de Exchange en TopicExchange uit org.springframework.amqp.core
@Configuration
public class Config {
    @Bean
    public Exchange eventExchange() {
        return new TopicExchange("converter");
    }
}
```

Om berichten te kunnen versturen veranderen we de functie in de frontendservice naar een stuk wat de link in de message queue plaatst.

```java
@Service
public class FrontendService implements FrontendServiceInterface {
    private Logger logger = LoggerFactory.getLogger(FrontendService.class);
    private int count;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private Exchange exchange;
    
    @Override
    public void convertYoutubeLink(String link) {
        logger.info("Start converting {} - {}", count++, link);
        String routingKey = "video.convert";
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, count + link);
    }
}
```

Als je nu in de front-end een link erin zet, dan wordt hij in de queue gezet, nu moeten we nog iets maken zodat de converter hem op kan pakken en converten

## Stap 6 - Message queue aan de back-end

We moeten de backend nog configureren om messages te ontvangen hiervoor maken we een RabbitConfiguration class aan:

```java
@Configuration
public class EventConsumerConfiguration {

    @Bean
    public Exchange eventExchange() {
        return new TopicExchange("converter");
    }

    @Bean
    public Queue queue() {
        return new Queue("convertLinkQueue");
    }

    @Bean
    public Binding binding(Queue queue, Exchange eventExchange) {
        return BindingBuilder
                .bind(queue)
                .to(eventExchange)
                .with("video.*")
                .noargs();
    }

    // Consumer schrijven we zo en hoeft dus niet geïmporteerd te worden
    @Bean
    public Consumer eventReceiver() {
        return new Consumer();
    }

}
```

Deze configuratie definieert de exchange, de naam van de queue, en verbind zich aan een routing key, zodat alle routing keys die beginnen met video. Naar de Consumer gaan alle berichten die in die queue wordt gezet, in die exchange, die beginnen met die key.

De Consumer ziet er als volgt uit:

```java
@Component
public class Consumer {
    
   @Autowired
   private ConverterServiceInterface converterService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RabbitListener(queues = "convertLinkQueue")
    public void receive(String message) {
        logger.info("Start converting {}", message);
        converterService.convert(new Link(message));
        logger.info("Finished converting {}", message);
    }
}
```

Deze luistert nu naar de queue, en als er een bericht komt, zet hij de service aan het werk.

## Stap 7 - Spelen met de workers!

Wat we nu hebben is leuk om mee te spelen, probeer eens de backend uit te zetten en dan een link op te sturen. Daarna zet je je backend weer aan, en als hij eenmaal online is, werkt hij hem rustig af!

Je kan ook meerdere workers draaien (Edit Configurations -> ConverterApplication -> duplicate -> Override parameters -> server.port=8102)
en dan als je heel veel links erin zet, dan zie je dat hij ze om en om afhandeld. Dus daarmee heb je een winst van 100%. Quick maths.
