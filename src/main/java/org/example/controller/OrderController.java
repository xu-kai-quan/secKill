package org.example.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import groovy.util.IFileNameFinder;
import org.example.entity.OrderInfo;
import org.example.entity.SecKillUser;
import org.example.redis.GoodsKey;
import org.example.redis.RedisService;
import org.example.result.CodeMsg;
import org.example.result.Result;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.service.UsersService;
import org.example.vo.GoodsDetailVo;
import org.example.vo.GoodsVo;
import org.example.vo.OrderDetailVo;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {


    OrderService orderService;
    GoodsService goodsService;

    @Inject
    public OrderController(OrderService orderService, GoodsService goodsService) {
        this.orderService = orderService;
        this.goodsService = goodsService;
    }

    @RequestMapping(value = "/detail")
    @ResponseBody
    public Result<OrderDetailVo> detail(SecKillUser user, @RequestParam("orderId") long orderId) {
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoods(goods);
        return Result.success(orderDetailVo);
    }

    @RequestMapping(value = "/pay")
    @ResponseBody
    public void pay(HttpServletResponse response, String orderId) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        AlipayClient alipayClient = new DefaultAlipayClient(
                "https://openapi.alipaydev.com/gateway.do",
                "2021000117648572",
                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCCbHRZ0M3pyzx0NG+VSM5XCKIcIFSk5rbb+fNQW3o2dka+K/JBiiGOhQNbepJXjN0U/VfCVLv7ouL8zbEWVb5L/WELxKgA43xnB/r72CRZJBu3LHwAeaTjoX8NoTq/rYujqT0yXThufUvocExWo9RXdTDItmBUfiIb4pDXhW3EvBjrKol61icT2wMJssQ1gXRI6FTkacgU1ciQXuLcoZ/iOovRN1pygOaegI+wuPP7ZwurNiyEPQnKNQJrtejqeERoVeo8EVbS0fEkMyezEuweDlZFFuuVlJGfjqFkrbayLnY6uZdzLt9F/BHLeLHYcsJ/QBMRsjrh9cWMGtUZS/BrAgMBAAECggEAC8wTmiww8/zWMwyrKy2yvFyjjTe/cEsOL0ekopgjrdl0/K6s8xCziFivvQ6RPioXtVZVIdbYSiKptYRFiGepFuaMaKwZJjttfURTh4Q9hXKmryLu9HwMViq9e1YiIkRoZR1h07Rq7lOXEidWQGABTQfkqcMVY48lNrB/uX7wNuNK6o5x6j/H3L19J7z0A7yJeLV4jKceP7UXivPrlcgkyqeAzobHkN50eIfox/iQP9UHfE4u9oGnaVEq6k4AYkQweKcAaqaMqUTgL5NHfonZlD0d7VCcaKHW4ss7sgISkAoZWvEeX+w2mQNYbrFHPQ+6JX9Q+E5Y3vT3ct3S1SUFAQKBgQDimaB8ahDcHr7R60vP4pyurtMKfF3tiIUH8nePmA+h8JylfkEPSn5g1Sus4nx7DXxx9k21uwghGXN5p4XuhHHo6Jnc9xDgOSX7iGyEU+4OuoXoYeD4xsnf2ePOHLw7RFAClovbe5Bi9nnryX8y2M+K8mD7cki/2IYodEizBhM/UQKBgQCTWGPa8OnSiRbthkzho13PZOzQtMHLvkQAh0xErymL0JxeSlfNElSEGlR726be1B98+WngpUAN37dVI9h0r8KGeYnpRLNzf7HFDJkTOncVcF85RQthTrHXOrF0ZLbmF0xA+9LQ/xuc58ffcSUdpSqt0vDHJHNy3J36CLzQEYAc+wKBgEeUO8vRPW4rECADcblDXjcX5nZjftAv1cYC452qbRW8id8FGMwZvf4cmGVOTgAY7HybT0Texdyey4lcT8RedhUalK/mI6CQrNzd5VmPoZ1pci9L6+Lp+I8LpxhOn28hTxziLV+xkpSQQ0cmKVYIgtbjZZtIKI7heM+CnAxhGOdhAoGAMKAu9AYlZDGRu3lizeQYBWWNMaM1gYOc9X+xD8musFF8CTIHn8m40o/N6cInY8olSh0Vb+mZJfr6icJ2oKuQSsi5Vp4xiqBNjnYF3m6g9hZLlp7OjqJl1l2sSnkiUvTFWXzBqe9GsZVcRMUWNp5XY14Fz/KblwBvsiBID3J3ERkCgYAEZnXV9Wo3kKkSrnF2638eBt2Yk/JFkzbtAtBoo8AFCTznojZ52KMmkf+ExMXPACT6b/ef6BpDf7aneI8B7ISdyF22gOYTqC1JL3LPmicOQGGrvqN8kp+iknCdNBcDI3eA+N6tFoLQutvYo32nU3emc+mZ4TnJHaSqQ4hWkNq/UQ==",
                "json",
                "utf-8",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgmx0WdDN6cs8dDRvlUjOVwiiHCBUpOa22/nzUFt6NnZGvivyQYohjoUDW3qSV4zdFP1XwlS7+6Li/M2xFlW+S/1hC8SoAON8Zwf6+9gkWSQbtyx8AHmk46F/DaE6v62Lo6k9Ml04bn1L6HBMVqPUV3UwyLZgVH4iG+KQ14VtxLwY6yqJetYnE9sDCbLENYF0SOhU5GnIFNXIkF7i3KGf4jqL0TdacoDmnoCPsLjz+2cLqzYshD0JyjUCa7Xo6nhEaFXqPBFW0tHxJDMnsxLsHg5WRRbrlZSRn46hZK22si52OrmXcy7fRfwRy3ix2HLCf0ATEbI64fXFjBrVGUvwawIDAQAB",
                "RSA2");

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl("http://localhost:8080/order/payResult?orderId=" + orderId);
//        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        OrderInfo order = orderService.getOrderById(Long.parseLong(orderId));
        String orderPrice = String.valueOf(order.getGoodsPrice());
        String randomString = UUID.randomUUID().toString();
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = randomString + "test" + orderId;
        //付款金额，必填
        String total_amount = orderPrice;
        //订单名称，必填
        String subject = "test";
        //商品描述，可空
        String body = "";

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String result = null;
        try {
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        String html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "<title>付款</title>\n" +
                "</head>\n" +
                result +
                "<body>\n" +
                "</body>\n" +
                "</html>";

        //输出
        response.getWriter().println(html);
    }

    @RequestMapping(value = "/payResult")
    @ResponseBody
    public void payResult(HttpServletResponse response,HttpServletRequest request,@RequestParam("orderId") String orderId) throws ServletException, IOException {

        OrderInfo order = orderService.getOrderById(Long.parseLong(orderId));
        order.setStatus(1);
        orderService.paySuccess(order);
        request.getRequestDispatcher("/order_detail.htm?orderId="+orderId).forward(request,response);
    }

}
