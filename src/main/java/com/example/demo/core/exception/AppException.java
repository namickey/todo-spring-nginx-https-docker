package com.example.demo.core.exception;

/**
 * アプリケーション例外
 * 業務エラーを検知した場合にスローする。
 * 基本的には再処理を促すメッセージを通知する。
 */
public class AppException extends RuntimeException {

    /** メッセージID */
    private String messageId;
    /** フィールド */
    private String field;
    
    /**
     * コンストラクタ
     * @param messageId メッセージID
     * @param field フィールド
     * @param cause 例外
     */
    public AppException(String messageId, String field, Throwable cause) {
        super(cause);
        this.messageId = messageId;
        this.field = field;
    }

    /**
     * コンストラクタ
     * @param messageId メッセージID
     * @param cause 例外
     */
    public AppException(String messageId, Throwable cause) {
        super(cause);
        this.messageId = messageId;
    }

    /**
     * コンストラクタ
     * @param messageId メッセージID
     * @param field フィールド
     */
    public AppException(String messageId, String field) {
        this.messageId = messageId;
        this.field = field;
    }

    /**
     * コンストラクタ
     * @param messageId メッセージID
     */
    public AppException(String messageId) {
        this.messageId = messageId;
    }

    /**
     * メッセージID取得
     * @return
     */
    public String getMessageId() {
        return this.messageId;
    }

    /**
     * フィールド取得
     * @return
     */
    public String getField() {
        return this.field;
    }
}