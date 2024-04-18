package com.example.chatgptjokes.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelDto {
    private int budget;

    private int numberOfPeople;

    private String destination;

    private String startingLocation;

   private String month;

   private int duration;


    public TravelDto(int budget, int numberOfPeople, String destination, String startingLocation, String month, int duration) {
        this.budget = budget;
        this.numberOfPeople = numberOfPeople;
        this.destination = destination;
        this.startingLocation = startingLocation;
        this.month = month;
        this.duration = duration;
    }
}