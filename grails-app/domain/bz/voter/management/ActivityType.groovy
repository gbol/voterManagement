package bz.voter.management

class ActivityType implements Serializable{

    String name

    static constraints = {
        unique(blank: false, unique: true)
    }


    def beforeValidate(){
        this.name = name?.capitalize()?.trim()
    }
}
