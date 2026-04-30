package com.github.aaronbittel.cli;

public sealed interface Command
    permits
        Decode,
        Encode,
        Help,
        Version {

    void execute() throws Exception;
}
