package com.example.demo.core.handler;


import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.core.exception.SystemException;

import lombok.RequiredArgsConstructor;

/**
 * WEB例外ハンドラー
 */
@ControllerAdvice
@RequiredArgsConstructor
public class WebExceptionHandler {
    
    /** メッセージ */
    private final MessageSource messages;
    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

    /**
     * システム例外の処理
     * ログ出力を行い、システムエラー画面へ遷移する。
     * @param t システム例外
     * @return エラー画面
     */
    @ExceptionHandler(SystemException.class)
    public String handleSystemException(SystemException t) {
        logger.error(messages.getMessage(t.getMessageId(), null, Locale.getDefault()), t);
        return "error.html";
    }

    /**
     * 予期せぬ例外の処理
     * ログ出力を行い、システムエラー画面へ遷移する。
     * @param t 例外
     * @return エラー画面
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception t) {
        logger.error(t.getMessage(), t);
        return "error.html";
    }
    
}