package com.example.exchangeratestelegrambot.controller;

import com.example.exchangeratestelegrambot.botapi.ExchangeRatesTelegramBot;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {
    private final ExchangeRatesTelegramBot exchangeRatesTelegramBot;

    public WebHookController(ExchangeRatesTelegramBot exchangeRatesTelegramBot){
        this.exchangeRatesTelegramBot = exchangeRatesTelegramBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return exchangeRatesTelegramBot.onWebhookUpdateReceived(update);
    }
}
