package bz.voter.management.utils

public enum FilterType {
    PLEDGE('Pledge'),
    AFFILIATION('Affiliation'),
    PICKUP_TIME('Pickup Time')

    final String name

    FilterType(String value){
        this.name = value
    }
}

