dataSource {
    pooled = true
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
    development {
        dataSource {
		  		driverClassName = "org.postgresql.Driver"
				username = "voter_management"
				password = ".,password.\$"
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:postgresql://127.0.0.1:5432/voter-management-dev"
        }
    }
    test {
        dataSource {
    			driverClassName = "org.hsqldb.jdbcDriver"
    			username = "sa"
    			password = ""
            dbCreate = "create-drop"
            url = "jdbc:hsqldb:mem:testDb"
        }
    }
    production {
        dataSource {
    			driverClassName = "org.hsqldb.jdbcDriver"
    			username = "sa"
    			password = ""
            dbCreate = "update"
            url = "jdbc:hsqldb:file:prodDb;shutdown=true"
        }
    }
}
