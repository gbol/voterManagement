package bz.voter.management

class Activity implements Serializable{

    ActivityType activityType
    Voter voter
    String notes
    Date activityDate


    static constraints = {
        notes(blank:false)
    }


}
