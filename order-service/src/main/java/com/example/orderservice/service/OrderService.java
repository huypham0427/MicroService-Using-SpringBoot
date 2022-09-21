package com.example.orderservice.service;


import com.example.orderservice.common.Payment;
import com.example.orderservice.common.TransactionRequest;
import com.example.orderservice.common.TransactionResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private RestTemplate template;

    public TransactionResponse saveOrder(TransactionRequest request){

        String response = "";

        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());

        /** Rest call */
        Payment paymentResponse =  template.postForObject("http://PAYMENT-SERVICE/payment/doPayment", payment, Payment.class);


        response = paymentResponse.getPaymentStatus().equals("success") ? "Payment processing successful and order placed" : "Something wrong";



        repository.save(order);
        return new TransactionResponse(order,
                paymentResponse.getAmount(),
                paymentResponse.getTransactionId(),
                response);
    }
}
