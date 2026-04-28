package com.juffman.cli;

public sealed interface Command
    permits
        Decode,
        Encode,
        Help,
        Version {

    void execute() throws Exception;
}
