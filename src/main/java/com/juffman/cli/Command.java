package com.juffman.cli;

public sealed interface Command
    permits
        Decode,
        Encode,
        Help,
        Version {

    public void execute() throws Exception;
}
