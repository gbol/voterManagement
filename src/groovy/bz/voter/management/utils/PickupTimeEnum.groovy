package bz.voter.management.utils

public enum PickupTimeEnum{
    SIX('6-7'),
    SEVEN('7-8'),
    EIGHT('8-9'),
    NINE('9-10'),
    TEN('10-11'),
    ELEVEN('11-12'),
    TWELVE('12-1'),
    ONE('1-2'),
    TWO('2-3'),
    THREE('3-4'),
    FOUR('4-5'),
    FIVE('5-6')


    final String name

    PickupTimeEnum(String value){
        this.name = value
    }

    public String value(){
        name
    }

}


