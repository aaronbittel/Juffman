package com.github.aaronbittel.cli;

public record Version() implements Command {

    @Override
    public void execute() {
        System.out.println("v1.0-SNAPSHOT");
    }
}

