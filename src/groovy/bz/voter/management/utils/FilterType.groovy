package bz.voter.management.utils

public enum FilterType {
    PLEDGE('Pledge'),
    AFFILIATION('Affiliation')

    final String name

    FilterType(String value){
        this.name = value
    }
}

/**
    private static def list 

    public String value(){
        name
    }

    public static FilterType fromValue(String v){
        valueOf(v)
    }



    public static List getList(){
        if(list){
            return list
        }else{
            return buildList()
        }
    }
    
    private static synchronized List buildList(){
        list = []
        for(filterType in FilterType.values()){
            list.add(filterType.name())
        }

        return list

    }
}
**/
