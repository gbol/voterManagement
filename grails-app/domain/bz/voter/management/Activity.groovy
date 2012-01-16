package bz.voter.management

class Activity implements Serializable{

    ActivityType activityType
    Voter voter
    String notes

    static constraints = {
        notes(blank:false)
    }

}
