package bz.voter.management.utils

public enum FilterType {
    PLEDGE('Pledge'),
    AFFILIATION('Affiliation'),
    PICKUP_TIME('Pickup Time'),
    POLL_STATION('Poll Station')

    final String name

    FilterType(String value){
        this.name = value
    }
}

