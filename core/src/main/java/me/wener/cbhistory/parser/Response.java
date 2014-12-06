package me.wener.cbhistory.parser;

public interface Response
{
    boolean isSuccess();

    String getState();

    String getMessage();

    String getContent();
}
