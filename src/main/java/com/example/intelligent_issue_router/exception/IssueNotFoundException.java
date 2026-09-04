package com.example.intelligent_issue_router.exception;

public class IssueNotFoundException extends RuntimeException{
    public IssueNotFoundException(Long id) {
        super("Issue with ID " + id + " was not found.");
    }
}
