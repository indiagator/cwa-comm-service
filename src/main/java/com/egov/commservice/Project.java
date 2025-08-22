package com.egov.commservice;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Project
{
    String id;
    String ownerPhone; // Phone number of the project owner
    String name;
    String description;
    String location;
    String startDate;
    String status; // PLANNED, IN_PROGRESS, COMPLETED, ON_HOLD
    double budget; // Estimated budget for the project
    List<String> messages; // List of messages or comments related to the quote
}
