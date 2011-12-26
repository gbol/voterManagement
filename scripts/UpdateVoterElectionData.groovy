import groovy.sql.Sql

def db = Sql.newInstance("jdbc:postgresql://127.0.0.1/voter-management", 
	"voter_management", ".,password.\$", "org.postgresql.Driver")

def results = db.rows("select id,pledge_id from voter")

println "Results: "
results.each{result->
	println result
	db.execute "UPDATE voter_election SET pledge_id = ${result.pledge_id} where voter_id=${result.id}"
}

println "\nModified ${results.size()} records\n"


