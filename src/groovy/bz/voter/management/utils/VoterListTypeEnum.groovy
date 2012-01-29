package bz.voter.management.utils

/**
This Enum is used to determine what 'type' of voter list is being displayed. 
This helps us know what type of list to print. 
**/
public enum VoterListTypeEnum{
    ALL('*'),
    NAME('Name'),
    AFFILIATION('Affiliation'),
    PICKUP_TIME('Pickup Time'),
    PLEDGE('Pledge')

    final String name

    VoterListTypeEnum(String value){
        this.name = value
    }


    public String value(){
        this.name
    }

}

