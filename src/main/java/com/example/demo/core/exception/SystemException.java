package com.example.demo.core.exception;

/**
 * システム例外
 * 想定外のシステムエラーを検知した場合にスローする。
 * 基本的に業務処理の継続は不可能で、システムエラー画面へ遷移する。
 */
public class SystemException extends RuntimeException {
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
    public SystemException(String messageId, String field, Throwable cause) {
        super(cause);
        this.messageId = messageId;
        this.field = field;
    }

    /**
     * コンストラクタ
     * @param messageId メッセージID
     * @param cause 例外
     */
    public SystemException(String messageId, Throwable cause) {
        super(cause);
        this.messageId = messageId;
    }

    /**
     * コンストラクタ
     * @param messageId メッセージID
     * @param field フィールド
     */
    public SystemException(String messageId, String field) {
        this.messageId = messageId;
        this.field = field;
    }

    /**
     * コンストラクタ
     * @param messageId メッセージID
     */
    public SystemException(String messageId) {
        this.messageId = messageId;
    }

    /**
     * メッセージID取得
     * @return メッセージID
     */
    public String getMessageId() {
        return this.messageId;
    }

    /**
     * フィールド取得
     * @return フィールド
     */
    public String getField() {
        return this.field;
    }
}
