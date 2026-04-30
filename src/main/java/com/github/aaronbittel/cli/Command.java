package com.github.aaronbittel.cli;

public sealed interface Command
    permits
        Decode,
        Encode,
        Help,
        Version {

    public abstract void execute() throws Exception;
}
