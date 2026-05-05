public class BookingManager {

    private IPaymentGateway paymentGateway;
    private INotificationService notificationService;
    private IEventRepository eventRepository;

    public BookingManager(IPaymentGateway paymentGateway,
                          INotificationService notificationService,
                          IEventRepository eventRepository) {
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.eventRepository = eventRepository;
    }

    public boolean bookTicket(String eventId, String userId, double amount) {


            if (eventId == null || eventId.isEmpty()
                    || userId == null || userId.isEmpty()
                    || amount <= 0) {
                return false;
            }

            if (eventRepository.isSoldOut(eventId)) {
                return false;
            }

            String transactionId = paymentGateway.processPayment(userId, amount);

            eventRepository.saveBooking(eventId, userId, transactionId);
            notificationService.sendConfirmation(userId, eventId);

            return true;


        }

}

interface IPaymentGateway {
    String processPayment(String userId, double amount);
}

interface INotificationService {
    void sendConfirmation(String userId, String eventId);
}

interface IEventRepository {
    boolean isSoldOut(String eventId);
    void saveBooking(String eventId, String userId, String transactionId);
}
